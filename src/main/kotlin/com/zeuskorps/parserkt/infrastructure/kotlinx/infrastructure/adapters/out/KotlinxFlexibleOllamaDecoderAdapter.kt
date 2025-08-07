package com.zeuskorps.parserkt.infrastructure.kotlinx.infrastructure.adapters.out

import com.zeuskorps.parserkt.application.dto.FlashcardDto
import com.zeuskorps.parserkt.infrastructure.kotlinx.domain.entities.SerializableFlashcard
import com.zeuskorps.parserkt.infrastructure.kotlinx.domain.entities.toDto
import com.zeuskorps.parserkt.infrastructure.ktor.application.valueobjects.OllamaDecodingException
import com.zeuskorps.parserkt.infrastructure.ollama.application.ports.out.OllamaResponseDecoderProviderPort
import com.zeuskorps.parserkt.infrastructure.ollama.application.ports.out.OllamaStreamingDecoderPort
import com.zeuskorps.parserkt.infrastructure.ollama.domain.valueobjects.OllamaStreamingModeConfig
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class KotlinxFlexibleOllamaDecoderAdapter(
    private val defaultMode: OllamaStreamingModeConfig = OllamaStreamingModeConfig.AUTODETECT
) : OllamaResponseDecoderProviderPort, OllamaStreamingDecoderPort {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun decode(raw: String): List<FlashcardDto> {
        println("🔍 Resposta bruta recebida (primeiros 500 chars): ${raw.take(500)}")

        return when (defaultMode) {
            is OllamaStreamingModeConfig.DISABLED -> runCatching { decodeNonStreaming(raw) }.getOrElse {
                println("⚠️ Falha no modo não-streaming: ${it.message}")
                decodeAutoDetect(raw)
            }
            is OllamaStreamingModeConfig.ENABLED -> runCatching { decodeStreaming(raw) }.getOrElse {
                println("⚠️ Falha no modo streaming: ${it.message}")
                decodeAutoDetect(raw)
            }
            is OllamaStreamingModeConfig.AUTODETECT -> decodeAutoDetect(raw)
        }
    }

    override suspend fun decodeStreaming(rawStreamingResponse: String): List<FlashcardDto> {
        println("🔄 Decodificando resposta streaming...")

        val contentBuilder = StringBuilder()
        val lines = rawStreamingResponse.lines()

        for (line in lines) {
            val trimmedLine = line.trim()
            if (trimmedLine.isEmpty()) continue

            try {
                val jsonObj = json.parseToJsonElement(trimmedLine).jsonObject
                val response = jsonObj["response"]?.jsonPrimitive?.content
                val isDone = jsonObj["done"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() == true

                if (!response.isNullOrEmpty()) {
                    contentBuilder.append(response)
                }

                if (isDone) {
                    break
                }
            } catch (e: Exception) {
                println("⚠️ Ignorando linha malformada no streaming: $trimmedLine")
                continue
            }
        }

        val finalContent = contentBuilder.toString().trim()
        println("📄 Conteúdo final extraído do streaming: $finalContent")

        return parseFlashcardsFromContent(finalContent)
    }

    override suspend fun decodeNonStreaming(rawJsonResponse: String): List<FlashcardDto> {
        println("📄 Decodificando resposta não-streaming...")

        return try {
            // Estratégia 1: Tentar como array JSON direto
            json.decodeFromString<List<SerializableFlashcard>>(rawJsonResponse.trim())
                .map { it.toDto() }
        } catch (arrayException: Exception) {
            println("⚠️ Não é um array JSON direto: ${arrayException.message}")

            try {
                // Estratégia 2: Tentar extrair de objeto Ollama único
                val jsonObj = json.parseToJsonElement(rawJsonResponse.trim()).jsonObject

                // 🔥 CORREÇÃO: Verificar diferentes campos possíveis
                val responseContent = jsonObj["response"]?.jsonPrimitive?.content
                    ?: jsonObj["content"]?.jsonPrimitive?.content
                    ?: jsonObj["message"]?.jsonPrimitive?.content
                    ?: throw OllamaDecodingException("Nenhum campo de resposta encontrado. Campos disponíveis: ${jsonObj.keys}")

                println("📄 Conteúdo extraído do campo response: $responseContent")
                parseFlashcardsFromContent(responseContent)

            } catch (objException: Exception) {
                println("⚠️ Não é um objeto Ollama válido: ${objException.message}")

                // Estratégia 3: Tentar extrair JSON de texto livre
                parseFlashcardsFromContent(rawJsonResponse.trim())
            }
        }
    }

    override suspend fun decodeAutoDetect(rawResponse: String): List<FlashcardDto> {
        println("🕵️ Auto-detectando formato da resposta...")

        if (rawResponse.isBlank()) {
            throw OllamaDecodingException("Resposta vazia do Ollama")
        }

        // Detectar se é streaming (múltiplas linhas com "done")
        val isStreaming = rawResponse.contains("\"done\":") && rawResponse.lines().size > 1

        return if (isStreaming) {
            println("🔄 Detectado: formato streaming")
            decodeStreaming(rawResponse)
        } else {
            println("📄 Detectado: formato não-streaming")
            decodeNonStreaming(rawResponse)
        }
    }

    private fun parseFlashcardsFromContent(content: String): List<FlashcardDto> {
        if (content.isBlank()) {
            throw OllamaDecodingException("Conteúdo vazio após extração")
        }

        println("🔍 Tentando parsear conteúdo: ${content.take(200)}...")

        // Estratégia 1: JSON array direto
        val jsonArrayPattern = Regex("""\[[\s\S]*?\]""")
        val arrayMatch = jsonArrayPattern.find(content)

        if (arrayMatch != null) {
            try {
                val flashcards = json.decodeFromString<List<SerializableFlashcard>>(arrayMatch.value)
                    .map { it.toDto() }
                println("✅ Sucesso com JSON array: ${flashcards.size} flashcards")
                return flashcards
            } catch (e: Exception) {
                println("⚠️ Falha ao parsear como array JSON: ${e.message}")
            }
        }

        // Estratégia 2: Múltiplos objetos JSON
        val objectPattern = Regex("""\{[^{}]*(?:\{[^{}]*\}[^{}]*)*\}""")
        val objects = objectPattern.findAll(content).toList()

        if (objects.isNotEmpty()) {
            val flashcards = mutableListOf<FlashcardDto>()
            for (objMatch in objects) {
                try {
                    val flashcard = json.decodeFromString<SerializableFlashcard>(objMatch.value)
                    flashcards.add(flashcard.toDto())
                    println("✅ Objeto JSON parseado com sucesso")
                } catch (e: Exception) {
                    println("⚠️ Ignorando objeto JSON malformado: ${objMatch.value.take(100)}...")
                }
            }

            if (flashcards.isNotEmpty()) {
                println("✅ Sucesso com múltiplos objetos: ${flashcards.size} flashcards")
                return flashcards
            }
        }

        // Estratégia 3: Procurar por JSON embutido em texto
        val embeddedJsonPattern = Regex("""```json\s*([\s\S]*?)\s*```""")
        val embeddedMatch = embeddedJsonPattern.find(content)

        if (embeddedMatch != null) {
            try {
                val jsonContent = embeddedMatch.groupValues[1].trim()
                println("🔍 JSON embutido encontrado: ${jsonContent.take(100)}...")
                return parseFlashcardsFromContent(jsonContent)
            } catch (e: Exception) {
                println("⚠️ JSON embutido malformado: ${e.message}")
            }
        }

        throw OllamaDecodingException(
            "Não foi possível extrair flashcards válidos do conteúdo.\n" +
                    "Conteúdo recebido: $content"
        )
    }
}
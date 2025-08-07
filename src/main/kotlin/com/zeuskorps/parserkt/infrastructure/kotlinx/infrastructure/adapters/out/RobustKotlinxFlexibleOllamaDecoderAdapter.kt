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

class RobustKotlinxFlexibleOllamaDecoderAdapter(
    private val defaultMode: OllamaStreamingModeConfig = OllamaStreamingModeConfig.AUTODETECT
) : OllamaResponseDecoderProviderPort, OllamaStreamingDecoderPort {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun decode(raw: String): List<FlashcardDto> {
        println("🔍 Resposta bruta recebida (primeiros 200 chars): ${raw.take(200)}")

        // 🔥 CORREÇÃO: Verificar se é uma resposta de erro ou incompleta
        if (raw.contains("\"done_reason\":\"stop\"") && raw.contains("\"response\":")) {
            val responseContent = extractResponseFromOllamaWrapper(raw)
            println("📄 Conteúdo da resposta extraído: ${responseContent.take(100)}")

            // Se a resposta está vazia ou incompleta, tentar recuperar
            if (responseContent.isBlank() || isIncompleteResponse(responseContent)) {
                println("⚠️ Resposta incompleta detectada. Tentando recuperar...")
                return handleIncompleteResponse(responseContent)
            }
        }

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

    private fun extractResponseFromOllamaWrapper(raw: String): String {
        return try {
            val jsonObj = json.parseToJsonElement(raw.trim()).jsonObject
            jsonObj["response"]?.jsonPrimitive?.content ?: ""
        } catch (e: Exception) {
            println("⚠️ Erro ao extrair response do wrapper Ollama: ${e.message}")
            ""
        }
    }

    private fun isIncompleteResponse(content: String): Boolean {
        val trimmed = content.trim()
        return when {
            trimmed.isEmpty() -> true
            trimmed == "{" -> true // Apenas chave de abertura
            trimmed == "[" -> true // Apenas colchete de abertura
            trimmed.count { it == '{' } != trimmed.count { it == '}' } -> true // JSON malformado
            trimmed.count { it == '[' } != trimmed.count { it == ']' } -> true // Array malformado
            else -> false
        }
    }

    private suspend fun handleIncompleteResponse(incompleteContent: String): List<FlashcardDto> {
        println("🛠️ Tentando recuperar de resposta incompleta: '$incompleteContent'")

        // Se é apenas uma chave de abertura, criar um flashcard vazio baseado no contexto
        if (incompleteContent.trim() in listOf("{", "[", "")) {
            println("⚠️ Resposta extremamente incompleta. Ollama pode estar sobrecarregado.")
            throw OllamaDecodingException(
                "Ollama retornou resposta incompleta: '$incompleteContent'. " +
                        "Possíveis causas: modelo sobrecarregado, timeout, ou sequências de parada problemáticas."
            )
        }

        // Tentar completar JSON malformado
        val completed = tryCompleteJson(incompleteContent)
        if (completed != null) {
            return parseFlashcardsFromContent(completed)
        }

        throw OllamaDecodingException("Não foi possível recuperar resposta incompleta: '$incompleteContent'")
    }

    private fun tryCompleteJson(incomplete: String): String? {
        return try {
            when {
                // Se começa com '[' mas não termina com ']'
                incomplete.trim().startsWith("[") && !incomplete.trim().endsWith("]") -> {
                    incomplete.trim() + "]"
                }
                // Se começa com '{' mas não termina com '}'
                incomplete.trim().startsWith("{") && !incomplete.trim().endsWith("}") -> {
                    incomplete.trim() + "}"
                }
                // Tentar envolver em array se parece com objeto isolado
                incomplete.trim().startsWith("{") && incomplete.trim().endsWith("}") -> {
                    "[$incomplete]"
                }
                else -> null
            }
        } catch (e: Exception) {
            null
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
        println("📄 Conteúdo final extraído do streaming: ${finalContent.take(100)}")

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
                // Estratégia 2: Extrair de wrapper Ollama
                val responseContent = extractResponseFromOllamaWrapper(rawJsonResponse)

                if (isIncompleteResponse(responseContent)) {
                    return handleIncompleteResponse(responseContent)
                }

                parseFlashcardsFromContent(responseContent)

            } catch (objException: Exception) {
                println("⚠️ Não é um objeto Ollama válido: ${objException.message}")
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

        println("🔍 Tentando parsear conteúdo: ${content.take(100)}...")

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

        // Estratégia 2: Tentar completar e parsear novamente
        val completed = tryCompleteJson(content)
        if (completed != null) {
            try {
                val flashcards = json.decodeFromString<List<SerializableFlashcard>>(completed)
                    .map { it.toDto() }
                println("✅ Sucesso com JSON completado: ${flashcards.size} flashcards")
                return flashcards
            } catch (e: Exception) {
                println("⚠️ Falha ao parsear JSON completado: ${e.message}")
            }
        }

        throw OllamaDecodingException(
            "Não foi possível extrair flashcards válidos do conteúdo.\n" +
                    "Conteúdo recebido: ${content.take(200)}\n" +
                    "SUGESTÃO: O modelo pode estar configurado com sequências de parada muito restritivas, " +
                    "ou pode estar sobrecarregado. Tente usar um modelo diferente ou ajustar as configurações."
        )
    }
}
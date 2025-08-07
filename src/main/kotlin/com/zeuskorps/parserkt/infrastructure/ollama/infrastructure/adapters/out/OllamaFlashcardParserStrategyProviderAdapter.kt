package com.zeuskorps.parserkt.infrastructure.ollama.infrastructure.adapters.out


import com.zeuskorps.parserkt.application.dto.FlashcardDto
import com.zeuskorps.parserkt.application.dto.HttpRequestDto
import com.zeuskorps.parserkt.application.ports.out.FlashcardParserStrategyProviderPort
import com.zeuskorps.parserkt.application.ports.out.HttpClientProviderPort
import com.zeuskorps.parserkt.application.valueobjects.HttpMethod
import com.zeuskorps.parserkt.infrastructure.ollama.application.ports.int.OllamaModelResolverPort
import com.zeuskorps.parserkt.infrastructure.ollama.application.ports.out.OllamaResponseDecoderProviderPort
import com.zeuskorps.parserkt.infrastructure.ollama.application.ports.out.OllamaSerializationProviderPort
import com.zeuskorps.parserkt.infrastructure.ollama.domain.valueobjects.FlexibleOllamaFlashcardPattern

class OllamaFlashcardParserStrategyProviderAdapter(
    private val pattern: FlexibleOllamaFlashcardPattern,
    private val client: HttpClientProviderPort,
    private val modelResolver: OllamaModelResolverPort,
    private val decoder: OllamaResponseDecoderProviderPort,
    private val serialization: OllamaSerializationProviderPort,
    private val fallbackModels: List<String> = listOf("qwen3:1.7b")
) : FlashcardParserStrategyProviderPort {

    override suspend fun parse(rawContent: String): List<FlashcardDto> {
        val prompt = pattern.formatPrompt(rawContent)
        val modelName = modelResolver.pickPreferredModel(fallbackModels)

        val request = HttpRequestDto(
            url = "http://localhost:11434/api/generate",
            method = HttpMethod.POST,
            headers = mapOf("Content-Type" to "application/json"),
            body = serialization.encode(modelName, prompt, stream = true)
        )

        val raw = client.execute(request)
        return decoder.decode(raw)
    }
}
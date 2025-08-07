package com.zeuskorps.parserkt.infrastructure.ollama.infrastructure.config.service

import com.zeuskorps.parserkt.application.ports.out.FlashcardParserStrategyProviderPort
import com.zeuskorps.parserkt.application.ports.out.HttpClientProviderPort
import com.zeuskorps.parserkt.infrastructure.ollama.application.ports.int.OllamaModelResolverPort
import com.zeuskorps.parserkt.infrastructure.ollama.application.ports.out.OllamaResponseDecoderProviderPort
import com.zeuskorps.parserkt.infrastructure.ollama.application.ports.out.OllamaSerializationProviderPort
import com.zeuskorps.parserkt.infrastructure.ollama.domain.valueobjects.FlexibleOllamaFlashcardPattern
import com.zeuskorps.parserkt.infrastructure.ollama.infrastructure.adapters.out.OllamaFlashcardParserStrategyProviderAdapter

object OllamaFlashcardParserStrategyConfig {

    fun provideOllamaFlashcardParserStrategyProvider(
        client: HttpClientProviderPort,
        modelResolver: OllamaModelResolverPort,
        decoder: OllamaResponseDecoderProviderPort,
        serialization: OllamaSerializationProviderPort,
        pattern: FlexibleOllamaFlashcardPattern = FlexibleOllamaFlashcardPattern.flexible,
        fallbackModels: List<String> = listOf("qwen3:1.7b")
    ): FlashcardParserStrategyProviderPort {
        return OllamaFlashcardParserStrategyProviderAdapter(
            pattern = pattern,
            client = client,
            modelResolver = modelResolver,
            decoder = decoder,
            serialization = serialization, // 🔥 AQUI É O QUE FALTAVA
            fallbackModels = fallbackModels
        )
    }
}

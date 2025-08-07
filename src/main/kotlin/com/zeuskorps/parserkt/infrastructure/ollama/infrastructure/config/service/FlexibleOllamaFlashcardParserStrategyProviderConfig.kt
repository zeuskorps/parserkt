package com.zeuskorps.parserkt.infrastructure.ollama.infrastructure.config.service

import com.zeuskorps.parserkt.infrastructure.ollama.domain.valueobjects.OllamaConfig
import com.zeuskorps.parserkt.infrastructure.ollama.domain.valueobjects.OllamaStreamingModeConfig
import com.zeuskorps.parserkt.infrastructure.ollama.infrastructure.adapters.out.FlexibleOllamaFlashcardParserStrategyProviderAdapter

object FlexibleOllamaFlashcardParserStrategyProviderConfig {

    // Para uso atual (não-streaming)
    fun createNonStreamingParser(): FlexibleOllamaFlashcardParserStrategyProviderAdapter {
        return provideFlexibleOllamaFlashcardParserStrategyHelper(
            OllamaConfig(streamingMode = OllamaStreamingModeConfig.DISABLED)
        )
    }

    // Para uso futuro (streaming)
    fun createStreamingParser(): FlexibleOllamaFlashcardParserStrategyProviderAdapter {
        return provideFlexibleOllamaFlashcardParserStrategyHelper(
            OllamaConfig(streamingMode = OllamaStreamingModeConfig.ENABLED)
        )
    }

    // Auto-detecta (padrão recomendado)
    fun createAutoDetectParser(): FlexibleOllamaFlashcardParserStrategyProviderAdapter {
        return provideFlexibleOllamaFlashcardParserStrategyHelper(
            OllamaConfig(streamingMode = OllamaStreamingModeConfig.AUTODETECT)
        )
    }

    // Para desenvolvimento/debug com configuração personalizada
    fun createCustomParser(
        streamingMode: OllamaStreamingModeConfig,
        temperature: Double = 0.1,
        maxTokens: Int? = null
    ): FlexibleOllamaFlashcardParserStrategyProviderAdapter {
        return provideFlexibleOllamaFlashcardParserStrategyHelper(
            OllamaConfig(
                streamingMode = streamingMode,
                temperature = temperature,
                maxTokens = maxTokens
            )
        )
    }
}
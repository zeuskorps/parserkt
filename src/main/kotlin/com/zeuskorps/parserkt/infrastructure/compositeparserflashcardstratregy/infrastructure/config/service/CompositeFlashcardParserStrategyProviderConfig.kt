package com.zeuskorps.parserkt.infrastructure.compositeparserflashcardstratregy.infrastructure.config.service


import com.zeuskorps.parserkt.application.ports.out.FlashcardParserStrategyProviderPort
import com.zeuskorps.parserkt.infrastructure.compositeparserflashcardstratregy.infrastructure.adapters.out.CompositeFlashcardParserStrategyProviderAdapter
import com.zeuskorps.parserkt.infrastructure.ollama.infrastructure.config.service.FlexibleOllamaFlashcardParserStrategyProviderConfig
import com.zeuskorps.parserkt.infrastructure.regex.infrastructure.config.service.provideRegexFlashcardParserStrategyHelper

object CompositeFlashcardParserStrategyProviderConfig {

    fun provideCompositeParserStrategyProvider(): FlashcardParserStrategyProviderPort {
        val strategies = mapOf(
            "regex" to provideRegexFlashcardParserStrategyHelper(),
            "ollama" to FlexibleOllamaFlashcardParserStrategyProviderConfig.createAutoDetectParser()
        )

        return CompositeFlashcardParserStrategyProviderAdapter(
            strategies = strategies,
            defaultStrategy = "regex",
            enableFallback = true
        )
    }

    // Métodos adicionais para diferentes configurações
    fun provideCompositeWithStreamingOllama(): FlashcardParserStrategyProviderPort {
        val strategies = mapOf(
            "regex" to provideRegexFlashcardParserStrategyHelper(),
            "ollama" to FlexibleOllamaFlashcardParserStrategyProviderConfig.createStreamingParser()
        )

        return CompositeFlashcardParserStrategyProviderAdapter(
            strategies = strategies,
            defaultStrategy = "regex"
        )
    }

    fun provideCompositeWithNonStreamingOllama(): FlashcardParserStrategyProviderPort {
        val strategies = mapOf(
            "regex" to provideRegexFlashcardParserStrategyHelper(),
            "ollama" to FlexibleOllamaFlashcardParserStrategyProviderConfig.createNonStreamingParser()
        )

        return CompositeFlashcardParserStrategyProviderAdapter(
            strategies = strategies,
            defaultStrategy = "regex"
        )
    }
}
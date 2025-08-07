package com.zeuskorps.parserkt.infrastructure.regex.infrastructure.config.service

import com.zeuskorps.parserkt.application.ports.out.FlashcardParserStrategyProviderPort
import com.zeuskorps.parserkt.infrastructure.regex.domain.valueobjects.RegexFlashcardPattern
import com.zeuskorps.parserkt.infrastructure.regex.infrastructure.adapters.out.RegexFlashcardParserStrategyProviderAdapter

object RegexFlashcardParserStrategyConfig {
    fun provideDefaultStrategy(): FlashcardParserStrategyProviderPort {
        return RegexFlashcardParserStrategyProviderAdapter(RegexFlashcardPattern.default)
    }

    fun provideFromEnv(envVar: String = "FLASHCARD_PATTERN"): FlashcardParserStrategyProviderPort {
        return RegexFlashcardParserStrategyProviderAdapter(RegexFlashcardPattern.fromEnvOrDefault(envVar))
    }

    fun provideFromMap(configMap: Map<String, String>, name: String): FlashcardParserStrategyProviderPort {
        return RegexFlashcardParserStrategyProviderAdapter(RegexFlashcardPattern.fromConfigMap(name, configMap))
    }
}
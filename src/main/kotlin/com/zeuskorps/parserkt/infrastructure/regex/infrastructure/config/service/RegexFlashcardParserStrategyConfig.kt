package com.zeuskorps.parserkt.infrastructure.regex.infrastructure.config.service

import com.zeuskorps.parserkt.application.ports.out.FlashcardParserStrategyPort
import com.zeuskorps.parserkt.infrastructure.regex.domain.valueobjects.FlashcardPattern
import com.zeuskorps.parserkt.infrastructure.regex.infrastructure.adapters.out.RegexFlashcardParserStrategyAdapter

object RegexFlashcardParserStrategyConfig {
    fun provideDefaultStrategy(): FlashcardParserStrategyPort {
        return RegexFlashcardParserStrategyAdapter(FlashcardPattern.default)
    }

    fun provideFromEnv(envVar: String = "FLASHCARD_PATTERN"): FlashcardParserStrategyPort {
        return RegexFlashcardParserStrategyAdapter(FlashcardPattern.fromEnvOrDefault(envVar))
    }

    fun provideFromMap(configMap: Map<String, String>, name: String): FlashcardParserStrategyPort {
        return RegexFlashcardParserStrategyAdapter(FlashcardPattern.fromConfigMap(name, configMap))
    }
}

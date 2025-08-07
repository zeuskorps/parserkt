package com.zeuskorps.parserkt.infrastructure.regex.infrastructure.config.service

import com.zeuskorps.parserkt.infrastructure.regex.infrastructure.adapters.out.RegexFlashcardParserStrategyProviderAdapter
import com.zeuskorps.parserkt.infrastructure.regex.domain.valueobjects.RegexFlashcardPattern

fun provideRegexFlashcardParserStrategyHelper(): RegexFlashcardParserStrategyProviderAdapter {
    return RegexFlashcardParserStrategyProviderAdapter(RegexFlashcardPattern.default)
}

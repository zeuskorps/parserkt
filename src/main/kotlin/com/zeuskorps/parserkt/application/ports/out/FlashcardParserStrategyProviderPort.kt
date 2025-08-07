package com.zeuskorps.parserkt.application.ports.out

import com.zeuskorps.parserkt.application.dto.FlashcardDto

interface FlashcardParserStrategyProviderPort {
    suspend fun parse(rawContent: String): List<FlashcardDto>
}

package com.zeuskorps.parserkt.application.ports.out

import com.zeuskorps.parserkt.application.dto.FlashcardDto

interface FlashcardParserStrategyPort {
    fun parse(rawContent: String): List<FlashcardDto>
}

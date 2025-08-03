package com.zeuskorps.parserkt.application.dto

data class ParseFlashcardResponse(
    val validFlashcards: List<FlashcardDto>,
    val totalParsed: Int,
    val totalErrors: Int,
    val invalidBlocks: List<String>
)

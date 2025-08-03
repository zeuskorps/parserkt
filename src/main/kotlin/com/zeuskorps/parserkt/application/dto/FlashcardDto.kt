package com.zeuskorps.parserkt.application.dto

data class FlashcardDto(
    val universe: String,
    val question: String,
    val response: String,
    val example: String,
    val counterExample: String,
    val counterExampleCorrection: String,
    val challenge: String,
)

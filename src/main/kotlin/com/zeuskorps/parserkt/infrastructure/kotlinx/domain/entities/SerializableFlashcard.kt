package com.zeuskorps.parserkt.infrastructure.kotlinx.domain.entities

import com.zeuskorps.parserkt.application.dto.FlashcardDto
import kotlinx.serialization.Serializable

@Serializable
data class SerializableFlashcard(
    val universe: String,
    val question: String,
    val response: String,
    val example: String,
    val counterExample: String,
    val counterExampleCorrection: String,
    val challenge: String
)

fun FlashcardDto.toSerializable(): SerializableFlashcard =
    SerializableFlashcard(
        universe,
        question,
        response,
        example,
        counterExample,
        counterExampleCorrection,
        challenge
    )
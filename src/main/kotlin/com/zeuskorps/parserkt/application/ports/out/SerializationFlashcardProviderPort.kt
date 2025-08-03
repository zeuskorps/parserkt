package com.zeuskorps.parserkt.application.ports.out
import com.zeuskorps.parserkt.application.dto.FlashcardDto

interface SerializationFlashcardProviderPort {
    fun encodeFlashcardsToJson(flashcards: List<FlashcardDto>): String
}

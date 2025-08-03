package com.zeuskorps.parserkt.infrastructure.kotlinx.adapters.out

import com.zeuskorps.parserkt.application.dto.FlashcardDto
import com.zeuskorps.parserkt.application.ports.out.SerializationFlashcardProviderPort
import com.zeuskorps.parserkt.infrastructure.kotlinx.domain.entities.toSerializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class KotlinxFlashcardSerializationAdapter : SerializationFlashcardProviderPort {
    override fun encodeFlashcardsToJson(flashcards: List<FlashcardDto>): String {
        val serializable = flashcards.map { it.toSerializable() }
        return Json.encodeToString(serializable)
    }
}

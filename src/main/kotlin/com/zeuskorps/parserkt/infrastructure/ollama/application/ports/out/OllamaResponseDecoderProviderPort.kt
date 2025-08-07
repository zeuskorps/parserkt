package com.zeuskorps.parserkt.infrastructure.ollama.application.ports.out
import com.zeuskorps.parserkt.application.dto.FlashcardDto

interface OllamaResponseDecoderProviderPort {
    suspend fun decode(raw: String): List<FlashcardDto>
}

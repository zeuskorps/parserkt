package com.zeuskorps.parserkt.infrastructure.ollama.application.ports.out

import com.zeuskorps.parserkt.application.dto.FlashcardDto

interface OllamaStreamingDecoderPort {
    suspend fun decodeStreaming(rawStreamingResponse: String): List<FlashcardDto>
    suspend fun decodeNonStreaming(rawJsonResponse: String): List<FlashcardDto>
    suspend fun decodeAutoDetect(rawResponse: String): List<FlashcardDto>
}

package com.zeuskorps.parserkt.infrastructure.ktor.application.valueobjects
import com.zeuskorps.parserkt.application.dto.FlashcardDto
import com.zeuskorps.parserkt.infrastructure.kotlinx.domain.entities.SerializableFlashcard
import com.zeuskorps.parserkt.infrastructure.kotlinx.domain.entities.toDto
import com.zeuskorps.parserkt.infrastructure.ollama.application.ports.out.OllamaResponseDecoderProviderPort
import kotlinx.serialization.json.Json
// Exceção customizada para problemas de HTTP
class HttpClientException(message: String, cause: Throwable) : RuntimeException(message, cause)

// ================================================================================

// 2. KotlinxOllamaResponseDecoderAdapter - PROPAGAR exceção em vez de retornar lista vazia



// Exceção customizada para problemas de decodificação do Ollama
class OllamaDecodingException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

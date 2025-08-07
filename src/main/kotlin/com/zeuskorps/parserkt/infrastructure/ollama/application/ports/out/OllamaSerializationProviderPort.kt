package com.zeuskorps.parserkt.infrastructure.ollama.application.ports.out


interface OllamaSerializationProviderPort {
    suspend fun encode(model: String, prompt: String, stream: Boolean = false): String
}

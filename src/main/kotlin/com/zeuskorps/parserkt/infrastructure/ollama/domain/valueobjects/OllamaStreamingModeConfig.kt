package com.zeuskorps.parserkt.infrastructure.ollama.domain.valueobjects

sealed class OllamaStreamingModeConfig {
    object DISABLED : OllamaStreamingModeConfig()         // stream = false, resposta única
    object ENABLED   : OllamaStreamingModeConfig()      // stream = true, múltiplas mensagens
    object AUTODETECT   : OllamaStreamingModeConfig() // detecta automaticamente o formato da resposta

    // Futuras extensões:
  //  data class CUSTOM(val chunkSize: Int) : OllamaStreamingModeConfig()
  //  data class BATCH(val batchSize: Int) : OllamaStreamingModeConfig()
}
package com.zeuskorps.parserkt.infrastructure.ollama.domain.valueobjects

data class OllamaConfig(
    val streamingMode: OllamaStreamingModeConfig = OllamaStreamingModeConfig.AUTODETECT,
    val temperature: Double = 0.1,
    val topP: Double = 0.9,
    val maxTokens: Int? = 20000,
    // 🔥 FIX: Remove stop sequences that interfere with JSON generation
    val stopSequences: List<String> = emptyList(), // Was: listOf("\\n\\n", "---")
    val format: String = "json",
    val timeoutMs: Long = 600_000L
)

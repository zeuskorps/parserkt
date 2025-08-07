package com.zeuskorps.parserkt.infrastructure.ollama.application.ports.int


interface OllamaModelResolverPort {
suspend    fun listAvailableModels(): List<String>
suspend    fun isModelAvailable(modelName: String): Boolean
suspend    fun pickPreferredModel(fallbackOrder: List<String> = emptyList()): String
}

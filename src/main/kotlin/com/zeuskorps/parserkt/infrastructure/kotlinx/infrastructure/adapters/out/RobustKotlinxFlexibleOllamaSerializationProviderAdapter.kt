package com.zeuskorps.parserkt.infrastructure.kotlinx.infrastructure.adapters.out

import com.zeuskorps.parserkt.infrastructure.ollama.application.ports.out.OllamaSerializationProviderPort
import com.zeuskorps.parserkt.infrastructure.ollama.domain.valueobjects.OllamaConfig
import com.zeuskorps.parserkt.infrastructure.ollama.domain.valueobjects.OllamaStreamingModeConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
@Serializable
data class OllamaOptions(
    val temperature: Double = 0.1,
    val top_p: Double = 0.9,
    val num_predict: Int? = null,
    // 🔥 FIX: Only include stop sequences if they're not empty
    val stop: List<String>? = null // Use null instead of empty list
)

@Serializable
data class OllamaRequestPayload(
    val model: String,
    val prompt: String,
    val stream: Boolean,
    val format: String? = null,
    val options: OllamaOptions = OllamaOptions()
)

class RobustKotlinxFlexibleOllamaSerializationProviderAdapter(
    private val config: OllamaConfig = OllamaConfig()
) : OllamaSerializationProviderPort {

    override suspend fun encode(model: String, prompt: String, stream: Boolean): String {
        val shouldStream = when (config.streamingMode) {
            is OllamaStreamingModeConfig.DISABLED -> false
            is OllamaStreamingModeConfig.ENABLED -> true
            is OllamaStreamingModeConfig.AUTODETECT -> stream
        }

        // 🔥 FIX: Only include stop sequences if they're not empty
        val options = OllamaOptions(
            temperature = config.temperature,
            top_p = config.topP,
            num_predict = config.maxTokens,
            stop = if (config.stopSequences.isNotEmpty()) config.stopSequences else null
        )

        val payload = OllamaRequestPayload(
            model = model,
            prompt = prompt,
            stream = shouldStream,
            format = config.format,
            options = options
        )

        val jsonPayload = Json.encodeToString(payload)

        println("🔧 Improved Payload enviado para Ollama:")
        println(jsonPayload)
        println("=".repeat(50))

        return jsonPayload
    }
}
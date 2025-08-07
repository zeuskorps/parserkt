package com.zeuskorps.parserkt.infrastructure.kotlinx.infrastructure.adapters.out


import com.zeuskorps.parserkt.infrastructure.ollama.application.ports.out.OllamaSerializationProviderPort
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class SimpleOllamaOptions(
    val temperature: Double = 0.1,
    val top_p: Double = 0.9,
    val num_predict: Int = 2048, // 🔥 CORREÇÃO: Limite de tokens maior
    // 🔥 CORREÇÃO: Remover stop sequences que estão causando parada prematura
    // val stop: List<String> = emptyList() // Comentado para não incluir no JSON
)

@Serializable
data class SimpleOllamaRequestPayload(
    val model: String,
    val prompt: String,
    val stream: Boolean = false,
    val format: String = "json",
    val options: SimpleOllamaOptions = SimpleOllamaOptions()
)

class KotlinxOllamaSerializationProviderAdapter : OllamaSerializationProviderPort {

    override suspend fun encode(model: String, prompt: String, stream: Boolean): String {
        val payload = SimpleOllamaRequestPayload(
            model = model,
            prompt = prompt,
            stream = stream,
            format = "json",
            options = SimpleOllamaOptions(
                temperature = 0.1,
                top_p = 0.9,
                num_predict = 2048
                // 🔥 CORREÇÃO: Não incluir stop sequences
            )
        )

        val jsonPayload = Json.encodeToString(payload)

        println("🔧 Payload KotlinxOllamaSerializer (corrigido):")
        println(jsonPayload)
        println("=".repeat(50))

        return jsonPayload
    }
}
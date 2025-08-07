package com.zeuskorps.parserkt.infrastructure.ollama.application.usecase

import com.zeuskorps.parserkt.application.dto.HttpRequestDto
import com.zeuskorps.parserkt.application.ports.out.HttpClientProviderPort
import com.zeuskorps.parserkt.application.valueobjects.HttpMethod
import com.zeuskorps.parserkt.infrastructure.ollama.application.ports.int.OllamaModelResolverPort
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class OllamaModelResolverUseCase(
    private val client: HttpClientProviderPort
) : OllamaModelResolverPort {

    override suspend fun listAvailableModels(): List<String> {
        val request = HttpRequestDto(
            url = "http://localhost:11434/api/tags",
            method = HttpMethod.GET
        )

        val response = client.execute(request)

        return try {
            val parsed = Json.Default.parseToJsonElement(response).jsonObject
            val models = parsed["models"]?.jsonArray ?: return emptyList()
            models.mapNotNull { it.jsonObject["name"]?.jsonPrimitive?.content }
        } catch (e: Exception) {
            println("❌ Erro ao consultar modelos do Ollama: ${e.message}")
            emptyList()
        }
    }

    override suspend fun isModelAvailable(modelName: String): Boolean {
        return listAvailableModels().contains(modelName)
    }

    override suspend fun pickPreferredModel(fallbackOrder: List<String>): String {
        val available = listAvailableModels()
        return fallbackOrder.firstOrNull { it in available }
            ?: available.firstOrNull()
            ?: error("❌ Nenhum modelo disponível no Ollama.")
    }
}
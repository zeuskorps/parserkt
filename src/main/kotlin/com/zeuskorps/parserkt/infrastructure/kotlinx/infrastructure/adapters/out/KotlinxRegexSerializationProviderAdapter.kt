package com.zeuskorps.parserkt.infrastructure.kotlinx.infrastructure.adapters.out


import com.zeuskorps.parserkt.infrastructure.regex.application.ports.out.RegexSerializationProviderPort
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class KotlinxRegexSerializationProviderAdapter : RegexSerializationProviderPort {

    override fun decode(raw: String): Map<String, String> {
        val json = Json.parseToJsonElement(raw).jsonObject
        return json.mapValues { it.value.jsonPrimitive.content }
    }
    override fun supports(path: String): Boolean {
        return path.endsWith(".json", ignoreCase = true)
    }
}

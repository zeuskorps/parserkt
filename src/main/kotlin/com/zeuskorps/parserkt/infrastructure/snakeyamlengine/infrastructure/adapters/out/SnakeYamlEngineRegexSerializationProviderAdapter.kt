package com.zeuskorps.parserkt.infrastructure.snakeyamlengine.infrastructure.adapters.out
import com.zeuskorps.parserkt.infrastructure.regex.application.ports.out.RegexSerializationProviderPort
import org.snakeyaml.engine.v2.api.Load
import org.snakeyaml.engine.v2.api.LoadSettings

class SnakeYamlEngineRegexSerializationProviderAdapter : RegexSerializationProviderPort {
    override fun decode(raw: String): Map<String, String> {
        val yaml = Load(LoadSettings.builder().build()).loadFromString(raw)
        @Suppress("UNCHECKED_CAST")
        return (yaml as? Map<String, Any?>)
            ?.mapValues { it.value?.toString().orEmpty() }
            ?: emptyMap()
    }

    override fun supports(path: String): Boolean {
        return path.endsWith(".yaml") || path.endsWith(".yml")
    }
}
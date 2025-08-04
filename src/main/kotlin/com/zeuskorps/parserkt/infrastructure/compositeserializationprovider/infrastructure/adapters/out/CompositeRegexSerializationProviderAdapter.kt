package com.zeuskorps.parserkt.infrastructure.compositeserializationprovider.infrastructure.adapters.out

import com.zeuskorps.parserkt.infrastructure.regex.application.ports.out.RegexSerializationProviderPort

class CompositeRegexSerializationProviderAdapter(
    private val providers: List<RegexSerializationProviderPort>
) : RegexSerializationProviderPort {

    override fun decode(raw: String): Map<String, String> {
        val failures = mutableListOf<String>()

        for (provider in providers) {
            try {
                val result = provider.decode(raw)
                if (result.isNotEmpty()) return result
            } catch (e: Exception) {
                failures += "❌ ${provider.javaClass.simpleName}: ${e.message}"
            }
        }

        error("Nenhum provider conseguiu fazer decode. Falhas:\n" + failures.joinToString("\n"))
    }

    override fun supports(path: String): Boolean {
        return providers.any { it.supports(path) }
    }
}

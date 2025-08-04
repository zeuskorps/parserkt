package com.zeuskorps.parserkt.infrastructure.propertiesdot.infrastructure.adapters.out


import com.zeuskorps.parserkt.infrastructure.regex.application.ports.out.RegexSerializationProviderPort

class PropertiesDotRegexSerializationProviderAdapter : RegexSerializationProviderPort {

    override fun decode(raw: String): Map<String, String> {
        return raw
            .lineSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() && !it.startsWith("#") && !it.startsWith("!") }
            .mapNotNull { line ->
                val delimiterIndex = line.indexOfFirst { it == '=' || it == ':' }
                if (delimiterIndex <= 0) return@mapNotNull null

                val key = line.substring(0, delimiterIndex).trim()
                val value = line.substring(delimiterIndex + 1).trim()
                key to value
            }
            .toMap()
    }

    override fun supports(path: String): Boolean {
        return path.endsWith(".properties", ignoreCase = true)
    }
}

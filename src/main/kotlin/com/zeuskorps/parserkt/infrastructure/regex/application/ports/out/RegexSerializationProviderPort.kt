package com.zeuskorps.parserkt.infrastructure.regex.application.ports.out

interface RegexSerializationProviderPort {
    fun decode(raw: String): Map<String, String>
    fun supports(path: String): Boolean
}

package com.zeuskorps.parserkt.infrastructure.regex.application.ports.out

interface RegexPatternFileLoaderProviderPort {
    fun supports(filePath: String): Boolean
    fun load(filePath: String): Map<String, String>
}

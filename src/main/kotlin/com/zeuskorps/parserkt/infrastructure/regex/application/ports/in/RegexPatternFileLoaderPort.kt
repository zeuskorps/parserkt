package com.zeuskorps.parserkt.infrastructure.regex.application.ports.`in`
interface RegexPatternFileLoaderPort {
    fun load(): Map<String, String>
}

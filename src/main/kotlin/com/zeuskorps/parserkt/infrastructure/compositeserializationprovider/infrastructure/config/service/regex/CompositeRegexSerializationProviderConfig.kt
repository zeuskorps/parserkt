package com.zeuskorps.parserkt.infrastructure.compositeserializationprovider.infrastructure.config.service.regex

import com.zeuskorps.parserkt.infrastructure.regex.application.ports.out.RegexSerializationProviderPort
import com.zeuskorps.parserkt.infrastructure.compositeserializationprovider.infrastructure.adapters.out.CompositeRegexSerializationProviderAdapter
import com.zeuskorps.parserkt.infrastructure.kotlinx.infrastructure.adapters.out.KotlinxRegexSerializationProviderAdapter
import com.zeuskorps.parserkt.infrastructure.propertiesdot.infrastructure.adapters.out.PropertiesDotRegexSerializationProviderAdapter
import com.zeuskorps.parserkt.infrastructure.snakeyamlengine.infrastructure.adapters.out.SnakeYamlEngineRegexSerializationProviderAdapter

object CompositeRegexSerializationProviderConfig {

    fun raw(): List<RegexSerializationProviderPort> = listOf(
        KotlinxRegexSerializationProviderAdapter(),
        SnakeYamlEngineRegexSerializationProviderAdapter(),
        PropertiesDotRegexSerializationProviderAdapter()
    )

    fun provide(
        providers: List<RegexSerializationProviderPort>
    ): RegexSerializationProviderPort {
        return CompositeRegexSerializationProviderAdapter(providers)
    }
}

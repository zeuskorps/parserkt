package com.zeuskorps.parserkt.infrastructure.compositefileloaderprovider.infrastructure.config.service.regex


import com.zeuskorps.parserkt.infrastructure.compositefileloaderprovider.application.ports.out.CompositeRegexPatternFileLoaderProviderAdapter
import com.zeuskorps.parserkt.infrastructure.regex.application.ports.out.RegexPatternFileLoaderProviderPort

object CompositeRegexPatternFileLoaderProviderConfig {

    fun provide(
        providers: List<RegexPatternFileLoaderProviderPort>
    ): RegexPatternFileLoaderProviderPort {
        return CompositeRegexPatternFileLoaderProviderAdapter(providers)
    }
}

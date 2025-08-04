package com.zeuskorps.parserkt.infrastructure.compositefileloaderprovider.application.ports.out

import com.zeuskorps.parserkt.infrastructure.regex.application.ports.out.RegexPatternFileLoaderProviderPort

class CompositeRegexPatternFileLoaderProviderAdapter(
    private val providers: List<RegexPatternFileLoaderProviderPort>
) : RegexPatternFileLoaderProviderPort {

    override fun supports(filePath: String): Boolean =
        providers.any { it.supports(filePath) }

    override fun load(filePath: String): Map<String, String> =
        providers.firstOrNull { it.supports(filePath) }
            ?.load(filePath)
            ?: error("❌ Nenhum provider disponível para $filePath")
}

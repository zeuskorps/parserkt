package com.zeuskorps.parserkt.infrastructure.regex.application.usecases

import com.zeuskorps.parserkt.infrastructure.regex.application.ports.`in`.RegexPatternFileLoaderPort
import com.zeuskorps.parserkt.infrastructure.regex.application.ports.out.RegexPatternFileLoaderProviderPort
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.name

class RegexPatternFileLoaderUseCase(
    private val providers: List<RegexPatternFileLoaderProviderPort>,
    private val defaultPath: String
) : RegexPatternFileLoaderPort {

    override fun load(): Map<String, String> {
        val path = Path(defaultPath)

        check(path.exists()) { "❌ Arquivo não encontrado: $defaultPath" }

        val provider = providers.firstOrNull { it.supports(path.name) }
            ?: error("❌ Nenhum provider suporta o arquivo: ${path.name}")

        return provider.load(defaultPath)
    }
}

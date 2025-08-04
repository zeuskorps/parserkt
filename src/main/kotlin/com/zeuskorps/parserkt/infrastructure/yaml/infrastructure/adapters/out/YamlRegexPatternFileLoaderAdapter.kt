package com.zeuskorps.parserkt.infrastructure.yaml.infrastructure.adapters.out

import com.zeuskorps.parserkt.infrastructure.regex.application.ports.out.RegexPatternFileLoaderProviderPort
import com.zeuskorps.parserkt.infrastructure.regex.application.ports.out.RegexSerializationProviderPort
import kotlin.io.path.Path
import kotlin.io.path.readText

class YamlRegexPatternFileLoaderAdapter(
    private val serializationProvider: RegexSerializationProviderPort
) : RegexPatternFileLoaderProviderPort {

    override fun supports(filePath: String): Boolean =
        filePath.endsWith(".yml") || filePath.endsWith(".yaml")

    override fun load(filePath: String): Map<String, String> {
        val content = Path(filePath).readText()
        return serializationProvider.decode(content)
    }
}

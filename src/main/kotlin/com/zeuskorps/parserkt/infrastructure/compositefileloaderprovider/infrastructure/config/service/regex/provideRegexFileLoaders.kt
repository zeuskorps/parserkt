package com.zeuskorps.parserkt.infrastructure.compositefileloaderprovider.infrastructure.config.service.regex

import com.zeuskorps.parserkt.infrastructure.regex.application.ports.out.RegexPatternFileLoaderProviderPort
import com.zeuskorps.parserkt.infrastructure.json.infrastructure.adapters.out.JsonRegexPatternFileLoaderAdapter
import com.zeuskorps.parserkt.infrastructure.yaml.infrastructure.adapters.out.YamlRegexPatternFileLoaderAdapter
import com.zeuskorps.parserkt.infrastructure.propertiesdot.infrastructure.adapters.out.PropertiesDotRegexPatternFileLoaderAdapter
import com.zeuskorps.parserkt.infrastructure.regex.application.ports.out.RegexSerializationProviderPort

/**
 * Fornece uma lista de implementações de [RegexPatternFileLoaderProviderPort],
 * cada uma responsável por carregar arquivos de configuração de padrões regex
 * em formatos específicos (.json, .yaml/.yml, .properties).
 *
 * Os `RegexSerializationProviderPort` correspondentes são extraídos da lista com base
 * no suporte ao sufixo de arquivo.
 *
 * Esta função é compatível com bibliotecas de DI como Koin/Dagger.
 *
 * @param serializationProviders Lista de provedores de serialização, cada um com suporte a um formato.
 * @return Lista de [RegexPatternFileLoaderProviderPort] já instanciados e prontos para uso.
 *
 * @throws IllegalStateException se algum tipo de arquivo necessário não tiver um provider correspondente.
 */
fun provideRegexFileLoaders(
    serializationProviders: List<RegexSerializationProviderPort>
): List<RegexPatternFileLoaderProviderPort> {
    return listOf(
        JsonRegexPatternFileLoaderAdapter(
            serializationProviders.firstOrNull { it.supports(".json") }
                ?: error("❌ Nenhum provider de serialização suporta .json")
        ),
        YamlRegexPatternFileLoaderAdapter(
            serializationProviders.firstOrNull { it.supports(".yaml") || it.supports(".yml") }
                ?: error("❌ Nenhum provider de serialização suporta .yaml ou .yml")
        ),
        PropertiesDotRegexPatternFileLoaderAdapter(
            serializationProviders.firstOrNull { it.supports(".properties") }
                ?: error("❌ Nenhum provider de serialização suporta .properties")
        )
    )
}

package com.zeuskorps.parserkt.infrastructure.compositeserializationprovider.infrastructure.config.service.regex

import com.zeuskorps.parserkt.infrastructure.regex.application.ports.out.RegexSerializationProviderPort
import com.zeuskorps.parserkt.infrastructure.compositeserializationprovider.infrastructure.adapters.out.CompositeRegexSerializationProviderAdapter

/**
 * Fornece um `RegexSerializationProviderPort` composto a partir de uma lista de implementações individuais.
 *
 * Esta função segue o padrão de bibliotecas de injeção de dependência (DI) como Koin ou Dagger/Hilt,
 * onde a lista de implementações é injetada automaticamente.
 *
 * @param providers uma lista de implementações de `RegexSerializationProviderPort`,
 *                  cada uma responsável por suportar um determinado tipo de arquivo (ex: .json, .yaml, .properties).
 * @return uma instância de `CompositeRegexSerializationProviderAdapter` que tenta todas as implementações disponíveis.
 */
fun provideRegexSerializationProvider(
    providers: List<RegexSerializationProviderPort>
): RegexSerializationProviderPort = CompositeRegexSerializationProviderAdapter(providers)

package com.zeuskorps.parserkt.infrastructure.ollama.infrastructure.config.service

import com.zeuskorps.parserkt.application.ports.out.HttpClientProviderPort
import com.zeuskorps.parserkt.infrastructure.kotlinx.infrastructure.adapters.out.KotlinxOllamaSerializationProviderAdapter
import com.zeuskorps.parserkt.infrastructure.kotlinx.infrastructure.adapters.out.RobustKotlinxFlexibleOllamaDecoderAdapter
import com.zeuskorps.parserkt.infrastructure.ktor.infrastructure.adapters.out.KtorHttpClientProviderAdapter
import com.zeuskorps.parserkt.infrastructure.ollama.application.usecase.OllamaModelResolverUseCase
import com.zeuskorps.parserkt.infrastructure.ollama.infrastructure.adapters.out.OllamaFlashcardParserStrategyProviderAdapter
import io.ktor.client.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.HttpTimeout

fun provideOllamaFlashcardParserStrategyHelper(): OllamaFlashcardParserStrategyProviderAdapter {


    val ktorClient = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 600_000 // aguenta 10 minutos
            connectTimeoutMillis = 120_000
            socketTimeoutMillis = 600_000
        }

        engine {
            requestTimeout = 600_000 // redundante mas ok
            endpoint {
                connectTimeout = 120_000
                socketTimeout = 600_000
            }
        }
    }

    val httpAdapter: HttpClientProviderPort = KtorHttpClientProviderAdapter(ktorClient)
    val decoder = RobustKotlinxFlexibleOllamaDecoderAdapter()
    val serialization = KotlinxOllamaSerializationProviderAdapter()
    val modelResolver = OllamaModelResolverUseCase(httpAdapter)

    return OllamaFlashcardParserStrategyConfig.provideOllamaFlashcardParserStrategyProvider(
        client = httpAdapter,
        modelResolver = modelResolver,
        decoder = decoder,
        serialization = serialization,
    ) as OllamaFlashcardParserStrategyProviderAdapter
}
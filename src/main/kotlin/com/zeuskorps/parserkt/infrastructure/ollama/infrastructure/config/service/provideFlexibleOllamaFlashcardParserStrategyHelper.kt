package com.zeuskorps.parserkt.infrastructure.ollama.infrastructure.config.service


import com.zeuskorps.parserkt.application.ports.out.HttpClientProviderPort
import com.zeuskorps.parserkt.infrastructure.kotlinx.infrastructure.adapters.out.KotlinxFlexibleOllamaDecoderAdapter
import com.zeuskorps.parserkt.infrastructure.kotlinx.infrastructure.adapters.out.RobustKotlinxFlexibleOllamaSerializationProviderAdapter
import com.zeuskorps.parserkt.infrastructure.ktor.infrastructure.adapters.out.KtorHttpClientProviderAdapter
import com.zeuskorps.parserkt.infrastructure.ollama.application.usecase.OllamaModelResolverUseCase
import com.zeuskorps.parserkt.infrastructure.ollama.domain.valueobjects.FlexibleOllamaFlashcardPattern
import com.zeuskorps.parserkt.infrastructure.ollama.domain.valueobjects.OllamaConfig
import com.zeuskorps.parserkt.infrastructure.ollama.domain.valueobjects.OllamaStreamingModeConfig
import com.zeuskorps.parserkt.infrastructure.ollama.infrastructure.adapters.out.FlexibleOllamaFlashcardParserStrategyProviderAdapter
import io.ktor.client.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.HttpTimeout

fun provideFlexibleOllamaFlashcardParserStrategyHelper(
    config: OllamaConfig = OllamaConfig(streamingMode = OllamaStreamingModeConfig.AUTODETECT)
): FlexibleOllamaFlashcardParserStrategyProviderAdapter {

    val ktorClient = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = config.timeoutMs
            connectTimeoutMillis = 120_000
            socketTimeoutMillis = config.timeoutMs
        }

        engine {
            requestTimeout = config.timeoutMs
            endpoint {
                connectTimeout = 120_000
                socketTimeout = config.timeoutMs
            }
        }
    }

    val httpAdapter: HttpClientProviderPort = KtorHttpClientProviderAdapter(ktorClient)
    val decoder = KotlinxFlexibleOllamaDecoderAdapter(config.streamingMode)
    val serialization = RobustKotlinxFlexibleOllamaSerializationProviderAdapter(config)
    val modelResolver = OllamaModelResolverUseCase(httpAdapter)
    val pattern = FlexibleOllamaFlashcardPattern.flexible

    return FlexibleOllamaFlashcardParserStrategyProviderAdapter(
        pattern = pattern,
        client = httpAdapter,
        modelResolver = modelResolver,
        decoder = decoder,
        serialization = serialization,
        config = config,
        fallbackModels = listOf("qwen2.5:1.5b", "qwen2.5:0.5b", "llama3.2:1b", "qwen2:0.5b")
    )
}

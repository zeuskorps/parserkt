package com.zeuskorps.parserkt.infrastructure.ktor.infrastructure.adapters.out

import com.zeuskorps.parserkt.application.dto.HttpRequestDto
import com.zeuskorps.parserkt.application.ports.out.HttpClientProviderPort
import com.zeuskorps.parserkt.application.valueobjects.HttpMethod
import com.zeuskorps.parserkt.infrastructure.ktor.application.valueobjects.HttpClientException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*



class KtorHttpClientProviderAdapter(
    private val client: HttpClient
) : HttpClientProviderPort {

    override suspend fun execute(request: HttpRequestDto): String {
        return try {
            val response: HttpResponse = client.request {
                url(request.url)
                this.method = mapToKtorMethod(request.method)

                headers {
                    request.headers.forEach { (k, v) -> append(k, v) }
                }

                request.body?.let { requestBody ->
                    setBody(requestBody)
                }
            }

            response.body<String>()
        } catch (e: Exception) {
            // 🔥 MUDANÇA: Propagar exceção em vez de retornar string vazia
            throw HttpClientException("Erro ao executar request HTTP para ${request.url}", e)
        }
    }

    private fun mapToKtorMethod(method: HttpMethod): io.ktor.http.HttpMethod {
        return when (method) {
            is HttpMethod.GET -> io.ktor.http.HttpMethod.Get
            is HttpMethod.POST -> io.ktor.http.HttpMethod.Post
            is HttpMethod.PUT -> io.ktor.http.HttpMethod.Put
            is HttpMethod.DELETE -> io.ktor.http.HttpMethod.Delete
            is HttpMethod.PATCH -> io.ktor.http.HttpMethod.Patch
            is HttpMethod.Custom -> io.ktor.http.HttpMethod(method.customName)
        }
    }
}

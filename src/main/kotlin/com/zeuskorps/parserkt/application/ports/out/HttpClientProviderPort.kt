package com.zeuskorps.parserkt.application.ports.out

import com.zeuskorps.parserkt.application.dto.HttpRequestDto

interface HttpClientProviderPort {
    suspend fun execute(request: HttpRequestDto): String
}

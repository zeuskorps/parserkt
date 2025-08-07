package com.zeuskorps.parserkt.application.dto

import com.zeuskorps.parserkt.application.valueobjects.HttpMethod


data class HttpRequestDto(
    val url: String,
    val method: HttpMethod = HttpMethod.POST,
    val body: String? = null,
    val headers: Map<String, String> = emptyMap()
)


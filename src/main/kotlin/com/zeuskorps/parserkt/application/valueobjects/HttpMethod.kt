package com.zeuskorps.parserkt.application.valueobjects

sealed class HttpMethod(val name: String) {
    object GET : HttpMethod("GET")
    object POST : HttpMethod("POST")
    object PUT : HttpMethod("PUT")
    object DELETE : HttpMethod("DELETE")
    object PATCH : HttpMethod("PATCH")

    data class Custom(val customName: String) : HttpMethod(customName)
}
package com.zeuskorps.parserkt.application.ports.`in`

interface WriteApkgPort {
    fun exportApkg(outputPath: String, deckName: String = "Default")
}
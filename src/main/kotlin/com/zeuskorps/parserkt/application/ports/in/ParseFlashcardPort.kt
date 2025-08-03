package com.zeuskorps.parserkt.application.ports.`in`

import com.zeuskorps.parserkt.application.dto.ParseFlashcardResponse

interface ParseFlashcardPort {
    fun parse(filePath: String): ParseFlashcardResponse
}
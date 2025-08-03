package com.zeuskorps.parserkt.application.ports.out

import com.zeuskorps.parserkt.application.dto.FlashcardDto

interface FlashcardRepositoryPort {
    fun findAll(): List<FlashcardDto>
}
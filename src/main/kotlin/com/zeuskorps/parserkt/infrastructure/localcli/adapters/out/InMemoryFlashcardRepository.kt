package com.zeuskorps.parserkt.infrastructure.localcli.adapters.out

import com.zeuskorps.parserkt.application.dto.FlashcardDto
import com.zeuskorps.parserkt.application.ports.out.FlashcardRepositoryPort

class InMemoryFlashcardRepository : FlashcardRepositoryPort {
    private var flashcards: List<FlashcardDto> = emptyList()

    fun saveAll(newFlashcards: List<FlashcardDto>) {
        flashcards = newFlashcards
    }

    override fun findAll(): List<FlashcardDto> = flashcards
}

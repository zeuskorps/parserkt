package com.zeuskorps.parserkt.application.ports.out
import com.zeuskorps.parserkt.application.dto.FlashcardDto

interface ApkgProviderPort {
    fun generateApkg(flashcards: List<FlashcardDto>, outputPath: String, deckName: String = "Deck")
}

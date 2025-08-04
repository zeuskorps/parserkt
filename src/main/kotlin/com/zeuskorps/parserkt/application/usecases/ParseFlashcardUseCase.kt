package com.zeuskorps.parserkt.application.usecases

import com.zeuskorps.parserkt.application.dto.ParseFlashcardResponse
import com.zeuskorps.parserkt.application.ports.`in`.ParseFlashcardPort
import com.zeuskorps.parserkt.application.ports.out.FlashcardParserStrategyPort
import kotlin.io.path.Path
import kotlin.io.path.readText

class ParseFlashcardUseCase(
    private val parserStrategy: FlashcardParserStrategyPort
) : ParseFlashcardPort {

    override fun parse(filePath: String): ParseFlashcardResponse {
        val content = Path(filePath).readText()
        val flashcards = parserStrategy.parse(content)

        val totalMatches = Regex("### \\[\\d+]").findAll(content).count()
        val totalErrors = totalMatches - flashcards.size

        return ParseFlashcardResponse(
            validFlashcards = flashcards,
            totalParsed = flashcards.size,
            totalErrors = totalErrors,
            invalidBlocks = if (totalErrors > 0)
                listOf("⚠️ Alguns flashcards mal formatados foram ignorados.")
            else emptyList()
        )
    }
}

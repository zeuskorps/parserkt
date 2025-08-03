package com.zeuskorps.parserkt.application.usecases

import com.zeuskorps.parserkt.application.ports.`in`.WriteCsvPort
import com.zeuskorps.parserkt.application.ports.out.FlashcardRepositoryPort
import kotlin.io.path.Path
import kotlin.io.path.writeText

class WriteCsvUseCase(
    private val flashcardRepositoryPort: FlashcardRepositoryPort
) : WriteCsvPort {

    override fun exportToCsv(filePath: String) {
        val flashcards = flashcardRepositoryPort.findAll()

        val csvContent = buildString {
            appendLine("universo,pergunta,resposta,exemplo,contraexemplo,correcao_contraexemplo,desafio")
            flashcards.forEach { card ->
                appendLine(
                    listOf(
                        card.universe,
                        card.question,
                        card.response,
                        card.example,
                        card.counterExample,
                        card.counterExampleCorrection,
                        card.challenge
                    ).joinToString(",") { field ->
                        "\"" + field.replace("\"", "\"\"") + "\"" // escapa aspas duplas
                    }
                )
            }
        }

        Path(filePath).writeText(csvContent)

        println("📁 CSV exportado com sucesso para: $filePath")
    }
}

package com.zeuskorps.parserkt.application.usecases

import com.zeuskorps.parserkt.application.ports.`in`.WriteTsvPort
import com.zeuskorps.parserkt.application.ports.out.FlashcardRepositoryPort
import kotlin.io.path.Path
import kotlin.io.path.writeText

class WriteTsvUseCase(
    private val flashcardRepositoryPort: FlashcardRepositoryPort
) : WriteTsvPort {

    override fun write(filePath: String) {
        val flashcards = flashcardRepositoryPort.findAll()

        val tsvContent = buildString {
            appendLine("universo\tpergunta\tresposta\texemplo\tcontraexemplo\tcorrecao_contraexemplo\tdesafio")
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
                    ).joinToString("\t") { field ->
                        field
                            .replace("\t", "    ")        // evita quebra de coluna
                            .replace("\n", "\\n")         // evita quebra de linha
                            .replace("\"", "\"\"")        // se o Anki TSV precisar escapamento
                    }
                )
            }
        }

        Path(filePath).writeText(tsvContent)
        println("📁 TSV exportado com sucesso para: $filePath")
    }
}
package com.zeuskorps.parserkt.application.usecases

import com.zeuskorps.parserkt.application.dto.FlashcardDto
import com.zeuskorps.parserkt.application.dto.ParseFlashcardResponse
import com.zeuskorps.parserkt.application.ports.`in`.ParseFlashcardPort
import kotlin.io.path.Path
import kotlin.io.path.readText

class ParseFlashcardUseCase : ParseFlashcardPort {

    override fun parse(filePath: String): ParseFlashcardResponse {
        val content = Path(filePath).readText()

        val regex = Regex(
            """### \[\d+]\s*\*\*\[Universo]\*\*\s*(.*?)\s*\*\*\[Pergunta]\*\*\s*(.*?)\s*\*\*\[Resposta]\*\*\s*(.*?)\s*\*\*\[Exemplo]\*\*\s*(.*?)\s*\*\*\[Contraexemplo]\*\*\s*(.*?)\s*\*\*\[Correção do Contraexemplo]\*\*\s*(.*?)\s*\*\*\[Desafio]\*\*\s*(.*?)(?=\n### \[\d+]|$)""",
            RegexOption.DOT_MATCHES_ALL
        )


        val matches = regex.findAll(content)

        val flashcards = matches.map {
            val (
                universe,
                question,
                response,
                example,
                counterExample,
                counterExampleCorrection,
                challenge,
            ) = it.destructured

            FlashcardDto(
                universe = universe.trim(),
                question = question.trim(),
                response = response.trim(),
                example = example.trim(),
                counterExample = counterExample.trim(),
                counterExampleCorrection = counterExampleCorrection.trim(),
                challenge = challenge.trim()
            )
        }.toList()

        val totalMatches = regex.findAll(content).count()
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
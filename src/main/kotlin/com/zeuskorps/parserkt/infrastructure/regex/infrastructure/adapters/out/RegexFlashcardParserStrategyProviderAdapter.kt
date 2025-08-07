package com.zeuskorps.parserkt.infrastructure.regex.infrastructure.adapters.out

import com.zeuskorps.parserkt.application.dto.FlashcardDto
import com.zeuskorps.parserkt.application.ports.out.FlashcardParserStrategyProviderPort
import com.zeuskorps.parserkt.infrastructure.regex.domain.valueobjects.RegexFlashcardPattern

class RegexFlashcardParserStrategyProviderAdapter(
    private val pattern: RegexFlashcardPattern
) : FlashcardParserStrategyProviderPort {

    override suspend fun parse(rawContent: String): List<FlashcardDto> {
        return pattern.regex.findAll(rawContent).map {
            val groups = it.groupValues.drop(1).map(String::trim)

            when (groups.size) {
                2 -> FlashcardDto(
                    universe = "",
                    question = groups[0],
                    response = groups[1],
                    example = "",
                    counterExample = "",
                    counterExampleCorrection = "",
                    challenge = ""
                )
                7 -> FlashcardDto(
                    universe = groups[0],
                    question = groups[1],
                    response = groups[2],
                    example = groups[3],
                    counterExample = groups[4],
                    counterExampleCorrection = groups[5],
                    challenge = groups[6]
                )
                else -> {
                    println("⚠️ Grupo de regex inesperado: ${groups.size} campos encontrados.")
                    FlashcardDto(
                        universe = groups.getOrNull(0).orEmpty(),
                        question = groups.getOrNull(1).orEmpty(),
                        response = groups.getOrNull(2).orEmpty(),
                        example = groups.getOrNull(3).orEmpty(),
                        counterExample = groups.getOrNull(4).orEmpty(),
                        counterExampleCorrection = groups.getOrNull(5).orEmpty(),
                        challenge = groups.getOrNull(6).orEmpty()
                    )
                }
            }
        }.toList()
    }
}
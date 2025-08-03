package com.zeuskorps.parserkt.domain.entities
import com.zeuskorps.parserkt.domain.valueobjects.*

data class Flashcard(
    val universe: Universe,
    val question: Question,
    val response: Response,
    val example: Example,
    val counterExample: CounterExample,
    val counterExampleCorrection: CounterExampleCorrection,
    val challenge: Challenge,
)
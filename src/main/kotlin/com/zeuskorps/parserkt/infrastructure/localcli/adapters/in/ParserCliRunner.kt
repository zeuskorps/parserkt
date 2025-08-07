package com.zeuskorps.parserkt.infrastructure.localcli.adapters.`in`

import com.zeuskorps.parserkt.application.dto.FlashcardDto
import com.zeuskorps.parserkt.application.ports.`in`.ParseFlashcardPort
import com.zeuskorps.parserkt.application.ports.`in`.WriteCsvPort
import com.zeuskorps.parserkt.application.ports.`in`.WriteTsvPort
import com.zeuskorps.parserkt.application.ports.out.ApkgProviderPort
import com.zeuskorps.parserkt.infrastructure.localcli.adapters.out.InMemoryFlashcardRepository

class ParserCliRunner(
    private val parserFlashcardPort: ParseFlashcardPort,
    private val writeCsvPort: WriteCsvPort,
    private val writeTsvPort: WriteTsvPort,
    private val apkgProviderPort: ApkgProviderPort,
    private val flashcardRepositoryPort: InMemoryFlashcardRepository
) {
   suspend fun run(markdownPath: String?) {
        val filePath = markdownPath?.takeIf { it.isNotBlank() } ?: prompt("📄 Digite o caminho do arquivo .md:")
        if (filePath.isNullOrBlank()) {
            println("❌ Nenhum caminho fornecido.")
            return
        }

        val response = parserFlashcardPort.parse(filePath)
        flashcardRepositoryPort.saveAll(response.validFlashcards)

        println("✅ ${response.totalParsed} flashcards parseados.")
        if (response.validFlashcards.isEmpty()) return

        val flashcards = flashcardRepositoryPort.findAll()
        var index = 0

        loop@ while (true) {
            printMenu()

            val input = readLine()?.trim()?.lowercase() ?: run {
                println("👋 Entrada encerrada (EOF). Saindo...")
                break@loop
            }

            when (input) {
                "n" -> if (index < flashcards.lastIndex) show(flashcards[++index], index, flashcards.size)
                "p" -> if (index > 0) show(flashcards[--index], index, flashcards.size)
                "v" -> flashcards.forEachIndexed { i, fc -> show(fc, i, flashcards.size) }
                "x" -> prompt("📥 CSV path:")?.takeIf { it.isNotBlank() }?.let { writeCsvPort.exportToCsv(it) }
                "t" -> prompt("📥 TSV path:")?.takeIf { it.isNotBlank() }?.let { writeTsvPort.write(it) }
                "a" -> {
                    val path = prompt("📥 APKG path:").orEmpty()
                    val deckName = prompt("📝 Nome do deck:").orEmpty()
                    if (path.isNotBlank() && deckName.isNotBlank()) {
                        apkgProviderPort.generateApkg(flashcards, path, deckName)
                    }
                }
                "q" -> {
                    println("👋 Saindo...")
                    break@loop
                }
                else -> println("❓ Comando inválido.")
            }
        }
    }

    private fun prompt(message: String): String? {
        print("$message ")
        return readLine()
    }

    private fun printMenu() {
        println(
            """
            |Comandos:
            | [n] Próximo
            | [p] Anterior
            | [v] Ver todos
            | [x] Exportar CSV
            | [t] Exportar TSV
            | [a] Exportar APKG
            | [q] Sair
            |> 
            """.trimMargin()
        )
    }

    private fun show(fc: FlashcardDto, index: Int, total: Int) {
        println(
            """
            |
            |🧠 Flashcard ${index + 1} de $total
            |Universo: ${fc.universe}
            |Pergunta: ${fc.question}
            |Resposta: ${fc.response}
            |Exemplo: ${fc.example}
            |Contraexemplo: ${fc.counterExample}
            |Correção: ${fc.counterExampleCorrection}
            |Desafio: ${fc.challenge}
            |${"-".repeat(60)}
            """.trimMargin()
        )
    }
}

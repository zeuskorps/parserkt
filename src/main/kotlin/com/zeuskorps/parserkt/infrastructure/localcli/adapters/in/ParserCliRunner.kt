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
    fun run(args: Array<String>) {
        val filePath = if (args.isNotEmpty()) args[0] else {
            print("📄 Digite o caminho do arquivo .md: ")
            readln().trim()
        }

        if (filePath.isBlank()) {
            println("❌ Nenhum caminho fornecido.")
            return
        }

        val response = parserFlashcardPort.parse(filePath)
        flashcardRepositoryPort.saveAll(response.validFlashcards)

        println("✅ ${response.totalParsed} flashcards parseados.")
        if (response.validFlashcards.isEmpty()) return

        var index = 0
        val flashcards = flashcardRepositoryPort.findAll()

        while (true) {
            print(
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

            when (readln().trim().lowercase()) {
                "n" -> if (index < flashcards.lastIndex) show(flashcards[++index], index, flashcards.size)
                "p" -> if (index > 0) show(flashcards[--index], index, flashcards.size)
                "v" -> flashcards.forEachIndexed { i, fc -> show(fc, i, flashcards.size) }
                "x" -> {
                    print("📥 CSV path: ")
                    readln().takeIf { it.isNotBlank() }?.let { writeCsvPort.exportToCsv(it) }
                }
                "t" -> {
                    print("📥 TSV path: ")
                    readln().takeIf { it.isNotBlank() }?.let { writeTsvPort.write(it) }
                }
                "a" -> {
                    print("📥 APKG path: ")
                    val path = readln().trim()
                    print("📝 Nome do deck: ")
                    val deckName = readln().trim()
                    if (path.isNotBlank() && deckName.isNotBlank())
                        apkgProviderPort.generateApkg(flashcards, path, deckName)
                }
                "q" -> {
                    println("👋 Saindo...")
                    break
                }
                else -> println("❓ Comando inválido.")
            }
        }
    }

    private fun show(fc: FlashcardDto, index: Int, total: Int) {
        println(
            """
            |🧠 Flashcard ${index + 1} de $total
            |Universo: ${fc.universe}
            |Pergunta: ${fc.question}
            |Resposta: ${fc.response}
            |Exemplo: ${fc.example}
            |Contraexemplo: ${fc.counterExample}
            |Correção: ${fc.counterExampleCorrection}
            |Desafio: ${fc.challenge}
            |
            |${"-".repeat(60)}
            """.trimMargin()
        )
    }
}

// infrastructure/python_apkg_adapter/infrastructure/adapter/out/PythonApkgProviderAdapter.kt
package com.zeuskorps.parserkt.infrastructure.python_apkg_adapter.infrastructure.adapter.out

import com.zeuskorps.parserkt.application.dto.FlashcardDto
import com.zeuskorps.parserkt.application.ports.out.ApkgProviderPort
import com.zeuskorps.parserkt.application.ports.out.SerializationFlashcardProviderPort
import com.zeuskorps.parserkt.infrastructure.python_apkg_adapter.application.ports.out.PythonEnvironmentProviderPort
import kotlin.io.path.createTempFile
import kotlin.io.path.writeText

class PythonApkgProviderAdapter(
    private val serializer: SerializationFlashcardProviderPort,
    private val envProvider: PythonEnvironmentProviderPort
) : ApkgProviderPort {

    override fun generateApkg(flashcards: List<FlashcardDto>, outputPath: String, deckName: String) {
        val json = serializer.encodeFlashcardsToJson(flashcards)
        val tempJsonPath = createTempFile("flashcards", ".json")
        tempJsonPath.writeText(json)

        val requirementsPath =
            "src/main/kotlin/com/zeuskorps/parserkt/infrastructure/python_apkg_adapter/infrastructure/config/requirements/requirements.txt"
        val scriptPath =
            "src/main/kotlin/com/zeuskorps/parserkt/infrastructure/python_apkg_adapter/infrastructure/config/service/generate_apkg.py"

        val env = envProvider.setupEnvironment(requirementsPath)

        println("🧠 Gerando .apkg com ambiente isolado...")
        val exit = ProcessBuilder(
            env.pythonExecutable,
            scriptPath,
            tempJsonPath.toString(),
            outputPath,
            deckName
        ).inheritIO().start().waitFor()

        check(exit == 0) { "Erro ao gerar .apkg (exit=$exit)" }
    }
}

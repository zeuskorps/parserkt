package com.zeuskorps.parserkt.application.usecases

import com.zeuskorps.parserkt.application.ports.`in`.WriteApkgPort
import com.zeuskorps.parserkt.application.ports.out.ApkgProviderPort
import com.zeuskorps.parserkt.application.ports.out.FlashcardRepositoryPort

class WriteApkgUseCase(
    private val flashcardRepository: FlashcardRepositoryPort,
    private val apkgProvider: ApkgProviderPort
) : WriteApkgPort {

    override fun exportApkg(outputPath: String, deckName: String) {
        val flashcards = flashcardRepository.findAll()
        apkgProvider.generateApkg(flashcards, outputPath, deckName)
    }
}

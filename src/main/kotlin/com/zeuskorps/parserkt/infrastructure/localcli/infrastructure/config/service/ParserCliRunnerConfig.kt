package com.zeuskorps.parserkt.infrastructure.localcli.infrastructure.config.service

import com.zeuskorps.parserkt.application.ports.`in`.ParseFlashcardPort
import com.zeuskorps.parserkt.application.ports.`in`.WriteCsvPort
import com.zeuskorps.parserkt.application.ports.`in`.WriteTsvPort
import com.zeuskorps.parserkt.application.ports.out.ApkgProviderPort
import com.zeuskorps.parserkt.infrastructure.localcli.adapters.`in`.ParserCliRunner
import com.zeuskorps.parserkt.infrastructure.localcli.adapters.out.InMemoryFlashcardRepository
import com.zeuskorps.parserkt.infrastructure.python_apkg_adapter.application.ports.out.PythonEnvironmentProviderPort

class ParserCliRunnerConfig(
) {
    fun provideParserCliRunner(parserFlashcardPort: ParseFlashcardPort,
                                writeCsvPort: WriteCsvPort,
                                writeTsvPort: WriteTsvPort,
                                apkgProviderPort: ApkgProviderPort,
                                flashcardRepositoryPort: InMemoryFlashcardRepository): ParserCliRunner {
        return ParserCliRunner(
            parserFlashcardPort = parserFlashcardPort,
            writeCsvPort = writeCsvPort,
            writeTsvPort = writeTsvPort,
            apkgProviderPort = apkgProviderPort,
            flashcardRepositoryPort = flashcardRepositoryPort
        )
    }
}


package com.zeuskorps.parserkt.infrastructure.localcli.infrastructure.config.service

import com.zeuskorps.parserkt.application.usecases.ParseFlashcardUseCase
import com.zeuskorps.parserkt.application.usecases.WriteCsvUseCase
import com.zeuskorps.parserkt.application.usecases.WriteTsvUseCase
import com.zeuskorps.parserkt.infrastructure.archso.adapters.out.ArchPythonVenvAdapter
import com.zeuskorps.parserkt.infrastructure.kotlinx.adapters.out.KotlinxFlashcardSerializationAdapter
import com.zeuskorps.parserkt.infrastructure.localcli.adapters.`in`.ParserCliRunner
import com.zeuskorps.parserkt.infrastructure.localcli.adapters.out.InMemoryFlashcardRepository
import com.zeuskorps.parserkt.infrastructure.python_apkg_adapter.infrastructure.adapter.out.PythonApkgProviderAdapter

fun provideParserCliRunner(): ParserCliRunner {
    val parser = ParseFlashcardUseCase()
    val repository = InMemoryFlashcardRepository()
    val csv = WriteCsvUseCase(repository)
    val tsv = WriteTsvUseCase(repository)
    val serializer = KotlinxFlashcardSerializationAdapter()
    val pythonEnv = ArchPythonVenvAdapter()
    val apkg = PythonApkgProviderAdapter(serializer, pythonEnv)

    return ParserCliRunnerConfig().provideParserCliRunner(
        parser, csv,tsv,apkg,repository
    )
}

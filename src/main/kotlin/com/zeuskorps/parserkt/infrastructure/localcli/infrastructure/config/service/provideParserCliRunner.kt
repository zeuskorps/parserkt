package com.zeuskorps.parserkt.infrastructure.localcli.infrastructure.config.service

import com.zeuskorps.parserkt.application.usecases.ParseFlashcardUseCase
import com.zeuskorps.parserkt.application.usecases.WriteCsvUseCase
import com.zeuskorps.parserkt.application.usecases.WriteTsvUseCase
import com.zeuskorps.parserkt.infrastructure.archso.adapters.out.ArchPythonVenvAdapter
import com.zeuskorps.parserkt.infrastructure.compositefileloaderprovider.infrastructure.config.service.regex.provideRegexFileLoaders
import com.zeuskorps.parserkt.infrastructure.compositeserializationprovider.infrastructure.config.service.regex.CompositeRegexSerializationProviderConfig
import com.zeuskorps.parserkt.infrastructure.kotlinx.infrastructure.adapters.out.KotlinxFlashcardSerializationProviderAdapter
import com.zeuskorps.parserkt.infrastructure.localcli.adapters.`in`.ParserCliRunner
import com.zeuskorps.parserkt.infrastructure.localcli.adapters.out.InMemoryFlashcardRepository
import com.zeuskorps.parserkt.infrastructure.python_apkg_adapter.infrastructure.adapter.out.PythonApkgProviderAdapter
import com.zeuskorps.parserkt.infrastructure.regex.application.usecases.RegexPatternFileLoaderUseCase
import com.zeuskorps.parserkt.infrastructure.regex.domain.valueobjects.FlashcardPattern
import com.zeuskorps.parserkt.infrastructure.regex.infrastructure.adapters.out.RegexFlashcardParserStrategyAdapter

fun provideParserCliRunner(patternPath: String?): ParserCliRunner {
    val serializationProviders = CompositeRegexSerializationProviderConfig.raw()
    val compositeSerialization = CompositeRegexSerializationProviderConfig.provide(serializationProviders)
    val fileLoaders = provideRegexFileLoaders(serializationProviders)

    val pattern = if (!patternPath.isNullOrBlank()) {
        val regexUseCase = RegexPatternFileLoaderUseCase(
            providers = fileLoaders,
            defaultPath = patternPath
        )
        val config = regexUseCase.load()
        FlashcardPattern.fromConfigMap("flashcard", config)
    } else {
        FlashcardPattern.default
    }

    val parserStrategy = RegexFlashcardParserStrategyAdapter(pattern)
    val parser = ParseFlashcardUseCase(parserStrategy)
    val repository = InMemoryFlashcardRepository()
    val csv = WriteCsvUseCase(repository)
    val tsv = WriteTsvUseCase(repository)
    val serializationForApkg = KotlinxFlashcardSerializationProviderAdapter()
    val pythonEnv = ArchPythonVenvAdapter()
    val apkg = PythonApkgProviderAdapter(serializationForApkg, pythonEnv)

    return ParserCliRunnerConfig().provideParserCliRunner(
        parserFlashcardPort = parser,
        writeCsvPort = csv,
        writeTsvPort = tsv,
        apkgProviderPort = apkg,
        flashcardRepositoryPort = repository
    )
}

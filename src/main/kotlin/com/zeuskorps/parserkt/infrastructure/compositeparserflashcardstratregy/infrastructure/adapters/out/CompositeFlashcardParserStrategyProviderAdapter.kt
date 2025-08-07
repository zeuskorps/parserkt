package com.zeuskorps.parserkt.infrastructure.compositeparserflashcardstratregy.infrastructure.adapters.out

import com.zeuskorps.parserkt.application.dto.FlashcardDto
import com.zeuskorps.parserkt.application.ports.out.FlashcardParserStrategyProviderPort

/**
 * Composite adapter que permite múltiplas estratégias de parsing de flashcards.
 *
 * Suporta fallback automático para a estratégia padrão (regex) em caso de falha,
 * e parsing de cabeçalhos de estratégia no formato ##parser=<nome>.
 *
 * @param strategies Mapa de estratégias disponíveis (chave = nome, valor = implementação)
 * @param defaultStrategy Nome da estratégia padrão a ser usada quando não especificada
 * @param enableFallback Se deve tentar a estratégia padrão em caso de falha
 */
class CompositeFlashcardParserStrategyProviderAdapter(
    private val strategies: Map<String, FlashcardParserStrategyProviderPort>,
    private val defaultStrategy: String = "regex",
    private val enableFallback: Boolean = true
) : FlashcardParserStrategyProviderPort {

    init {
        require(strategies.isNotEmpty()) {
            "❌ Pelo menos uma estratégia deve ser fornecida"
        }
        require(strategies.containsKey(defaultStrategy)) {
            "❌ Estratégia padrão '$defaultStrategy' não está registrada"
        }
    }

    override suspend fun parse(rawContent: String): List<FlashcardDto> {
        val (strategyName, content) = parseStrategyHeader(rawContent)

        println("🎯 Estratégia selecionada: '$strategyName'")
        println("📝 Tamanho do conteúdo: ${content.length} caracteres")

        val primaryStrategy = strategies[strategyName]
            ?: throw IllegalArgumentException("❌ Estratégia '$strategyName' não registrada. Disponíveis: ${strategies.keys}")

        return try {
            println("🚀 Executando estratégia '$strategyName'...")
            val result = primaryStrategy.parse(content)
            println("✅ Estratégia '$strategyName': ${result.size} flashcards parseados")
            result

        } catch (e: Exception) {
            println("❌ Estratégia '$strategyName' falhou: ${e.javaClass.simpleName} - ${e.message}")

            if (enableFallback && strategyName != defaultStrategy) {
                println("🔄 Tentando fallback para estratégia '$defaultStrategy'...")

                try {
                    val fallbackStrategy = strategies[defaultStrategy]!!
                    val fallbackResult = fallbackStrategy.parse(rawContent) // Usar rawContent original
                    println("✅ Fallback '$defaultStrategy': ${fallbackResult.size} flashcards parseados")
                    fallbackResult

                } catch (fallbackError: Exception) {
                    println("❌ Fallback '$defaultStrategy' também falhou: ${fallbackError.message}")
                    throw RuntimeException(
                        "Falhou estratégia '$strategyName' e fallback '$defaultStrategy'. " +
                                "Erro original: ${e.message}. Erro fallback: ${fallbackError.message}",
                        e
                    )
                }
            } else {
                throw e
            }
        }
    }

    private fun parseStrategyHeader(rawContent: String): Pair<String, String> {
        val lines = rawContent.trim().lines()
        val firstLine = lines.firstOrNull()?.trim() ?: ""

        return if (firstLine.startsWith("##parser=")) {
            val strategyName = firstLine.removePrefix("##parser=").lowercase()
            val contentWithoutHeader = lines.drop(1).joinToString("\n")
            strategyName to contentWithoutHeader
        } else {
            defaultStrategy to rawContent
        }
    }
}
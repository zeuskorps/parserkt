package com.zeuskorps.parserkt.infrastructure.regex.domain.valueobjects

data class FlashcardPattern(val rawPattern: String) {
    val regex: Regex

    init {
        try {
            regex = Regex(rawPattern, RegexOption.DOT_MATCHES_ALL)
        } catch (e: Exception) {
            throw IllegalArgumentException("❌ Padrão inválido: ${e.message}", e)
        }
    }

    companion object {

        val default: FlashcardPattern = FlashcardPattern(
            """
            ### \[\d+]\s*
            \*\*\[Universo]\*\*\s*(.*?)\s*
            \*\*\[Pergunta]\*\*\s*(.*?)\s*
            \*\*\[Resposta]\*\*\s*(.*?)\s*
            \*\*\[Exemplo]\*\*\s*(.*?)\s*
            \*\*\[Contraexemplo]\*\*\s*(.*?)\s*
            \*\*\[Correção do Contraexemplo]\*\*\s*(.*?)\s*
            \*\*\[Desafio]\*\*\s*(.*?)
            (?=\n### \[\d+]|$)
            """.trimIndent()
        )

        fun fromConfigMap(name: String, config: Map<String, String>): FlashcardPattern {
            val pattern = config[name]
                ?: throw IllegalArgumentException("❌ Padrão '$name' não encontrado na configuração.")
            return FlashcardPattern(pattern)
        }

        fun fromEnvOrDefault(envVar: String = "FLASHCARD_PATTERN"): FlashcardPattern {
            val pattern = System.getenv(envVar)
            return if (pattern.isNullOrBlank()) {
                println("⚠️ Nenhuma variável de ambiente '$envVar' encontrada. Usando padrão default.")
                default
            } else {
                println("🔧 Carregando padrão de parsing via variável '$envVar'")
                FlashcardPattern(pattern)
            }
        }
    }
}

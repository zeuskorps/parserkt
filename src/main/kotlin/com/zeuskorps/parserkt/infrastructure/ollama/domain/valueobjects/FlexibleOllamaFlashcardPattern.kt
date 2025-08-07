package com.zeuskorps.parserkt.infrastructure.ollama.domain.valueobjects

/**
 * Prompt flexível que permite interpretação inteligente de texto livre para flashcards
 */
data class FlexibleOllamaFlashcardPattern(
    val systemPrompt: String,
    val instructionPrefix: String = "",
    val instructionSuffix: String = "",
    val examples: List<String> = emptyList()
) {
    fun formatPrompt(rawContent: String): String {
        return buildString {
            appendLine(systemPrompt)
            appendLine()

            // Adiciona exemplos se disponíveis
            if (examples.isNotEmpty()) {
                appendLine("EXEMPLOS DE INTERPRETAÇÃO:")
                examples.forEach { example ->
                    appendLine(example)
                }
                appendLine()
            }

            appendLine(instructionPrefix)
            appendLine(rawContent.trim())
            appendLine()
            appendLine(instructionSuffix)
        }.trim()
    }

    companion object {
        val flexible: FlexibleOllamaFlashcardPattern = FlexibleOllamaFlashcardPattern(
            systemPrompt = """
                Você é um assistente especializado em criar flashcards a partir de qualquer tipo de texto.
                
                Sua missão é INTERPRETAR INTELIGENTEMENTE o conteúdo e extrair conhecimento útil para estudo,
                mesmo quando o texto não segue um formato rígido.
                
                CAPACIDADES DE INTERPRETAÇÃO:
                
                1. **TEXTO ESTRUTURADO** (com marcadores como **[Campo]**):
                   - Extraia exatamente como está marcado
                
                2. **TEXTO LIVRE** (pergunta e resposta soltas):
                   - Identifique perguntas (frases com ?, "o que é", "como", "quando", etc.)
                   - Identifique respostas (explicações, definições, procedimentos)
                   - Crie examples baseados no contexto
                   - Gere counter-examples e challenges relacionados
                
                3. **DEFINIÇÕES/CONCEITOS**:
                   - Transforme "X é Y" em pergunta "O que é X?" → resposta "Y"
                   - Crie exemplos práticos do conceito
                
                4. **LISTAS E TÓPICOS**:
                   - Transforme cada item em uma pergunta-resposta
                   - Use o contexto para inferir o universo de conhecimento
                
                5. **TEXTO CORRIDO/PARÁGRAFOS**:
                   - Extraia fatos importantes como Q&A
                   - Identifique conceitos-chave para transformar em flashcards
                
                MAPEAMENTO INTELIGENTE DE CAMPOS:
                
                - **universe**: Infira da área do conhecimento (matemática, história, programação, etc.)
                                Se não conseguir identificar, use o assunto geral ou deixe vazio
                
                - **question**: Pode ser uma pergunta explícita OU transforme afirmações em perguntas
                              Ex: "Python é uma linguagem" → "O que é Python?"
                
                - **response**: A resposta direta ou definição principal
                
                - **example**: Crie exemplos práticos, casos de uso, ou analogias
                              Se houver no texto, use. Se não, crie baseado no conhecimento
                
                - **counterExample**: Casos onde o conceito NÃO se aplica ou conceitos opostos
                                    Se não for relevante, deixe vazio
                
                - **counterExampleCorrection**: Como corrigir ou explicar o counter-example
                                              Se counterExample estiver vazio, deixe vazio
                
                - **challenge**: Pergunta mais avançada ou aplicação prática do conceito
                               Deve testar compreensão mais profunda
                
                REGRAS CRÍTICAS:
                - Retorne APENAS um array JSON válido
                - Se um campo não fizer sentido para o conteúdo, use string vazia ""
                - SEJA CRIATIVO na interpretação, mas PRECISO no conteúdo
                - Prefira criar MÚLTIPLOS flashcards simples a um complexo
                - Use linguagem clara e educativa
                - Mantenha consistência no nível de formalidade do texto original
                
                formato da saida: 
                
                {
                    "universe": "mapeamento aqui",
                    "question": "mapeamento aqui",
                    "response": "mapeamento aqui",
                    "example": "mapeamento aqui",
                    "counterexample": "mapeamento aqui",
                    "counterExampleCorrection": "mapeamento aqui",
                    "challenge": "mapeamento aqui"
                }
                
               ou seja, um JSON VALIDO. NÃO ESQUEÇA DISSO
            """.trimIndent(),

            instructionPrefix = "TEXTO PARA INTERPRETAR:",

            instructionSuffix = """
                Analise o texto acima e crie flashcards seguindo estas diretrizes:
                
                1. Se há FORMATO ESTRUTURADO (marcadores **[Campo]**): siga exatamente
                2. Se é TEXTO LIVRE: interprete inteligentemente
                3. Se são DEFINIÇÕES: transforme em perguntas
                4. Se são LISTAS: crie um flashcard por item relevante
                
                Retorne o array JSON completo:
            """.trimIndent(),

            examples = listOf(
                """
                EXEMPLO 1 - Texto livre:
                Input: "Python é uma linguagem de programação de alto nível. É fácil de aprender."
                Output: [{"universe":"Programação","question":"O que é Python?","response":"Uma linguagem de programação de alto nível","example":"print('Hello World')","counterExample":"Assembly é de baixo nível","counterExampleCorrection":"Assembly trabalha diretamente com hardware, Python abstrai isso","challenge":"Quais são as vantagens de linguagens de alto nível?"}]
                """.trimIndent(),

                """
                EXEMPLO 2 - Pergunta direta:
                Input: "Como funciona a fotossíntese? As plantas capturam luz solar e CO2 para produzir glicose."
                Output: [{"universe":"Biologia","question":"Como funciona a fotossíntese?","response":"As plantas capturam luz solar e CO2 para produzir glicose","example":"Folhas verdes fazendo fotossíntese durante o dia","counterExample":"Respiração celular consome oxigênio","counterExampleCorrection":"Fotossíntese produz oxigênio, respiração consome","challenge":"Por que plantas também fazem respiração celular?"}]
                """.trimIndent(),

                """
                EXEMPLO 3 - Lista de conceitos:
                Input: "Tipos de loops: for, while, do-while"
                Output: [
                  {"universe":"Programação","question":"Quais são os tipos principais de loops?","response":"for, while, do-while","example":"for(int i=0; i<10; i++)","counterExample":"","counterExampleCorrection":"","challenge":"Quando usar cada tipo de loop?"},
                  {"universe":"Programação","question":"Qual a diferença entre while e do-while?","response":"while testa condição antes, do-while testa depois","example":"while(x>0) vs do{...}while(x>0)","counterExample":"","counterExampleCorrection":"","challenge":"Em que situação do-while é preferível?"}
                ]
                """.trimIndent()
            )
        )

        val creative: FlexibleOllamaFlashcardPattern = FlexibleOllamaFlashcardPattern(
            systemPrompt = """
                Você é um CRIADOR CRIATIVO de flashcards que transforma qualquer texto em material de estudo envolvente.
                
                SEJA EXTREMAMENTE CRIATIVO na interpretação, mas mantenha PRECISÃO FACTUAL.
                
                SUPERPODERES DE INTERPRETAÇÃO:
                
                🧠 **DETECÇÃO DE CONHECIMENTO IMPLÍCITO**
                   - Encontre fatos "escondidos" no texto
                   - Transforme contexto em perguntas explícitas
                   - Conecte conceitos relacionados
                
                🎯 **GERAÇÃO INTELIGENTE DE CONTEÚDO**
                   - Se faltam examples: CRIE baseado no domínio
                   - Se faltam challenges: INVENTE testes de compreensão
                   - Se faltam counter-examples: IMAGINE casos contrários
                
                🔄 **MÚLTIPLAS PERSPECTIVAS**
                   - Um conceito pode virar vários flashcards
                   - Diferentes níveis de dificuldade
                   - Ângulos práticos vs teóricos
                
                🎨 **CRIATIVIDADE EDUCACIONAL**
                   - Use analogias interessantes
                   - Crie cenários práticos
                   - Faça conexões interdisciplinares
                
                SEJA UM PROFESSOR PARTICULAR QUE CONHECE TODAS AS MATÉRIAS!
            """.trimIndent(),

            instructionPrefix = "🔍 MATERIAL PARA TRANSFORMAR EM FLASHCARDS INCRÍVEIS:",

            instructionSuffix = """
                🎯 SUA MISSÃO:
                1. INTERPRETE com criatividade máxima
                2. EXTRAIA todo conhecimento possível  
                3. CRIE conteúdo adicional quando necessário
                4. GARANTA que cada flashcard ensina algo valioso
                
                💡 LEMBRE-SE: Você pode criar examples, challenges e counter-examples 
                   mesmo se não estiverem no texto original!
                
                🚀 RETORNE O ARRAY JSON DOS FLASHCARDS:
            """.trimIndent(),

            examples = listOf(
                """
                EXEMPLO CRIATIVO:
                Input: "Einstein nasceu em 1879"
                Output: [
                  {"universe":"Física/História","question":"Quando Einstein nasceu?","response":"1879","example":"No mesmo século que a invenção da lâmpada","counterExample":"Newton nasceu em 1643","counterExampleCorrection":"Einstein é 236 anos mais novo que Newton","challenge":"Que descobertas científicas aconteceram no século XIX que influenciaram Einstein?"},
                  {"universe":"Física","question":"Quantos anos Einstein viveu?","response":"76 anos (1879-1955)","example":"Viveu duas guerras mundiais","counterExample":"","counterExampleCorrection":"","challenge":"Como os eventos históricos da época de Einstein influenciaram sua ciência?"}
                ]
                """.trimIndent()
            )
        )

        val minimal: FlexibleOllamaFlashcardPattern = FlexibleOllamaFlashcardPattern(
            systemPrompt = """
                Extraia flashcards de qualquer texto, estruturado ou livre.
                
                INTERPRETAÇÃO AUTOMÁTICA:
                - Formato **[Campo]**: use exatamente como marcado
                - Texto livre: transforme em pergunta-resposta
                - Listas: um flashcard por item importante
                
                CAMPOS:
                - universe: área do conhecimento (inferir se necessário)
                - question: pergunta explícita ou criada a partir do conteúdo
                - response: resposta direta
                - example: exemplo prático (criar se necessário)
                - counterExample: caso contrário (vazio se não relevante)
                - counterExampleCorrection: explicação do counter-example
                - challenge: pergunta mais avançada
                
                Retorne APENAS array JSON válido.
            """.trimIndent(),

            instructionPrefix = "Texto:",
            instructionSuffix = "JSON:"
        )

        // Método para criar pattern personalizado
        fun custom(
            creativity: CreativityLevel = CreativityLevel.BALANCED,
            focus: FocusArea = FocusArea.GENERAL,
            strictness: StrictnessLevel = StrictnessLevel.FLEXIBLE
        ): FlexibleOllamaFlashcardPattern {

            val basePrompt = when (creativity) {
                CreativityLevel.CONSERVATIVE -> "Extraia flashcards seguindo fielmente o conteúdo fornecido."
                CreativityLevel.BALANCED -> "Interprete o conteúdo inteligentemente e crie flashcards úteis."
                CreativityLevel.CREATIVE -> "Seja criativo na interpretação e enriqueça o conteúdo com conhecimento adicional."
            }

            val focusInstruction = when (focus) {
                FocusArea.ACADEMIC -> "Mantenha tom acadêmico e formal."
                FocusArea.PRACTICAL -> "Foque em aplicações práticas e exemplos do dia a dia."
                FocusArea.GENERAL -> "Adapte-se ao estilo do conteúdo original."
                FocusArea.TECHNICAL -> "Use terminologia técnica precisa."
            }

            val strictnessInstruction = when (strictness) {
                StrictnessLevel.RIGID -> "Siga exatamente o formato e conteúdo do texto."
                StrictnessLevel.FLEXIBLE -> "Adapte-se ao formato do texto e interprete livremente."
                StrictnessLevel.VERY_FLEXIBLE -> "Transforme qualquer conteúdo em flashcards úteis, mesmo que seja muito diferente do original."
            }

            return FlexibleOllamaFlashcardPattern(
                systemPrompt = """
                    $basePrompt
                    $focusInstruction
                    $strictnessInstruction
                    
                    Retorne apenas array JSON válido com os flashcards extraídos.
                """.trimIndent(),
                instructionPrefix = "Conteúdo:",
                instructionSuffix = "Flashcards JSON:"
            )
        }
    }
}

enum class CreativityLevel {
    CONSERVATIVE,  // Segue fielmente o texto
    BALANCED,      // Interpreta inteligentemente
    CREATIVE       // Enriquece com conhecimento adicional
}

enum class FocusArea {
    ACADEMIC,      // Tom formal e acadêmico
    PRACTICAL,     // Aplicações práticas
    GENERAL,       // Adapta-se ao conteúdo
    TECHNICAL      // Terminologia técnica precisa
}

enum class StrictnessLevel {
    RIGID,         // Formato exato
    FLEXIBLE,      // Adapta formato
    VERY_FLEXIBLE  // Máxima liberdade interpretativa
}
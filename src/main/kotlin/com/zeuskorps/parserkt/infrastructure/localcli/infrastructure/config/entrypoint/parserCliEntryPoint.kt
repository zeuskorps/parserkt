package com.zeuskorps.parserkt.infrastructure.localcli.infrastructure.config.entrypoint

import com.zeuskorps.parserkt.infrastructure.localcli.infrastructure.config.service.provideParserCliRunner

fun main(args: Array<String>) {
   val (patternPath, deckPath) = when (args.size) {
      0 -> null to null
      1 -> null to args[0] // Apenas arquivo .md
      else -> args[0] to args[1] // Primeiro: pattern.yaml, Segundo: markdown
   }

   val runner = provideParserCliRunner(patternPath)
   runner.run(deckPath)
}
package com.zeuskorps.parserkt.infrastructure.localcli.infrastructure.config.entrypoint
import com.zeuskorps.parserkt.infrastructure.localcli.infrastructure.config.service.provideParserCliRunner
fun main(args: Array<String>) {
   provideParserCliRunner().run(args)
}
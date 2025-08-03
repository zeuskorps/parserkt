package com.zeuskorps.parserkt.infrastructure.archso.adapters.out

import com.zeuskorps.parserkt.infrastructure.python_apkg_adapter.application.dto.PythonEnvPathsDto
import com.zeuskorps.parserkt.infrastructure.python_apkg_adapter.application.ports.out.PythonEnvironmentProviderPort
import kotlin.io.path.Path
import kotlin.io.path.exists

class ArchPythonVenvAdapter(
    private val venvDir: String = "build/venv"
) : PythonEnvironmentProviderPort {

    override fun setupEnvironment(requirementsPath: String): PythonEnvPathsDto {
        val venvPython = "$venvDir/bin/python"
        val venvPip = "$venvDir/bin/pip"

        if (!Path(venvPython).exists()) {
            println("🧪 Criando virtualenv...")
            val creation = ProcessBuilder("python3", "-m", "venv", venvDir)
                .inheritIO()
                .start()
                .waitFor()
            check(creation == 0) { "Erro ao criar o virtualenv (exit=$creation)" }
        }

        println("📦 Instalando dependências Python no venv...")
        val install = ProcessBuilder(venvPip, "install", "-r", requirementsPath)
            .inheritIO()
            .start()
            .waitFor()

        check(install == 0) { "Erro ao instalar dependências no virtualenv (exit=$install)" }

        return PythonEnvPathsDto(venvPython, venvPip)
    }
}

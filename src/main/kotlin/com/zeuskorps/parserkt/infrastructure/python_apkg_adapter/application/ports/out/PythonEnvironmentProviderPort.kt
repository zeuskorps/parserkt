package com.zeuskorps.parserkt.infrastructure.python_apkg_adapter.application.ports.out

import com.zeuskorps.parserkt.infrastructure.python_apkg_adapter.application.dto.PythonEnvPathsDto

interface PythonEnvironmentProviderPort {
    fun setupEnvironment(requirementsPath: String): PythonEnvPathsDto
}
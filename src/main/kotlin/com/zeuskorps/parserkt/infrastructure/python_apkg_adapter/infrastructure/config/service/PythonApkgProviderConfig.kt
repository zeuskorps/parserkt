package com.zeuskorps.parserkt.infrastructure.python_apkg_adapter.infrastructure.config.service

import com.zeuskorps.parserkt.application.ports.out.ApkgProviderPort
import com.zeuskorps.parserkt.application.ports.out.SerializationFlashcardProviderPort
import com.zeuskorps.parserkt.infrastructure.python_apkg_adapter.application.ports.out.PythonEnvironmentProviderPort
import com.zeuskorps.parserkt.infrastructure.python_apkg_adapter.infrastructure.adapter.out.PythonApkgProviderAdapter

object PythonApkgProviderConfig {
    fun apkgProvider(serializer: SerializationFlashcardProviderPort,envProvider: PythonEnvironmentProviderPort ): ApkgProviderPort {
        return PythonApkgProviderAdapter(serializer,envProvider)
    }
}

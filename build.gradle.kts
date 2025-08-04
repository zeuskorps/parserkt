plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.1.20"
    application
}
group = "com.zeuskorps"
version = "1.0-SNAPSHOT"
repositories {
    mavenCentral()
}
dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3") // versão estável até o momento
    implementation("org.snakeyaml:snakeyaml-engine:2.7")
}
application {
    mainClass.set("com.zeuskorps.parserkt.infrastructure.localcli.infrastructure.config.entrypoint.ParserCliEntryPointKt")
}
kotlin {
    jvmToolchain(17)
    compilerOptions {
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
    }
}
tasks.test {
    useJUnitPlatform()
}
tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.zeuskorps.parserkt.infrastructure.localcli.infrastructure.config.entrypoint.ParserCliEntryPointKt"
    }
    // (opcional) inclui dependências no JAR
    from({
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
plugins {
    kotlin("jvm") version "2.1.20"
}

group = "com.zeuskorps"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3") // versão estável até o momento
}

tasks.test {
    useJUnitPlatform()
}
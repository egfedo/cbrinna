plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("kapt") version "1.9.0"
    kotlin("plugin.allopen") version "1.9.0"
    application
    id("io.micronaut.application") version "1.4.2"
    kotlin("plugin.serialization") version "1.9.0"
}

micronaut {
    version = "3.10.3"
    processing {
        incremental(true)
        annotations ("ru.egfedo.*")
    }
}

group = "ru.egfedo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("eu.vendeli:telegram-bot:3.4.0")
    implementation("com.squareup.moshi:moshi:1.14.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")
    implementation("io.micronaut:micronaut-inject:4.2.0")
    kapt("io.micronaut:micronaut-inject-java:4.2.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    runtimeOnly("org.yaml:snakeyaml")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("ru.egfedo.cbrinna.MainKt")
}

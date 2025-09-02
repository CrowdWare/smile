plugins {
    kotlin("jvm") version "2.0.20"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("plugin.serialization") version "1.9.0"
}

group = "at.crowdware.nocode"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {

    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib"))
    implementation("com.github.h0tk3y.betterParse:better-parse:0.4.4")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")       // HTTP-Client
    implementation("org.json:json:20240303")
    implementation("io.ktor:ktor-client-content-negotiation:3.0.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.3")

}

application {
    mainClass.set("at.crowdware.nocode.MainKt")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "at.crowdware.nocode.MainKt"
    }
}

tasks {
    named<Jar>("shadowJar") {
        archiveClassifier.set("all")
    }
}


tasks.register<Exec>("nativeImage") {
    group = "build"
    description = "Build native image using GraalVM"

    val outputName = "smile"
    val jarPath = layout.buildDirectory.file("libs/${project.name}-${project.version}-all.jar").get().asFile

    dependsOn("shadowJar")

    commandLine = listOf(
        "native-image",
        "-jar", jarPath.absolutePath,
        outputName
    )
}
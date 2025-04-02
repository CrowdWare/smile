plugins {
    kotlin("jvm") version "2.0.20"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
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

    dependsOn("jar")

    commandLine = listOf(
        "native-image",
        "-jar", jarPath.absolutePath,
        outputName
    )
}
plugins {
    kotlin("jvm") version "2.0.20"
    application
}

group = "at.crowdware.nocode"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
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

tasks.register<Exec>("nativeImage") {
    group = "build"
    description = "Build native image using GraalVM"

    val outputName = "smile"
    val jarPath = layout.buildDirectory.file("libs/${project.name}-${project.version}.jar").get().asFile

    dependsOn("jar")

    commandLine = listOf(
        "native-image",
        "-jar", jarPath.absolutePath,
        outputName
    )
}
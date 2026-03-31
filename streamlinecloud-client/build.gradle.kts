import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm") version "2.3.10"
}

group = "net.streamlinecloud"
val version: String by rootProject
val branch: String by rootProject

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    build {
        dependsOn("shadowJar")
    }
}

tasks.named<ShadowJar>("shadowJar") {
    group = "net.streamlinecloud"

    destinationDirectory.set(project.rootProject.projectDir.resolve("finished_builds/streamlinecloud-client"))
    archiveFileName.set("streamlinecloud-client-$branch-$version.jar")
}

tasks.shadowJar {
    archiveClassifier.set("")
}
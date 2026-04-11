import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm")
}

group = "net.streamlinecloud"
val version: String by rootProject
val branch: String by rootProject

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.spring.io/milestone")
    }
}

dependencies {
    implementation("org.hildan.krossbow:krossbow-stomp-core:7.1.0")
    implementation("org.hildan.krossbow:krossbow-websocket-ktor:7.1.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("io.ktor:ktor-client-cio:3.1.3")

    api(project(":streamlinecloud-api"))

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
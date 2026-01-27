import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "net.streamlinecloud"
val version: String by rootProject
val branch: String by rootProject

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<ShadowJar>("shadowJar") {
    group = "net.streamlinecloud"

    destinationDirectory.set(project.rootProject.projectDir.resolve("finished_builds/streamlinecloud-launcher"))
    archiveFileName.set("streamlinecloud-launcher-$branch-$version.jar")
}

tasks.test {
    useJUnitPlatform()
}
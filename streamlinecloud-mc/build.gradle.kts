import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.8.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java")
    id("maven-publish")
}

group = "net.streamlinecloud"
val version: String by rootProject
val branch: String by rootProject

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
}

dependencies {

    implementation(kotlin("stdlib"))

    implementation("commons-io:commons-io:2.16.1")

    implementation("javax.websocket:javax.websocket-api:1.1")
    implementation("org.glassfish.tyrus:tyrus-client:1.17")

    implementation("org.jetbrains:annotations:24.0.0")
    implementation(project(":streamlinecloud-api"))
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")

    testCompileOnly("org.projectlombok:lombok:1.18.32")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.32")

    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
}

publishing {
    repositories {
        maven {
            name = "streamlinecloud-repo"
            url = uri("https://maven.pkg.github.com/streamlinecloud/StreamlineCloud")
            credentials {
                username = (project.findProperty("gpr.user") ?: System.getenv("USERNAME")) as String?
                password = (project.findProperty("gpr.key") ?: System.getenv("TOKEN")) as String?
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
            version = "$branch-$version"
        }
    }
}

tasks.processResources {
    val props = mapOf("version" to project.version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props + mapOf("project" to project))
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("")
}

tasks {
    build {
        dependsOn("shadowJar")
    }
}

tasks.named<ShadowJar>("shadowJar") {
    group = "net.streamlinecloud"

    destinationDirectory.set(project.rootProject.projectDir.resolve("finished_builds/streamlinecloud-mc"))
    archiveFileName.set("streamlinecloud-mc-$branch-$version.jar")
}

tasks.shadowJar {
    archiveClassifier.set("")
}


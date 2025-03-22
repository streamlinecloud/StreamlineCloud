import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

plugins {
    kotlin("jvm") version "1.8.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("java")
}

group = "io.streamlinemc"
version = "1.0-SNAPSHOT"
val branch = "alpha"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {

    implementation(kotlin("stdlib"))

    implementation("org.slf4j:slf4j-simple:2.0.9")
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("org.jline:jline:3.27.1")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("io.javalin:javalin:6.1.3")
    implementation("org.java-websocket:Java-WebSocket:1.5.6")
    implementation("com.github.Carleslc.Simple-YAML:Simple-Yaml:1.8.4")
    implementation("org.fusesource.jansi:jansi:2.4.1")
    implementation("me.tongfei:progressbar:0.10.1")
    implementation("commons-io:commons-io:2.16.1")
    implementation("org.apache.httpcomponents:httpclient:4.5")
    implementation("org.json:json:20230227")

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    testCompileOnly("org.projectlombok:lombok:1.18.32")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.32")

    implementation(project(":streamlinecloud-api"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")


}

tasks.test {
    useJUnitPlatform()
}

val currentBuildNumber: Int = Random.nextInt(1000, 100000000)
val currentBuildTime: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

tasks.register("generateBuildConfig") {
    doLast {
        val outputDir = file("$buildDir/generated-src")
        outputDir.mkdirs()
        val buildConfigFile = File(outputDir, "MainBuildConfig.java")
        buildConfigFile.writeText(
            """
            package net.streamlinecloud.main.utils;
            
            public final class MainBuildConfig {
                public static final String BUILD_NUMBER = "SC-$currentBuildNumber";
                public static final String BUILD_DATE = "$currentBuildTime";
            }
            """.trimIndent()
        )
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("")
    configurations.forEach { configuration ->
        from(configuration)
    }
}

sourceSets {
    getByName("main").java.srcDir("$buildDir/generated-src")
}

val mainClass = "net.streamlinecloud.main.CloudLauncher"

tasks.jar {
    manifest {
        dependsOn("generateBuildConfig")
        attributes["Main-Class"] = mainClass
    }
}

tasks {
    build {
        dependsOn("generateBuildConfig")
        dependsOn("shadowJar")
    }
}

tasks.register("Make Main Project") {
    dependsOn("generateBuildConfig")
    group = "StreamlineCloud"

    val bdir = project.rootProject.projectDir.resolve("finished_builds/streamlinecloud-main")

    if (bdir.exists()) {
        bdir.deleteRecursively()
    }

    val jarTask = tasks.getByName<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar")
    val jarFile = jarTask.archiveFile.get()

    doLast {

        bdir.mkdirs()

        val copiedJar = project.copy {
            from(jarFile)
            into(bdir)
            rename(jarFile.asFile.name, "streamlinecloud_NODE-$branch-$version.jar")
        }

        println("Built Jar File: $bdir")

    }

}

tasks.named("Make Main Project") {
    dependsOn("shadowJar")
}
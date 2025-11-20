import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import groovy.json.JsonSlurper
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

plugins {
    kotlin("jvm") version "1.8.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("java")
}

group = "net.streamlinecloud"
val version: String by rootProject
val branch: String by rootProject

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
    implementation("io.javalin:javalin:6.7.0")
    implementation("org.java-websocket:Java-WebSocket:1.5.6")
    implementation("com.github.Carleslc.Simple-YAML:Simple-Yaml:1.8.4")
    implementation("org.fusesource.jansi:jansi:2.4.1")
    implementation("me.tongfei:progressbar:0.10.1")
    implementation("commons-io:commons-io:2.16.1")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("org.json:json:20231013")

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
                public static final String VERSION = "$branch-$version";
            }
            """.trimIndent()
        )
    }
}

tasks.withType<ShadowJar> {
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

tasks.register("makeMainProject") {
    dependsOn("generateBuildConfig")

    group = "StreamlineCloud"

    val bdir = project.rootProject.projectDir.resolve("finished_builds/streamlinecloud-main")

    if (bdir.exists()) {
        bdir.deleteRecursively()
    }

    val jarTask = tasks.getByName<ShadowJar>("shadowJar")
    val jarFile = jarTask.archiveFile.get()

    doLast {

        bdir.mkdirs()

        val copiedJar = project.copy {
            from(jarFile)
            into(bdir)
            rename(jarFile.asFile.name, "streamlinecloud_MAIN-$branch-$version.jar")
        }

        println("Built Jar File: $bdir")

    }

}

tasks.register("startTest") {
    group = "Testing"
    description = "Starts or resumes a StreamlineCloud main instance for testing."

    dependsOn("makeMainProject")

    doLast {
        val rootDir = rootProject.layout.projectDirectory.asFile
        val buildDir = File(rootDir, "finished_builds/streamlinecloud-main")
        val testSystemRoot = File(rootDir, "testSystem")
        val testDir = File(testSystemRoot, "main")

        if (!testDir.exists()) {
            println("→ No existing test system found, creating new one...")
            testDir.mkdirs()

            val latestJar = buildDir.listFiles()
                ?.filter { it.extension == "jar" }
                ?.maxByOrNull { it.lastModified() }
                ?: throw GradleException("Cannot start StreamlineCloud: no JAR found in ${buildDir.path}")

            println("→ Copying ${latestJar.name} to ${testDir.path}")
            latestJar.copyTo(File(testDir, "streamlinecloud_test.jar"))
        } else {
            println("→ Existing test system found: ${testDir.path}")
        }

        val jarFile = File(testDir, "streamlinecloud_test.jar")
        if (!jarFile.exists()) {
            throw GradleException("No streamlinecloud_test.jar found in ${testDir.path}")
        }

        println("→ Starting StreamlineCloud Main instance...")

        val os = System.getProperty("os.name").lowercase()
        val javaPath = File(System.getProperty("java.home"), "bin/java").absolutePath

        val command = when {
            os.contains("win") -> listOf(
                "cmd", "/c",
                "start", "cmd", "/k", "\"\"$javaPath\" -jar \"${jarFile.absolutePath}\"\""
            )

            os.contains("linux") -> {
                // Try common terminal emulators
                val terminals = listOf("x-terminal-emulator", "gnome-terminal", "konsole", "xfce4-terminal", "xterm", "kgx")
                val terminal = terminals.find { Runtime.getRuntime().exec(arrayOf("which", it)).waitFor() == 0 }
                    ?: error("No supported terminal found! Install one of: ${terminals.joinToString()}")

                listOf(
                    "bash", "-c",
                    "$terminal -e 'bash -c \"\\\"$javaPath\\\" -jar \\\"${jarFile.absolutePath}\\\"; exec bash\"'"
                )
            }

            else -> error("Unsupported OS: $os")
        }


        println("→ Opening new console window...")
        ProcessBuilder(command)
            .directory(testDir)
            .start()

        println("StreamlineCloud started in: ${testDir.path}")
    }
}

tasks.register("rebuildTest") {
    group = "Testing"
    description = "Deletes and recreates the test system environment."

    dependsOn("makeMainProject")

    doLast {
        val rootDir = rootProject.layout.projectDirectory.asFile
        val testSystemRoot = File(rootDir, "testSystem")

        if (testSystemRoot.exists()) {
            println("→ Deleting old test system: ${testSystemRoot.path}")
            testSystemRoot.deleteRecursively()
        }

        testSystemRoot.mkdirs()
        println("Test system cleaned and ready for rebuild.")
    }
}

tasks.register<Copy>("updateTest") {
    group = "Testing"
    description = "Builds the project and updates the test system environment."
    dependsOn("makeMainProject")

    println("Copying StreamlineCloud-MAIN")

    val destResources = project.layout.projectDirectory.file("../testSystem/main")

    from(project.rootProject.projectDir.resolve("finished_builds/streamlinecloud-main/streamlinecloud_MAIN-$branch-$version.jar"))
    into(destResources)

    rename { "streamlinecloud_test.jar" }

    doLast {
        println("Copied ${destResources.asFile.absolutePath}")
    }
}


tasks.named("makeMainProject") {
    dependsOn("copyStreamlineMc")
    dependsOn("shadowJar")
}

tasks.named("compileJava") {
    dependsOn(":streamlinecloud-api:shadowJar")
}

tasks.named("processResources") {
    dependsOn("copyStreamlineMc")
}

tasks.register<Copy>("copyStreamlineMc") {
    group = "StreamlineCloud"
    description = "Copies the streamlinecloud-mc plugin into the main project"

    println("Copying StreamlineCloud-MC")

    val destResources = project.layout.projectDirectory.file("src/main/resources")
    dependsOn(":streamlinecloud-mc:makeMcProject")

    from(project.rootProject.projectDir.resolve("finished_builds/streamlinecloud-mc/streamlinecloud-mc.jar"))
    into(destResources)

    rename { "streamlinecloud-mc" }

    doLast {
        println("Copied ${destResources.asFile.absolutePath}")
    }
}

tasks.register("matchLanguageFiles") {
    group = "localization"
    description = "Aligns other language files to en.json template with ordering and spacing"

    doLast {
        val langDir = file("src/main/resources/lang")
        val mainLangFile = langDir.resolve("en.json")

        if (!mainLangFile.exists()) {
            throw GradleException("Main language file not found at: $mainLangFile")
        }

        val jsonSlurper = JsonSlurper()
        val templateLines = mainLangFile.readLines()
        val templateKeys = mutableListOf<String>()
        val spacingAfter = mutableSetOf<String>() // store keys after which a blank line occurs

        for (i in templateLines.indices) {
            val line = templateLines[i].trim()
            val match = Regex("^\"([^\"]+)\"\\s*:").find(line)
            if (match != null) {
                val key = match.groupValues[1]
                templateKeys.add(key)

                if (i + 1 < templateLines.size && templateLines[i + 1].isBlank()) {
                    spacingAfter.add(key)
                }
            }
        }

        langDir.listFiles { _, name -> name.endsWith(".json") && name != "en.json" }
            ?.forEach { file ->
                val content = jsonSlurper.parseText(file.readText(StandardCharsets.UTF_8)) as MutableMap<String, Any?>
                var changed = false

                val sb = StringBuilder()
                sb.append("{\n")

                templateKeys.forEachIndexed { idx, key ->
                    val value = content[key] ?: run {
                        changed = true
                        "-"
                    }

                    val comma = if (idx == templateKeys.size - 1) "" else ","
                    sb.append("  \"").append(key).append("\": \"").append(value).append("\"").append(comma).append("\n")

                    if (spacingAfter.contains(key)) {
                        sb.append("\n")
                    }
                }

                sb.append("}\n")

                if (changed || file.readText() != sb.toString()) {
                    file.writeText(sb.toString())
                    println("Updated language file: ${file.name}")
                } else {
                    println("Language file up to date: ${file.name}")
                }
            }
    }
}
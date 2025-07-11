import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.8.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("java")
}

group = "io.streamlinemc"
version = "1.0.0"
val branch = "alpha"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "spigotmc-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
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

    compileOnly("org.spigotmc:spigot-api:1.20-R0.1-SNAPSHOT")
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
}

val targetJavaVersion = 17
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"

    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
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

tasks.register("Make MC Project") {
    group = "StreamlineCloud"

    val bdir = project.rootProject.projectDir.resolve("finished_builds/streamlinecloud-mc")


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
            rename(jarFile.asFile.name, "streamlinecloud_MC-$branch-$version.jar")
        }

        println("Built Jar File: $bdir")

    }

}

tasks.named("Make MC Project") {
    dependsOn("shadowJar")
}


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
}

dependencies {

    implementation(kotlin("stdlib"))

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    testCompileOnly("org.projectlombok:lombok:1.18.32")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.32")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("")
}

tasks {
    build {
        dependsOn("shadowJar")
    }
}

tasks.register("Make API Project") {
    group = "StreamlineCloud"

    val bdir = project.rootProject.projectDir.resolve("finished_builds/streamlinecloud-api")

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
            rename(jarFile.asFile.name, "streamlinecloud_API-$branch-$version.jar")
        }

        println("Built Jar File: $bdir")

    }

}

tasks.named("Make API Project") {
    dependsOn("shadowJar")
}
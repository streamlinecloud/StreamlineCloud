import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java")
    id("maven-publish")
}

group = "net.streamlinecloud"
val version: String by rootProject
val branch: String by rootProject

dependencies {

    implementation("com.google.code.gson:gson:2.11.0")

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    testCompileOnly("org.projectlombok:lombok:1.18.32")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.32")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
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

java {
    withJavadocJar()
    withSourcesJar()
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

    destinationDirectory.set(project.rootProject.projectDir.resolve("finished_builds/streamlinecloud-api"))
    archiveFileName.set("streamlinecloud-api-$branch-$version.jar")
}

tasks.shadowJar {
    archiveClassifier.set("")
}
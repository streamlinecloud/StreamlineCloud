plugins {
    kotlin("multiplatform") version "2.3.10" apply false
    kotlin("jvm")           version "2.3.10" apply false
    kotlin("plugin.spring") version "2.3.10" apply false

    id("org.springframework.boot") version "4.0.2" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    kotlin("plugin.jpa") version "2.3.10"
}

subprojects {
    plugins.withId("java") {
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        }
    }
}

group = "net.streamlinecloud"
val version: String by rootProject
val branch: String by rootProject

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}

dependencies {
}

tasks.register("compileAll") {
    group = "net.streamlinecloud"

    dependsOn(":streamlinecloud-api:shadowJar",
        ":streamlinecloud-api:shadowJar",
        ":streamlinecloud-mc:shadowJar",
        ":streamlinecloud-node:shadowJar",
        ":streamlinecloud-backend:bootJar"
    )
}
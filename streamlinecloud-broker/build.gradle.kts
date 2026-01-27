plugins {
    java
    id("org.springframework.boot") version "4.0.2"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "net.streamlinecloud"
val version: String by rootProject
val branch: String by rootProject


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(project(":streamlinecloud-api"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    group = "net.streamlinecloud"

    destinationDirectory.set(project.rootProject.layout.projectDirectory.dir("finished_builds/streamlinecloud-broker"))

    archiveFileName.set("streamlinecloud-broker-$branch-$version.jar")
}

tasks.register("prepareKotlinBuildScriptModel") {
    group = "IDE compatibility"
    // no actions needed — exists only so IDE import doesn't fail
}


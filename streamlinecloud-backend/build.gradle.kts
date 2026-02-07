plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
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
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    group = "net.streamlinecloud"

    destinationDirectory.set(project.rootProject.layout.projectDirectory.dir("finished_builds/streamlinecloud-backend"))

    archiveFileName.set("streamlinecloud-backend-$branch-$version.jar")
}

tasks.register("prepareKotlinBuildScriptModel") {
    group = "IDE compatibility"
    // no actions needed — exists only so IDE import doesn't fail
}

tasks.withType<Test> {
    useJUnitPlatform()
}

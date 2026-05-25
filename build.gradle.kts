
plugins {
    kotlin("multiplatform") version "2.3.10" apply false
    kotlin("jvm")           version "2.3.10" apply false
    kotlin("plugin.spring") version "2.3.10" apply false

    id("org.springframework.boot") version "4.0.2" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    kotlin("plugin.jpa") version "2.3.10"
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
        ":streamlinecloud-launcher:shadowJar",
        ":streamlinecloud-backend:bootJar"
    )
}

tasks.register<Copy>("packageLauncher") {
    group = "net.streamlinecloud"

    dependsOn(
        ":compileAll",
        ":copyLauncherReadme",
        ":streamlinecloud-launcher:packageAllNative",
    )

    into(layout.projectDirectory.dir("finished_builds/streamlinecloud-launcher/jar"))

    from(
        rootProject.projectDir.resolve(
            "finished_builds/streamlinecloud-node/streamlinecloud-node-$branch-$version.jar"
        )
    ) {
        rename { "streamlinecloud-node.jar" }
    }

    from(
        rootProject.projectDir.resolve(
            "finished_builds/streamlinecloud-backend/streamlinecloud-backend-$branch-$version.jar"
        )
    ) {
        rename { "streamlinecloud-backend.jar" }
    }
}

tasks.register<Copy>("copyLauncherReadme") {
    into(layout.projectDirectory.dir("finished_builds/streamlinecloud-launcher"))

    from(
        rootProject.projectDir.resolve(
            "streamlinecloud-launcher/README_INSTALLATION.md"
        )
    ) {
        rename { "README.md" }
    }
}

tasks.register<Zip>("packageLauncherZipRelease") {
    group = "net.streamlinecloud"

    from(layout.projectDirectory.dir("finished_builds/streamlinecloud-launcher")) {
        into("streamlinecloud-$branch-$version")
    }

    archiveFileName.set("streamlinecloud-$branch-$version.zip")
    destinationDirectory.set(layout.projectDirectory.dir("finished_builds"))

    dependsOn("packageLauncher")
}
repositories {
    mavenCentral()
}
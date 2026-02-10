import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("multiplatform")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("plugin.serialization") version "1.9.22"
}

group = "net.streamlinecloud"
val version: String by rootProject
val branch: String by rootProject

kotlin {
    jvm()
    linuxX64()
    linuxArm64()
    mingwX64()
    // macosArm64()

    linuxX64 {
        binaries {
            executable {
                entryPoint = "net.streamlinecloud.launcher.nativeLinuxMain"
            }
        }
    }

    linuxArm64 {
        binaries {
            executable {
                entryPoint = "net.streamlinecloud.launcher.nativeLinuxMain"
            }
        }
    }

    mingwX64 {
        binaries {
            executable {
                entryPoint = "net.streamlinecloud.launcher.nativeWinMain"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
                implementation("com.squareup.okio:okio:3.16.4")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib")
            }
        }

        val nativeMain by creating {
            dependsOn(commonMain)
        }
        val linuxX64Main by getting {
            dependsOn(nativeMain)
        }
        val linuxArm64Main by getting {
            dependsOn(nativeMain)
        }
        val mingwX64Main by getting {
            dependsOn(nativeMain)
        }
    }
}

val nativeOutRoot: Directory = project.rootProject.layout.projectDirectory.dir("finished_builds/streamlinecloud-launcher")

tasks.register<Copy>("packageNativeLinuxX64") {
    dependsOn("linkReleaseExecutableLinuxX64")
    from(layout.buildDirectory.file("bin/linuxX64/releaseExecutable/streamlinecloud-launcher.kexe"))
    into(nativeOutRoot)
    rename { _ -> "streamlinecloud-launcher-LINUX_X64" }
}

tasks.register<Copy>("packageNativeLinuxArm64") {
    dependsOn("linkReleaseExecutableLinuxArm64")
    from(layout.buildDirectory.file("bin/linuxArm64/releaseExecutable/streamlinecloud-launcher.kexe"))
    into(nativeOutRoot)
    rename { _ -> "streamlinecloud-launcher-LINUX_ARM64" }
}

tasks.register<Copy>("packageNativeMingwX64") {
    dependsOn("linkReleaseExecutableMingwX64")
    from(layout.buildDirectory.file("bin/mingwX64/releaseExecutable/streamlinecloud-launcher.exe"))
    into(nativeOutRoot)
    rename { _ -> "streamlinecloud-launcher-WIN_X64" }
}

tasks.register("packageAllNative") {
    group = "distribution"
    dependsOn("packageNativeLinuxX64", "packageNativeLinuxArm64", "packageNativeMingwX64")
}

tasks.register<ShadowJar>("shadowJar") {
    group = "net.streamlinecloud"
    destinationDirectory.set(project.rootProject.projectDir.resolve("finished_builds/streamlinecloud-launcher"))
    archiveFileName.set("streamlinecloud-launcher-$branch-$version.jar")

    manifest {
        attributes["Main-Class"] = "net.streamlinecloud.launcher.MainKt"
    }

    from(sourceSets["jvmMain"].output)

    val rt = project.configurations.findByName("jvmRuntimeClasspath")
        ?: project.configurations.getByName("runtimeClasspath")

    rt.filter { it.exists() }.forEach { f ->
        if (f.isDirectory) from(f) else from(zipTree(f))
    }
}

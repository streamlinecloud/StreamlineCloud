import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("multiplatform")
}

group = "net.streamlinecloud"
val version: String by rootProject
val branch: String by rootProject

kotlin {
    jvm ()
    linuxArm64()        // Linux
    mingwX64()          // Windows
    //macosArm64()      // macOS

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
        jvmMain.dependencies {
            implementation("org.jetbrains.kotlin:kotlin-stdlib")
        }
    }

}

val nativeOutRoot: Directory = project.rootProject.layout.projectDirectory.dir("finished_builds/streamlinecloud-launcher")

tasks.register<Copy>("packageNativeLinuxX64") {
    dependsOn("linkReleaseExecutableLinuxX64")
    from(layout.buildDirectory.file("bin/linuxX64/releaseExecutable/streamlinecloud-launcher.kexe"))
    into(nativeOutRoot)
    rename { _ -> "streamlinecloud-launcher-LINUX_X64" }
    include("*")
}

tasks.register<Copy>("packageNativeLinuxArm64") {
    dependsOn("linkReleaseExecutableLinuxArm64")
    from(layout.buildDirectory.dir("bin/linuxArm64/releaseExecutable/streamlinecloud-launcher.kexe"))
    into(nativeOutRoot)
    rename { _ -> "streamlinecloud-launcher-LINUX_ARM64" }
    include("*")
}

tasks.register<Copy>("packageNativeMingwX64") {
    dependsOn("linkReleaseExecutableMingwX64")
    from(layout.buildDirectory.dir("bin/mingwX64/releaseExecutable/streamlinecloud-launcher.exe"))
    into(nativeOutRoot)
    rename { _ -> "streamlinecloud-launcher-WIN_X64" }
    include("*")
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
        if (f.isDirectory) {
            from(f)
        } else {
            from(zipTree(f))
        }
    }

}


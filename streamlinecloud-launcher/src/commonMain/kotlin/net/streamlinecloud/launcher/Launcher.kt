package net.streamlinecloud.launcher

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.streamlinecloud.launcher.util.Cli
import net.streamlinecloud.launcher.cli.Question
import net.streamlinecloud.launcher.cli.runWithSpinner
import net.streamlinecloud.launcher.config.ConfigManager
import net.streamlinecloud.launcher.config.LauncherConfig
import net.streamlinecloud.launcher.util.FileUtils

class Launcher {

    val nodeJar = "streamlinecloud-node.jar"
    val backendJar = "streamlinecloud-backend.jar"

    val configManager = ConfigManager()

    fun launch() {

        println()
        println("StreamlineCloud Launcher")
        println("[!] An optional CLI tool that can install, start and manage your StreamlineCloud instance.")
        println()

        if (!configManager.exists()) {
            setup()
            return
        }

        println("launching")

    }

    fun setup() {
        println("Starting setup...")

        val installBackend = Question("Do you want to create a standalone setup, otherwise this installation will be part of an existing node").ask()
        var download = !checkForJar(nodeJar)
        if (installBackend) download = !checkForJar(backendJar)

        if (download) {
            if (Question("Required JAR files are missing. Do you want to download the latest version").ask()) {
                println("Download not implemented yet")
            } else {
                Cli.warn("Quitting the launcher because of missing JAR files")
                return
            }
        }

        configManager.save(LauncherConfig(
            "1",
            "unknown",
            "unknown",
            installBackend,
            backendJar,
            nodeJar,
        ))

        runBlocking {
            try {
                val result = runWithSpinner("Setup completed, restarting...") {
                    delay(3000)
                    "restarted"
                }
                println(result)
                launch()
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }

    fun checkForJar(name: String): Boolean {
        if (!FileUtils.exists("./jar")) return false;
        if (!FileUtils.exists("./jar/$name")) return false;
        return true
    }
    
}
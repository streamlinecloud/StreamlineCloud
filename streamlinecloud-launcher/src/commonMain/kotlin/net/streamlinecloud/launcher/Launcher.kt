package net.streamlinecloud.launcher

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.streamlinecloud.launcher.cli.Question
import net.streamlinecloud.launcher.cli.runWithSpinner
import net.streamlinecloud.launcher.config.ConfigManager

class Launcher {

    fun launch() {

        println()
        println("StreamlineCloud Launcher")
        println("[!] An optional CLI tool that can install, start and manage your StreamlineCloud instance.")
        println()

        val configManager = ConfigManager()

        if (!configManager.exists()) {
            setup()
            return
        }

        runBlocking {
            delay(1200)
            var s = 0L
            for (i in 1..5_000_000) { s += i }
            println("Done: sum=$s")

            try {
                val result = runWithSpinner("Working...") {
                    delay(1200)
                    var s = 0L
                    for (i in 1..5_000_000) { s += i }
                    "Done: sum=$s"
                }
                println(result)
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }

    }

    fun setup() {
        println("Starting setup...")

        var installBackend = Question("Do you want to create a standalone setup, otherwise this installation will be part of an existing node").ask()
        var download = false
    }
    
}
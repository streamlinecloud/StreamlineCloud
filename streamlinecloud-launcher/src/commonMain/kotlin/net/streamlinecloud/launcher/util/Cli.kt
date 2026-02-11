package net.streamlinecloud.launcher.util

object Cli {

    fun info(message: String) {
        println("[!] ${Ansi.RED}$message${Ansi.RESET}")
    }

    fun warn(message: String) {
        println("[!] ${Ansi.YELLOW}$message${Ansi.RESET}")
    }

    fun success(message: String) {
        println("[!] ${Ansi.GREEN}$message${Ansi.RESET}")
    }
}
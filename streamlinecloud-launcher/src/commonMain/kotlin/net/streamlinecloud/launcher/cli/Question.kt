package net.streamlinecloud.launcher.cli

import net.streamlinecloud.launcher.util.Ansi

class Question (var question: String) {

    fun ask(): Boolean {
        println("[?] ${Ansi.RED}$question [yes/no]${Ansi.RESET}")

        fun check(input: String): Boolean {
            return if (input.startsWith("y")) true
            else if (input.startsWith("n")) false
            else check(readln())
        }

        val text = readln()

        return check(text)

    }
}
package net.streamlinecloud.launcher.cli

class Question (var question: String) {

    fun ask(): Boolean {
        println("[?] $question [yes/no]")

        fun check(input: String): Boolean {
            return if (input.startsWith("y")) true
            else if (input.startsWith("n")) false
            else check(readln())
        }

        val text = readln()

        return check(text)

    }
}
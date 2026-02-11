package net.streamlinecloud.launcher.cli

import kotlinx.coroutines.*
import net.streamlinecloud.launcher.util.Ansi
import net.streamlinecloud.launcher.util.Cli

object Console {
    fun flush() { /* nothing: println/print flush automatically in Kotlin stdlib */ }
    fun hideCursor()  { print("\u001B[?25l"); flush() } // ANSI hide cursor
    fun showCursor()  { print("\u001B[?25h"); flush() } // ANSI show cursor
    fun carriageReturn() { print("\r"); flush() }
}

suspend fun <T> runWithSpinner(
    message: String = "",
    frameDelayMs: Long = 80L,
    block: suspend () -> T
): T {
    val frames = listOf("⠋","⠙","⠹","⠸","⠼","⠴","⠦","⠧","⠇","⠏")
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val spinnerJob = scope.launch {
        Console.hideCursor()
        try {
            var i = 0
            while (isActive) {
                val frame = frames[i % frames.size]
                print("\r[$frame] ${Ansi.RED}$message${Ansi.RESET}")
                Console.flush()
                i++
                delay(frameDelayMs)
            }
        } finally {
            print("\r")
            val clear = " ".repeat(message.length + 4)
            print("$clear\r")
            Console.showCursor()
            Console.flush()
        }
    }

    return try {
        val result = withContext(Dispatchers.Default) { block() }
        result
    } catch (e: Throwable) {
        throw e
    } finally {
        spinnerJob.cancelAndJoin()
        scope.cancel()
    }
}

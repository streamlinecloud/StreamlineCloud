package net.streamlinecloud.launcher

//JVM ONLY
fun main() {
    println("LAUNCHING THE JVM VERSION")
    launcher()
}

fun nativeLinuxMain() {
    println("LAUNCHING NATIVE LINUX")
    launcher()
}

fun nativeWinMain() {
    println("LAUNCHING NATIVE WIN")
    launcher()
}

fun launcher() {
    println("Hello World form StreamlineCloud-Launcher!")
    val text = readln()
    println(text)
}
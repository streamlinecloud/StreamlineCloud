package net.streamlinecloud.launcher

var launcher = Launcher()

//JVM ONLY
fun main() {
    println("LAUNCHING THE JVM VERSION")
    launcher.launch()
}

fun nativeLinuxMain() {
    println("LAUNCHING NATIVE LINUX")
    launcher.launch()
}

fun nativeWinMain() {
    println("LAUNCHING NATIVE WIN")
    launcher.launch()
}
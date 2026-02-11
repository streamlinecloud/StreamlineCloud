package net.streamlinecloud.launcher

import net.streamlinecloud.launcher.util.FileUtils

var launcher = Launcher()

fun main() {
    println("LAUNCHING THE JVM VERSION")

    FileUtils.delete("./streamlinecloud-launcher-LINUX")
    FileUtils.delete("./streamlinecloud-launcher-LINUX-ARM")
    FileUtils.delete("./streamlinecloud-launcher-WIN.exe")

    launcher.launch()
}

fun nativeLinuxMain() {
    println("LAUNCHING NATIVE LINUX")

    FileUtils.delete("./streamlinecloud-launcher.jar")
    FileUtils.delete("./streamlinecloud-launcher-LINUX-ARM")
    FileUtils.delete("./streamlinecloud-launcher-WIN.exe")

    launcher.launch()
}

fun nativeLinuxArmMain() {
    println("LAUNCHING NATIVE LINUX ARM")

    FileUtils.delete("./streamlinecloud-launcher.jar")
    FileUtils.delete("./streamlinecloud-launcher-LINUX")
    FileUtils.delete("./streamlinecloud-launcher-WIN.exe")

    launcher.launch()
}

fun nativeWinMain() {
    println("LAUNCHING NATIVE WIN")

    FileUtils.delete("./streamlinecloud-launcher-LINUX")
    FileUtils.delete("./streamlinecloud-launcher-LINUX-ARM")
    FileUtils.delete("./streamlinecloud-launcher.jar")

    launcher.launch()
}
package net.streamlinecloud.launcher.util

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.SYSTEM

object FileUtils {
    private val fs: FileSystem = FileSystem.SYSTEM

    fun exists(pathStr: String): Boolean {
        val path: Path = pathStr.toPath()
        return fs.exists(path)
    }

    fun readText(pathStr: String): String {
        val path: Path = pathStr.toPath()
        return fs.read(path) {
            readUtf8()
        }
    }

    fun writeText(pathStr: String, text: String) {
        val path: Path = pathStr.toPath()
        path.parent?.let { parent ->
            if (!fs.exists(parent)) fs.createDirectories(parent)
        }
        fs.write(path) {
            writeUtf8(text)
        }
    }

    fun delete(pathStr: String) {
        val path: Path = pathStr.toPath()
        if (fs.exists(path)) {
            fs.delete(path)
        }
    }

    fun deleteRecursively(pathStr: String) {
        val path: Path = pathStr.toPath()
        if (fs.exists(path)) {
            fs.deleteRecursively(path)
        }
    }

}

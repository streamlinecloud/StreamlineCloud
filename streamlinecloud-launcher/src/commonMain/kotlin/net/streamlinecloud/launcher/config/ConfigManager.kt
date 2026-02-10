package net.streamlinecloud.launcher.config

import net.streamlinecloud.launcher.util.FileUtils
import net.streamlinecloud.launcher.util.JsonUtils

class ConfigManager {

    val name = "launcher.json"

    fun exists(): Boolean {
        return FileUtils.exists(name)
    }

    fun save(config: LauncherConfig) {
        FileUtils.writeText(
            name,
            JsonUtils.json.encodeToString(LauncherConfig.serializer(), config)
        )
    }

}
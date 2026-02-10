package net.streamlinecloud.launcher.util

import kotlinx.serialization.json.Json

object JsonUtils {

    val json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

}
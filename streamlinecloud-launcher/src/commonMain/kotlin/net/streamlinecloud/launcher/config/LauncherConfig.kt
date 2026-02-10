package net.streamlinecloud.launcher.config

import kotlinx.serialization.Serializable

@Serializable
class LauncherConfig(

    var launcherVersion: String,
    var installedVersion: String,
    var installedBranch: String,

    var launchBackend: Boolean,
    var backendExecutable: String?,
    var nodeExecutable: String

)
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}
rootProject.name = "StreamlineCloud"
include("streamlinecloud-node")
include("streamlinecloud-api")
include("streamlinecloud-mc")
include("streamlinecloud-launcher")
include("streamlinecloud-backend")
include("streamlinecloud-client")

project(":streamlinecloud-backend").name = "streamlinecloud-backend"
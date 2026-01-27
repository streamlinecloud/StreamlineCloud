
plugins {
    id("java")
}

group = "net.streamlinecloud"
val version: String by rootProject
val branch: String by rootProject

repositories {
    mavenCentral()
}

dependencies {
    implementation("commons-io:commons-io:2.16.1")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register("compileAll") {
    group = "StreamlineCloud"

    dependsOn(":streamlinecloud-api:shadowJar",
        ":streamlinecloud-mc:shadowJar",
        ":streamlinecloud-node:shadowJar",
        ":streamlinecloud-launcher:shadowJar"
    )
}

plugins {
    id("java")
}

group = "de.theflames"
version = "1.0-SNAPSHOT"

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

tasks.register("Make All Products") {
    group = "StreamlineCloud"

    dependsOn(":streamlinecloud-api:Make API Project", ":streamlinecloud-main:Make MAIN Project", ":streamlinecloud-mc:Make MC Project")
}
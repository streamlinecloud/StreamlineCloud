plugins {
    id("java")
}

group = "de.theflames"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register("Make All Products") {
    group = "StreamlineCloud"

    dependsOn(":streamlinecloud-api:Make Project", ":streamlinecloud-main:Make Project", ":streamlinecloud-mc:Make Project")
}
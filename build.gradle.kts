plugins {
    kotlin("jvm") version "2.5.0-Beta2"
}

group = "net.typho"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://typho.net/maven")
}

dependencies {
    implementation("com.formdev:flatlaf:3.8")
    implementation(kotlin("analysis-api-standalone-surface"))
    implementation(kotlin("analysis-api-standalone-implementation"))
    implementation("net.typho:maven_util:1.5.1")
}

kotlin {
    jvmToolchain(21)
}
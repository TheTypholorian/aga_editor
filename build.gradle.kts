plugins {
    kotlin("jvm") version "2.4.21"
}

group = "net.typho"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://typho.net/maven")
    maven("https://www.jetbrains.com/intellij-repository/releases")
    maven("https://cache-redirector.jetbrains.com/intellij-dependencies")
}

dependencies {
    implementation("com.formdev:flatlaf:3.8")
    implementation("net.typho:maven_util:1.5.1")
    implementation(project(":compilation_util"))
}

kotlin {
    jvmToolchain(21)
}
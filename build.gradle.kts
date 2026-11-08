plugins {
    kotlin("jvm") version "2.4.21"
}

group = "net.typho"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.formdev:flatlaf:3.8")
}

kotlin {
    jvmToolchain(21)
}
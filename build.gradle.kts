plugins {
    kotlin("jvm") version "2.5.0-Beta2"
}

group = "net.typho"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.formdev:flatlaf:3.8")
    implementation(kotlin("analysis-api-standalone-surface"))
    implementation(kotlin("analysis-api-standalone-implementation"))
}

kotlin {
    jvmToolchain(21)
}
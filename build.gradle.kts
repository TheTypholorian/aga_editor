plugins {
    kotlin("jvm") version "2.4.21"
}

group = "net.typho"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies/")
}

dependencies {
    implementation("com.formdev:flatlaf:3.8")
    implementation(kotlin("compiler"))
    implementation("org.jetbrains.kotlin:analysis-api-for-ide:2.4.21") {
        isTransitive = false
    }
}

kotlin {
    jvmToolchain(21)
}
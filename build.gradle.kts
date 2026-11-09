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
}

kotlin {
    jvmToolchain(21)

    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}
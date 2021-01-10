import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
    id("com.github.johnrengelman.shadow") version("6.0.0")
    maven
}

allprojects {
    repositories {
        mavenCentral()
        jcenter()
        maven("https://jitpack.io")
    }
}

group = "me.boss"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")

    compileOnly("net.dv8tion:JDA:4.2.0_225")

    testImplementation(kotlin("test-junit"))
    testImplementation("net.dv8tion:JDA:4.2.0_225")
    testImplementation("ch.qos.logback:logback-classic:1.2.3")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
plugins {
    kotlin("jvm") version "1.9.10"
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = "com.pattexpattex"

    repositories {
        mavenCentral()
        maven("https://jitpack.io/")
    }

    kotlin {
        jvmToolchain(8)
    }

    tasks {
        test {
            useJUnitPlatform()
        }
    }
}

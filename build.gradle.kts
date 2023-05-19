import java.net.URI

plugins {
    kotlin("jvm") version "1.8.21" apply false
}

allprojects {

    group = "com.vitekkor"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven { url = URI("https://jitpack.io") }
    }
}

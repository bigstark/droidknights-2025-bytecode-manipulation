plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("kapt") version "2.0.21"
    id("java-gradle-plugin")
    id("maven-publish")
}

group = "com.bigstark.example"
version = "1.0.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.0.21")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api:2.0.21")
    
    implementation("org.ow2.asm:asm:9.7.1")
    implementation("org.ow2.asm:asm-util:9.7.1")
    implementation("org.ow2.asm:asm-commons:9.7.1")
    
    compileOnly("com.google.auto.service:auto-service-annotations:1.1.1")
    kapt("com.google.auto.service:auto-service:1.1.1")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        allWarningsAsErrors = false
    }
}

gradlePlugin {
    plugins {
        create("composable-click-log") {
            id = "composable-click-log"
            implementationClass = "com.bigstark.example.plugin.ComposableClickLogPlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = "com.bigstark.example"
            artifactId = "composable-click-log"
            version = "1.0.0"
        }
    }
} 
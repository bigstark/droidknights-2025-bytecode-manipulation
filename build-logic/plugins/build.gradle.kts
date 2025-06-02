plugins {
    `java-gradle-plugin`
    kotlin("jvm") version libs.versions.kotlin
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin.api)
    implementation(gradleKotlinDsl())
    implementation(libs.asm.util)
}

gradlePlugin {
    plugins {
        create("click-count-plugin") {
            id = "click-count"
            implementationClass = "com.bigstark.example.plugin.count.ClickCountPlugin"
        }

        create("click-log-plugin") {
            id = "click-log"
            implementationClass = "com.bigstark.example.plugin.log.ClickLogPlugin"
        }
    }
}
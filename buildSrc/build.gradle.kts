plugins {
    `kotlin-dsl`
}

repositories {
    maven("https://maven.aliyun.com/repository/central")
    maven("https://maven.aliyun.com/repository/gradle-plugin")
}

dependencies {
    implementation("io.github.gradle-nexus:publish-plugin:1.1.0")
    implementation(gradleApi())
}
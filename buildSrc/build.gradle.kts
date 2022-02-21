plugins {
    `kotlin-dsl`
}

repositories {
    maven("https://maven.aliyun.com/repository/central")
//    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("io.github.gradle-nexus:publish-plugin:1.1.0")
    implementation(gradleApi())
}
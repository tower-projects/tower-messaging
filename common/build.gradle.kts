plugins {
    `java-library`
    id("java-conventions")
    id("maven-deploy")
}

description = "tower commons"

dependencies {
    implementation("com.google.code.findbugs:jsr305")
    api("com.google.guava:guava")
}
plugins {
    `java-library`
    id("java-conventions")
    id("maven-deploy")
}

description = "tower commons"

dependencies {
    implementation("org.jboss.logging:jboss-logging")

    api("com.google.guava:guava")

    api("org.apache.commons:commons-lang3")
    api("org.apache.commons:commons-collections4")
}
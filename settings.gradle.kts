pluginManagement {
    val quarkusVersion: String by settings
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("io.quarkus") version quarkusVersion
    }
}
rootProject.name = "tower-messaging"

include(
        "messaging-dependencies"
)

include(
        "common",
        "messaging-core"
)

include(
        "support:quarkus:integration-tests",
        "support:quarkus:tower-quarkus",
        "support:quarkus:tower-quarkus-deployment"
)
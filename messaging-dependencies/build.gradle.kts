plugins {
    `java-platform`
    id("java-conventions")
    id("maven-deploy")
}

javaPlatform {
    allowDependencies()
}

val quarkusVersion: String by project

dependencies {
    api(platform("io.quarkus.platform:quarkus-bom:${quarkusVersion}"))
    constraints {
        api("io.quarkus:quarkus-extension-processor:${quarkusVersion}")
        api(project(":common"))
        api(project(":messaging-core"))
        api(project(":support:quarkus:tower-quarkus"))
        api(project(":support:quarkus:tower-quarkus-deployment"))
    }
}
plugins {
    `java-library`
    id("java-conventions")
    id("maven-deploy")
}


tasks.processResources {
    filter<org.apache.tools.ant.filters.ReplaceTokens>("tokens" to mapOf("version" to project.version))
}

java {
    registerFeature("metrics") {
        usingSourceSet(sourceSets.main.get())
    }
}

dependencies {
    annotationProcessor("io.quarkus:quarkus-extension-processor")

    implementation("io.quarkus:quarkus-core")
    implementation("io.quarkus:quarkus-arc")
    compileOnly("io.quarkus:quarkus-micrometer")

    api(project(":messaging-core"))
    api(project(":graphql-adapter"))

}
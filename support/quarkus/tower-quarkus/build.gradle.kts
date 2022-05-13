plugins {
    `java-library`
    id("java-conventions")
    id("maven-deploy")
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

//    compileOnly("io.quarkus:quarkus-micrometer")

    api(project(":messaging-cdi"))
    api(project(":schema:schema-model"))

}
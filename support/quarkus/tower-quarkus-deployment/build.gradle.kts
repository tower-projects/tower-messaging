plugins {
    `java-library`
    id("java-conventions")
    id("maven-deploy")
}

dependencies {
    annotationProcessor("io.quarkus:quarkus-extension-processor")

    api(project(":support:quarkus:tower-quarkus"))
    api("io.quarkus:quarkus-core-deployment")
    api("io.quarkus:quarkus-arc-deployment")
//    api "io.quarkus:quarkus-smallrye-health-spi"

    api(project(":schema:schema-builder"))

    testImplementation("io.quarkus:quarkus-junit5-internal")
}

tasks.test {
    systemProperties["java.util.logging.manager"] = "org.jboss.logmanager.LogManager"
    systemProperties["platform.quarkus.native.builder-image"] = false
}
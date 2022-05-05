plugins {
    `java-library`
    id("java-conventions")
    id("maven-deploy")
}

dependencies {
    implementation("org.jboss.logging:jboss-logging")
    compileOnly("org.jboss.logging:jboss-logging-annotations")

    annotationProcessor("org.jboss.logging:jboss-logging")
    annotationProcessor("org.jboss.logging:jboss-logging-annotations")
    annotationProcessor("org.jboss.logging:jboss-logging-processor")

    implementation(project(":common"))
    implementation(project(":schema:schema-model"))

    testImplementation("org.jboss.logging:jboss-logging")
    testImplementation("org.jboss:jandex")
    testImplementation(project(":schema:schema-builder"))
}
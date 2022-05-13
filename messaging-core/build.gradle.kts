plugins {
    `java-library`
    id("java-conventions")
    id("maven-deploy")
}

dependencies {
    implementation("org.jboss.logging:jboss-logging")

    implementation(project(":common"))
    implementation(project(":schema:schema-model"))

    testImplementation("org.jboss:jandex")
    testImplementation(project(":schema:schema-builder"))
}
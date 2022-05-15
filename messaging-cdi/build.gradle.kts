plugins {
    `java-library`
    id("java-conventions")
    id("maven-deploy")
}

dependencies {
    implementation("org.jboss.logging:jboss-logging")

    api(project(":common"))
    api(project(":messaging-core"))
    implementation(project(":schema:schema-model"))

    compileOnly("jakarta.enterprise:jakarta.enterprise.cdi-api")
    compileOnly("org.eclipse.microprofile.context-propagation:microprofile-context-propagation-api")

    testImplementation("org.jboss.logging:jboss-logging")
    testImplementation("org.jboss:jandex")
    testImplementation(project(":schema:schema-builder"))
}
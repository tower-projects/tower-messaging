plugins {
    `java-library`
    id("java-conventions")
    id("maven-deploy")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":messaging-core"))
    api("io.smallrye:smallrye-graphql")

//    api("io.quarkus:quarkus-smallrye-graphql")
    implementation("org.jboss.logging:jboss-logging-annotations")
    implementation("org.jboss.logging:jboss-logging")
    implementation("io.smallrye:smallrye-graphql-schema-builder")

    implementation("org.jboss:jandex")
//
    testImplementation("org.glassfish:jakarta.json")
    testImplementation("jakarta.json.bind:jakarta.json.bind-api")

    testImplementation("org.eclipse:yasson")

}
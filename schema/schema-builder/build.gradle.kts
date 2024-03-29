plugins {
    `java-library`
    id("java-conventions")
    id("maven-deploy")
}

description = "tower schema"

dependencies {
    implementation("org.jboss.logging:jboss-logging")

    testCompileOnly("org.jboss.logging:jboss-logging")
    implementation(project(":common"))
    implementation(project(":schema:schema-model"))
    implementation("org.jboss:jandex")

//    testImplementation(project(":core"))
}
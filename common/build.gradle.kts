plugins {
    `java-library`
    id("java-conventions")
    id("maven-deploy")
}

description = "tower commons"

dependencies {
    implementation("org.jboss.logging:jboss-logging")
    compileOnly("org.jboss.logging:jboss-logging-annotations")

    annotationProcessor("org.jboss.logging:jboss-logging")
    annotationProcessor("org.jboss.logging:jboss-logging-annotations")
    annotationProcessor("org.jboss.logging:jboss-logging-processor")

//    api("com.google.code.findbugs:jsr305")
    api("com.google.guava:guava")

    api("org.apache.commons:commons-lang3")
    api("org.apache.commons:commons-collections4")
}
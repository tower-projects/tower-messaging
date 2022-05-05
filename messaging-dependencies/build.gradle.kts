plugins {
    `java-platform`
    id("java-conventions")
    id("maven-deploy")
}

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        api("org.jboss.logging:jboss-logging:3.4.3.Final")
        api("org.jboss.logging:jboss-logging-annotations:2.2.1.Final")
        api("org.jboss.logging:jboss-logging-processor:2.2.1.Final")

        api("jakarta.enterprise:jakarta.enterprise.cdi-api:2.0.2")

        api("com.google.guava:guava:31.1-jre")
        api("com.graphql-java:graphql-java:18.0")
        api("io.smallrye:smallrye-graphql:1.4.4")
        api("io.smallrye:smallrye-graphql-schema-builder:1.4.4")
        api("org.apache.commons:commons-collections4:4.4")
        api("org.apache.commons:commons-lang3:3.12.0")
        api("org.jboss:jandex:2.4.2.Final")
        api(project(":common"))

        api("org.junit.jupiter:junit-jupiter-engine:5.8.2")
        api("org.assertj:assertj-core:3.22.0")
        api(project(":messaging-core"))
        api(project(":support:quarkus:tower-quarkus"))
        api(project(":support:quarkus:tower-quarkus-deployment"))
    }
}
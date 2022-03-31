plugins {
    `java-platform`
    id("java-conventions")
    id("maven-deploy")
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(platform("io.quarkus.platform:quarkus-bom:${rootProject.extra["quarkus"]}"))
    api(platform("io.smallrye:smallrye-graphql-parent:1.4.4"))
    constraints {
        api("io.quarkus:quarkus-extension-processor:${rootProject.extra["quarkus"]}")
        api("com.graphql-java:graphql-java:18.0")
        api("io.smallrye:smallrye-graphql")
        api("io.smallrye:smallrye-graphql-schema-builder")
        api(project(":common"))
        api(project(":messaging-core"))
        api(project(":support:quarkus:tower-quarkus"))
        api(project(":support:quarkus:tower-quarkus-deployment"))
    }
}
plugins {
    `java-library`
    id("java-conventions")
    id("maven-deploy")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":messaging-core"))
    implementation("io.smallrye:smallrye-graphql")
}
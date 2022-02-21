plugins {
    `java-library`
    id("java-conventions")
    id("maven-deploy")
}

dependencies{
    implementation(project(":common"))
}
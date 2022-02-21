plugins {
    id("java")
    id("io.quarkus")
    id("java-conventions")
}

repositories {
//    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    mavenLocal()
}

dependencies {

    implementation("io.quarkus:quarkus-arc")
    implementation(project(":support:quarkus:tower-quarkus"))
    implementation(project(":support:quarkus:tower-quarkus-deployment"))

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")

}

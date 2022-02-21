plugins{
    `java-library`
    id("java-conventions")
    id("maven-deploy")
}

description = "tower commons"

dependencies {
    implementation ("com.google.code.findbugs:jsr305")
    api ("org.apache.commons:commons-lang3")
    api ("org.slf4j:slf4j-api")
    api ("com.google.guava:guava")
}
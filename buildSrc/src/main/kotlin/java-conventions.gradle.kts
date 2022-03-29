plugins.withType(JavaBasePlugin::class) {
    project.setProperty("sourceCompatibility", JavaVersion.VERSION_11)
}

plugins.withType(JavaPlugin::class) {
    dependencies {
        add(JavaPlugin.TEST_RUNTIME_ONLY_CONFIGURATION_NAME, "org.junit.platform:junit-platform-launcher")
        add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, "org.junit.jupiter:junit-jupiter-engine")
        add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, "org.junit.jupiter:junit-jupiter-api")
        add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, "org.assertj:assertj-core")
    }
}

val dep = project(":messaging-dependencies")
val enforced = dependencies.platform(dep)
if (project.name != dep.name) {
    plugins.withType(JavaPlugin::class) {
        dependencies.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, enforced)
        configurations.getByName(JavaPlugin.ANNOTATION_PROCESSOR_CONFIGURATION_NAME) {
            dependencies.add(enforced)
        }
    }
    plugins.withType(WarPlugin::class) {
        dependencies.add(WarPlugin.PROVIDED_RUNTIME_CONFIGURATION_NAME, enforced)
    }
}

tasks.withType(Test::class) {
    useJUnitPlatform()
    maxHeapSize = "1024M"
}

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
    sourceCompatibility = JavaVersion.VERSION_11.majorVersion
    targetCompatibility = JavaVersion.VERSION_11.majorVersion
}

tasks.withType(Javadoc::class) {
    options {
        encoding("UTF-8").source(JavaVersion.VERSION_11.majorVersion)
        (this as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    }
}
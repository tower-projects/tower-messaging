subprojects {
    plugins.withType(JavaPlugin::class) {

        dependencies.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME,
                dependencies.platform("io.quarkus.platform:quarkus-bom:${rootProject.extra["quarkus"]}"))

        dependencies.add(JavaPlugin.ANNOTATION_PROCESSOR_CONFIGURATION_NAME, "io.quarkus:quarkus-extension-processor:${rootProject.extra["quarkus"]}")
    }
}
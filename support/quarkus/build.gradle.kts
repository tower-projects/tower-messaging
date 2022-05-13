subprojects {
    plugins.withType(JavaPlugin::class) {

        dependencies.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME,
                dependencies.platform("io.quarkus.platform:quarkus-bom:${rootProject.extra["quarkus"]}"))

        dependencies.add(JavaPlugin.ANNOTATION_PROCESSOR_CONFIGURATION_NAME, "io.quarkus:quarkus-extension-processor:${rootProject.extra["quarkus"]}")
    }

    tasks.withType(ProcessResources::class) {
        filter<org.apache.tools.ant.filters.ReplaceTokens>("tokens" to mapOf("version" to project.version, "quarkus" to rootProject.extra["quarkus"]))
    }


}
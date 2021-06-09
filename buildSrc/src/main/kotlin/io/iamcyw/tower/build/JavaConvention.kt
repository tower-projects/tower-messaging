package io.iamcyw.tower.build

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.WarPlugin
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.api.tasks.testing.Test
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.kotlin.dsl.project
import org.gradle.kotlin.dsl.withType

class JavaConvention {

    fun apply(project: Project) {
        project.plugins.withType(JavaBasePlugin::class.java) {
            project.setProperty("sourceCompatibility", JavaVersion.VERSION_11)
            configureJavaCompileConventions(project)
            configureEnforcedPlatform(project)
            configureTestConventions(project)
            configureJavadocConventions(project)
        }
    }

    private fun configureTestConventions(project: Project) {
        project.tasks.withType(Test::class) {
            this.useJUnitPlatform()
            this.setMaxHeapSize("1024M")
        }
        project.plugins.withType(JavaPlugin::class) {
            project.dependencies.add(
                    JavaPlugin.TEST_RUNTIME_ONLY_CONFIGURATION_NAME,
                    "org.junit.platform:junit-platform-launcher"
            )
            project.dependencies.add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME,
                    "org.junit.jupiter:junit-jupiter-engine")
            project.dependencies.add(
                    JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME,
                    "org.junit.jupiter:junit-jupiter-api"
            )
            project.dependencies.add(
                    JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME,
                    "org.assertj:assertj-core"
            )
        }
    }

    private fun configureJavaCompileConventions(project: Project) {
        project.tasks.withType(JavaCompile::class) {
            this.options.encoding = "UTF-8"
            this.sourceCompatibility = JavaVersion.VERSION_11.majorVersion
            this.targetCompatibility = JavaVersion.VERSION_11.majorVersion
        }
    }

    private fun configureJavadocConventions(project: Project) {
        project.tasks.withType(Javadoc::class) {
            (this.options as StandardJavadocDocletOptions).addStringOption(
                    "Xdoclint:none",
                    "-quiet"
            )
            options.source(JavaVersion.VERSION_11.majorVersion).encoding("UTF-8")
        }
    }

    private fun configureEnforcedPlatform(project: Project) {
        val dependencyProject = project.dependencies.project(":tower-messaging-dependencies")
        if (project.name != dependencyProject.name) {
            project.plugins.withType(JavaPlugin::class) {
                project.dependencies.add(
                        JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME,
                        project.dependencies.enforcedPlatform(dependencyProject)
                )
                project.configurations.getByName(JavaPlugin.ANNOTATION_PROCESSOR_CONFIGURATION_NAME).run {
                    dependencies.add(
                            project.dependencies.enforcedPlatform(dependencyProject)
                    )
                }
            }
            project.plugins.withType(WarPlugin::class) {
                project.dependencies.add(
                        WarPlugin.PROVIDED_RUNTIME_CONFIGURATION_NAME,
                        project.dependencies.enforcedPlatform(dependencyProject)
                )
            }
        }
    }

}
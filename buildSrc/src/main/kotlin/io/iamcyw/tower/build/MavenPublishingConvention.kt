package io.iamcyw.tower.build

import org.gradle.api.Project
import org.gradle.api.attributes.Usage
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPomDeveloperSpec
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import java.net.URI

class MavenPublishingConvention {


    fun apply(project: Project) {
        project.plugins.withType(MavenPublishPlugin::class).all {
            val publishing = project.extensions.getByType(PublishingExtension::class)
            if (project.hasProperty("aliRepositoryUsername") && project.hasProperty("aliRepositoryPassword")) {
                val username = project.property("aliRepositoryUsername")
                val password = project.property("aliRepositoryPassword")
                val url = if (project.version.toString().endsWith(suffix = "-SNAPSHOT", ignoreCase = true)) project.property("alisnapshotRepository") else project.property("aliRepository")

                publishing.repositories.maven {
                    this.url = URI.create(url.toString())
                    this.name = "deployment"
                    this.credentials.username = username.toString()
                    this.credentials.password = password.toString()
                }
            }

            publishing.publications.withType(MavenPublication::class).all {
                customizeMavenPublication(this, project)
            }
            project.plugins.withType(JavaPlugin::class).all {
                with(project.extensions.getByType(JavaPluginExtension::class)) {
                    withJavadocJar()
                    withSourcesJar()
                }
            }
        }
    }

    private fun customizeMavenPublication(publication: MavenPublication, project: Project) {
        customizePom(publication.pom, project)
        project.plugins.withType(JavaPlugin::class).all {
            customizeJavaMavenPublication(publication)
        }
    }

    private fun customizeJavaMavenPublication(publication: MavenPublication) {
        publication.versionMapping {
            this.usage(Usage.JAVA_API) {
                this.fromResolutionOf(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)
            }
        }
        publication.versionMapping {
            this.usage(Usage.JAVA_RUNTIME) {
                this.fromResolutionResult()
            }
        }
    }

    private fun customizePom(pom: MavenPom, project: Project) {
        pom.name to project.provider(project::getName)
        pom.description to project.provider(project::getDescription)
        pom.developers(this::customizeDevelopers)
    }

    private fun customizeDevelopers(developerSpec: MavenPomDeveloperSpec) {
        developerSpec.developer {
            this.name to "Johnson Wang"
            this.organization to "Tower Projects"
        }
    }
}
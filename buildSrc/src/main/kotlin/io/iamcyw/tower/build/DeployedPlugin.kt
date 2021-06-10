package io.iamcyw.tower.build

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlatformPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

class DeployedPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply(MavenPublishPlugin::class)
        val mavenPublication = with(target.extensions.getByType(PublishingExtension::class)) {
            this.publications.create("maven", MavenPublication::class)
        }

        mavenPublication.artifactId = "tower-" + mavenPublication.artifactId

        target.afterEvaluate {
            this.plugins.withType(JavaPlugin::class).all {
                if (target.tasks.getByName(JavaPlugin.JAR_TASK_NAME).enabled) {
                    target.components.matching {
                        it.name.equals("java")
                    }.all {
                        mavenPublication.from(this)
                    }
                }
            }
        }

        target.plugins.withType(JavaPlatformPlugin::class).all {
            target.components.matching {
                it.name.equals("javaPlatform")
            }.all {
                mavenPublication.from(this)
            }
        }

    }
}
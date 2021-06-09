package io.iamcyw.tower.build

import org.gradle.api.Plugin
import org.gradle.api.Project

class ConventionsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        JavaConvention().apply(target)
        MavenPublishingConvention().apply(target)
    }

}
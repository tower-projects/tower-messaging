plugins {
    id("io.github.gradle-nexus.publish-plugin")
}

nexusPublishing {
    repositories {
        sonatype {  //only for users registered in Sonatype after 24 Feb 2021
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(project.property("MAVEN_USERNAME").toString())
            password.set(project.property("MAVEN_PASSWORD").toString())
        }
    }
}

allprojects {

    group = "io.iamcyw.tower"
    description = "tower projects"

    repositories {
        maven("https://maven.aliyun.com/repository/central")
//        mavenCentral()
//        gradlePluginPortal()
    }

    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.MINUTES)
    }
}
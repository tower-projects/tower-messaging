plugins {
    id("io.quarkus") version "2.9.0.Final" apply false
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

repositories {
    maven("https://maven.aliyun.com/repository/central")
    maven("https://maven.aliyun.com/repository/gradle-plugin")
}

ext {
    set("quarkus", "2.9.0.Final")
}

allprojects {

    group = "io.iamcyw.tower"
    description = "tower projects"

    repositories {
        maven("https://maven.aliyun.com/repository/central")
    }

    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.MINUTES)
    }
}
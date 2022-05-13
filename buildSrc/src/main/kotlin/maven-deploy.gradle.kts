plugins {
    `maven-publish`
    signing
}

plugins.withType(JavaPlugin::class) {
    extensions.getByType(JavaPluginExtension::class).withJavadocJar()
    extensions.getByType(JavaPluginExtension::class).withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            plugins.withType(JavaPlugin::class).all {
                if ((tasks.getByName(JavaPlugin.JAR_TASK_NAME) as Jar).isEnabled) {
                    components.matching { element -> element.name == "java" }.all {
                        from(this)
                    }
                }
            }

            plugins.withType(JavaPlatformPlugin::class).all {
                components.matching { element -> element.name == "javaPlatform" }.all {
                    from(this)
                }
            }

            pom {
                name.set(project.name)
                description.set(project.description)
                url.set("https://github.com/tower-projects/tower-messaging")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("iamcyw")
                        name.set("yucheng w")
                    }
                }
                scm {
                    connection.set("scm:https://github.com/tower-projects/tower-messaging.git")
                    developerConnection.set("scm:git@github.com:tower-projects/tower-messaging.git")
                    url.set("https://github.com/tower-projects/tower-messaging")
                }
            }
        }
    }
}

//signing {
//    setRequired { !project.version.toString().endsWith("-SNAPSHOT") }
//    if (System.getenv().containsKey("MAVEN_SIGNING_KEY")) {
//        useInMemoryPgpKeys(System.getenv("MAVEN_SIGNING_KEY").toString(), System.getenv("MAVEN_SIGNING_PASSWORD").toString())
//    } else {
//        useGpgCmd()
//    }
//    sign(publishing.publications["maven"])
//}
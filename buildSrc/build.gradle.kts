plugins {
    `kotlin-dsl`
}

repositories {
    maven(uri("https://maven.aliyun.com/repository/public"))
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("Conventions") {
            this.id = "conventions"
            this.implementationClass = "io.iamcyw.tower.build.ConventionsPlugin"
        }
        create("DeployedPlugin") {
            this.id = "deployed"
            this.implementationClass = "io.iamcyw.tower.build.DeployedPlugin"
        }
    }
}
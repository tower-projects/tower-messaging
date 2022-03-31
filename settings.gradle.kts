rootProject.name = "tower-messaging"

include(
        "messaging-dependencies"
)

include(
        "common",
        "messaging-core",
        "graphql-adapter"
)

include(
        "support:quarkus:integration-tests",
        "support:quarkus:tower-quarkus",
        "support:quarkus:tower-quarkus-deployment"
)
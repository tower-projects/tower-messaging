rootProject.name = "tower-messaging"

include(
        "messaging-dependencies"
)

include(
        "schema:schema-builder",
        "schema:schema-model"
)

include(
        "common",
        "messaging-core",
        "messaging-cdi"
)

include(
        "support:quarkus:integration-tests",
        "support:quarkus:tower-quarkus",
        "support:quarkus:tower-quarkus-deployment"
)
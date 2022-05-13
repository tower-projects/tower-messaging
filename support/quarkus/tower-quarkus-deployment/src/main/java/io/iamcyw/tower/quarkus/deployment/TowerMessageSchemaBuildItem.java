package io.iamcyw.tower.quarkus.deployment;

import io.iamcyw.tower.schema.model.Schema;
import io.quarkus.builder.item.SimpleBuildItem;

public final class TowerMessageSchemaBuildItem extends SimpleBuildItem {

    private final Schema schema;

    public TowerMessageSchemaBuildItem(Schema schema) {
        this.schema = schema;
    }

    public Schema getSchema() {
        return schema;
    }

}

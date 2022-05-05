package io.iamcyw.tower.quarkus.deployment;

import io.quarkus.builder.item.SimpleBuildItem;

import java.util.Map;

final class TowerMessageIndexBuildItem extends SimpleBuildItem {

    private final Map<String, byte[]> modifiedClases;

    public TowerMessageIndexBuildItem(Map<String, byte[]> modifiedClases) {
        this.modifiedClases = modifiedClases;
    }

    public Map<String, byte[]> getModifiedClases() {
        return modifiedClases;
    }
}

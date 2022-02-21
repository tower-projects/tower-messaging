package io.iamcyw.tower.quarkus.runtime;

import java.util.Map;

public class DomainNameMappings {
    public Map<String, Class<?>> domainMaps;

    public DomainNameMappings(Map<String, Class<?>> domainMaps) {
        this.domainMaps = domainMaps;
    }

    public Class<?> getDomain(String name) {
        return domainMaps.get(name);
    }

}

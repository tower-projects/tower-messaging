package io.iamcyw.tower;

import java.util.function.Supplier;

public interface Assert {

    static <T> T assertNotNull(T object) {
        if (object != null) {
            return object;
        }
        throw new IllegalArgumentException(TowerMessageCommonMessages.log.objectRequiredNotNull());
    }

    static <T> T assertNotNull(T object, String name) {
        if (object != null) {
            return object;
        }
        throw TowerMessageCommonMessages.log.nullParam(name);
    }

    static <T> T assertNotNull(T object, Supplier<String> value) {
        if (object != null) {
            return object;
        }
        throw new IllegalArgumentException(value.get());
    }

}
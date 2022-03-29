package io.iamcyw.tower.responsetype;

import java.util.List;

public abstract class ResponseTypes {

    private ResponseTypes() {
        // Utility class
    }

    public static <R> ResponseType<R> instanceOf(Class<R> type) {
        return new InstanceResponseType<>(type);
    }

    public static <R> ResponseType<List<R>> multipleInstancesOf(Class<R> type) {
        return new MultipleInstancesResponseType<>(type);
    }

}

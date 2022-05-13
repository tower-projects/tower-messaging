package io.iamcyw.tower.messaging.responsetype;

import io.iamcyw.tower.messaging.spi.ClassloadingService;
import io.iamcyw.tower.schema.model.WrapperType;

import java.util.List;

public abstract class ResponseTypes {

    private ResponseTypes() {
        // Utility class
    }

    public static <R> ResponseType<R> instanceOf(Class<R> type) {
        return new InstanceResponseType<R>(type);
    }

    public static <R> ResponseType<List<R>> listInstanceOf(Class<R> type) {
        return new ListInstancesResponseType<>(type);
    }

    public static <R> ResponseType<R[]> arrayInstanceOf(Class<R> type) {
        return new ArrayInstancesResponseType<>(type);
    }

    public static ResponseType<Object> instanceOf(String className, WrapperType wrapperType) {
        return new ResponseType<>() {
            @Override
            public Class<?> responseMessagePayloadType() {
                return ClassloadingService.get().loadClass(className);
            }

            @Override
            public WrapperType responseMessagePayloadWrapperType() {
                return wrapperType;
            }
        };
    }

    public static ResponseType<Void> voidInstanceOf() {
        return new VoidResponseType();
    }

}

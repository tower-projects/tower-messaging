package io.iamcyw.tower.messaging.responsetype;

import io.iamcyw.tower.StringPool;
import io.iamcyw.tower.messaging.spi.ClassloadingService;
import io.iamcyw.tower.schema.model.WrapperType;

public abstract class ResponseTypes {

    private ResponseTypes() {
        // Utility class
    }

    public static <R> ResponseType instanceOf(Class<R> type) {
        return new InstanceResponseType(type);
    }

    public static ResponseType listInstanceOf(Class<?> type) {
        return new ListInstancesResponseType<>(type);
    }

    public static ResponseType arrayInstanceOf(Class<?> type) {
        return new ArrayInstancesResponseType(type);
    }

    public static ResponseType instanceOf(String className, WrapperType wrapperType) {
        return new ResponseType() {
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

    public static ResponseType voidInstanceOf() {
        return new VoidResponseType();
    }

    public static ResponseType anyInstanceOf() {
        return new ResponseType() {

            @Override
            public Class<?> responseMessagePayloadType() {
                return Object.class;
            }

            @Override
            public WrapperType responseMessagePayloadWrapperType() {
                return WrapperType.EMPTY;
            }

            @Override
            public String name() {
                return StringPool.ASTERISK;
            }
        };
    }

}

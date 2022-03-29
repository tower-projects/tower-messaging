package io.iamcyw.tower.responsetype;

import java.io.Serializable;
import java.lang.reflect.Type;

public interface ResponseType<R> extends Serializable {

    boolean matches(Type responseType);

    @SuppressWarnings("unchecked")
    default R convert(Object response) {
        return (R) response;
    }

    Class<R> responseMessagePayloadType();

}

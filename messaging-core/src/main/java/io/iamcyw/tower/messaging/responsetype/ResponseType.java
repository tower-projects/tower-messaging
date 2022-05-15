package io.iamcyw.tower.messaging.responsetype;

import io.iamcyw.tower.schema.model.WrapperType;

import java.io.Serializable;

public interface ResponseType extends Serializable {

    Class<?> responseMessagePayloadType();

    WrapperType responseMessagePayloadWrapperType();

    default String name() {
        return responseMessagePayloadType().getName();
    }

}

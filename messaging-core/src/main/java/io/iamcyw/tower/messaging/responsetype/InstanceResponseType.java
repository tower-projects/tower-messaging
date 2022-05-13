package io.iamcyw.tower.messaging.responsetype;

import io.iamcyw.tower.schema.model.WrapperType;

public class InstanceResponseType<R> extends AbstractResponseType<R> {

    public InstanceResponseType(Class<R> expectedResponseType) {
        super(expectedResponseType);
    }

    @Override
    public WrapperType responseMessagePayloadWrapperType() {
        return WrapperType.EMPTY;
    }

}
package io.iamcyw.tower.messaging.responsetype;

import io.iamcyw.tower.schema.model.WrapperType;

public class InstanceResponseType extends AbstractResponseType {

    public InstanceResponseType(Class<?> expectedResponseType) {
        super(expectedResponseType);
    }

    @Override
    public WrapperType responseMessagePayloadWrapperType() {
        return WrapperType.EMPTY;
    }

}
package io.iamcyw.tower.messaging.responsetype;


import io.iamcyw.tower.schema.model.WrapperType;

public class ArrayInstancesResponseType<R> extends AbstractResponseType<R[]> {

    protected ArrayInstancesResponseType(Class<R> expectedResponseType) {
        super(expectedResponseType);
    }

    @Override
    public WrapperType responseMessagePayloadWrapperType() {
        return WrapperType.ARRAY;
    }

}

package io.iamcyw.tower.messaging.responsetype;


import io.iamcyw.tower.schema.model.WrapperType;

public class ListInstancesResponseType<R> extends AbstractResponseType {

    protected ListInstancesResponseType(Class<R> expectedResponseType) {
        super(expectedResponseType);
    }

    @Override
    public WrapperType responseMessagePayloadWrapperType() {
        return WrapperType.COLLECTION;
    }

}

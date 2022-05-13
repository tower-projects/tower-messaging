package io.iamcyw.tower.messaging.responsetype;


import io.iamcyw.tower.schema.model.WrapperType;

import java.util.List;

public class ListInstancesResponseType<R> extends AbstractResponseType<List<R>> {

    protected ListInstancesResponseType(Class<R> expectedResponseType) {
        super(expectedResponseType);
    }

    @Override
    public WrapperType responseMessagePayloadWrapperType() {
        return WrapperType.COLLECTION;
    }

}

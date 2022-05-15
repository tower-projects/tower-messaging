package io.iamcyw.tower.messaging.responsetype;


import io.iamcyw.tower.schema.model.WrapperType;

public class ArrayInstancesResponseType extends AbstractResponseType {

    protected ArrayInstancesResponseType(Class expectedResponseType) {
        super(expectedResponseType);
    }

    @Override
    public WrapperType responseMessagePayloadWrapperType() {
        return WrapperType.ARRAY;
    }

}

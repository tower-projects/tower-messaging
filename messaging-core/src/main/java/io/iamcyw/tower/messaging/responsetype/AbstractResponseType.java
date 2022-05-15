package io.iamcyw.tower.messaging.responsetype;

public abstract class AbstractResponseType implements ResponseType {

    protected final Class<?> expectedResponseType;

    protected AbstractResponseType(Class<?> expectedResponseType) {
        this.expectedResponseType = expectedResponseType;
    }

    @Override
    public Class<?> responseMessagePayloadType() {
        return expectedResponseType;
    }

}

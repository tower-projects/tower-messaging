package io.iamcyw.tower.messaging.responsetype;

public abstract class AbstractResponseType<R> implements ResponseType<R> {

    protected final Class<?> expectedResponseType;

    protected AbstractResponseType(Class<?> expectedResponseType) {
        this.expectedResponseType = expectedResponseType;
    }

    @Override
    public Class<?> responseMessagePayloadType() {
        return expectedResponseType;
    }

}

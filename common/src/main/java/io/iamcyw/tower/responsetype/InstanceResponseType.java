package io.iamcyw.tower.responsetype;

import java.lang.reflect.Type;

public class InstanceResponseType<R> extends AbstractResponseType<R> {

    /**
     * Instantiate a {@link InstanceResponseType} with the given
     * {@code expectedResponseType} as the type to be matched against and to which the query response should be
     * converted
     * to.
     *
     * @param expectedResponseType the response type which is expected to be matched against and returned
     */
    public InstanceResponseType(Class<R> expectedResponseType) {
        super(expectedResponseType);
    }

    /**
     * Match the query handler its response {@link Type} with this implementation its responseType
     * {@code R}.
     * Will return true if the expected type is assignable to the response type, taking generic types into account.
     *
     * @param responseType the response {@link Type} of the query handler which is matched against
     * @return true if the response type is assignable to the expected type, taking generic types into account
     */
    @Override
    public boolean matches(Type responseType) {
        Type unwrapped = unwrapIfTypeFuture(responseType);
        return isGenericAssignableFrom(unwrapped) || isAssignableFrom(unwrapped);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<R> responseMessagePayloadType() {
        return (Class<R>) expectedResponseType;
    }

}
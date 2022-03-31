package io.iamcyw.tower.responsetype;

import io.iamcyw.tower.utils.TypeReflectionKit;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public abstract class AbstractResponseType<R> implements ResponseType<R> {

    protected final Class<?> expectedResponseType;

    protected AbstractResponseType(Class<?> expectedResponseType) {
        this.expectedResponseType = expectedResponseType;
    }

    protected Type unwrapIfTypeFuture(Type type) {
        Type futureType = TypeReflectionKit.getExactSuperType(type, Future.class);
        if (futureType instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) futureType).getActualTypeArguments();
            if (actualTypeArguments.length == 1) {
                return actualTypeArguments[0];
            }
        }
        return type;
    }

    protected boolean isIterableOfExpectedType(Type responseType) {
        Type iterableType = TypeReflectionKit.getExactSuperType(responseType, Iterable.class);
        return iterableType != null && isParameterizedTypeOfExpectedType(iterableType);
    }

    protected boolean isStreamOfExpectedType(Type responseType) {
        Type streamType = TypeReflectionKit.getExactSuperType(responseType, Stream.class);
        return streamType != null && isParameterizedTypeOfExpectedType(streamType);
    }

    protected boolean isParameterizedTypeOfExpectedType(Type responseType) {
        boolean isParameterizedType = isParameterizedType(responseType);
        if (!isParameterizedType) {
            return false;
        }

        Type[] actualTypeArguments = ((ParameterizedType) responseType).getActualTypeArguments();
        boolean hasOneTypeArgument = actualTypeArguments.length == 1;
        if (!hasOneTypeArgument) {
            return false;
        }

        Type actualTypeArgument = actualTypeArguments[0];
        return isAssignableFrom(actualTypeArgument) || isGenericAssignableFrom(actualTypeArgument) ||
                isWildcardTypeWithMatchingUpperBound(actualTypeArgument);
    }

    protected boolean isParameterizedType(Type responseType) {
        return responseType instanceof ParameterizedType;
    }

    protected boolean isWildcardTypeWithMatchingUpperBound(Type responseType) {
        boolean isWildcardType = isWildcardType(responseType);
        if (!isWildcardType) {
            return false;
        }

        Type[] upperBounds = ((WildcardType) responseType).getUpperBounds();
        return Arrays.stream(upperBounds).anyMatch(this::isAssignableFrom) ||
                Arrays.stream(upperBounds).anyMatch(this::isGenericAssignableFrom);
    }

    protected boolean isWildcardType(Type responseType) {
        return responseType instanceof WildcardType;
    }

    protected boolean isArrayOfExpectedType(Type responseType) {
        return isArray(responseType) && isAssignableFrom(((Class) responseType).getComponentType());
    }

    protected boolean isArray(Type responseType) {
        return responseType instanceof Class && ((Class) responseType).isArray();
    }

    protected boolean isGenericArrayOfExpectedType(Type responseType) {
        return isGenericArrayType(responseType) &&
                isGenericAssignableFrom(((GenericArrayType) responseType).getGenericComponentType());
    }

    protected boolean isGenericArrayType(Type responseType) {
        return responseType instanceof GenericArrayType;
    }

    protected boolean isGenericAssignableFrom(Type responseType) {
        return isTypeVariable(responseType) &&
                Arrays.stream(((TypeVariable) responseType).getBounds()).anyMatch(this::isAssignableFrom);
    }

    protected boolean isTypeVariable(Type responseType) {
        return responseType instanceof TypeVariable;
    }

    protected boolean isAssignableFrom(Type responseType) {
        return responseType instanceof Class && expectedResponseType.isAssignableFrom((Class) responseType);
    }

}

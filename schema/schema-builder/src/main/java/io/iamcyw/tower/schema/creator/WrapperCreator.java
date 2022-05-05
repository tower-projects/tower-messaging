package io.iamcyw.tower.schema.creator;

import io.iamcyw.tower.schema.Classes;
import io.iamcyw.tower.schema.model.Wrapper;
import io.iamcyw.tower.schema.model.WrapperType;
import org.jboss.jandex.Type;

import java.util.Optional;

/**
 * Helper with detecting if this field is in a wrapper
 * <p>
 * If it is we create an WrapperInfo model that contains the relevant information
 */
public class WrapperCreator {

    private WrapperCreator() {
    }

    public static Optional<Wrapper> createWrapper(Type type) {
        return createWrapper(null, type);
    }

    /**
     * Create a Wrapper for a Field (that has properties and methods)
     *
     * @param fieldType  the java field type
     * @param methodType the java method type
     * @return optional array
     */
    public static Optional<Wrapper> createWrapper(Type fieldType, Type methodType) {
        if (Classes.isWrapper(methodType)) {
            Wrapper wrapper = new Wrapper(getWrapperType(methodType), methodType.name().toString());
            wrapper.setNotEmpty(true);
            // Wrapper of wrapper
            Optional<Wrapper> wrapperOfWrapper = getWrapperOfWrapper(methodType);
            if (wrapperOfWrapper.isPresent()) {
                wrapper.setWrapper(wrapperOfWrapper.get());
            }

            return Optional.of(wrapper);
        }
        return Optional.empty();
    }

    private static WrapperType getWrapperType(Type type) {
        if (Classes.isOptional(type)) {
            return WrapperType.OPTIONAL;
        } else if (Classes.isArray(type)) {
            return WrapperType.ARRAY;
        } else if (Classes.isCollection(type)) {
            return WrapperType.COLLECTION;
        } else if (Classes.isMap(type)) {
            return WrapperType.MAP;
        } else if (Classes.isParameterized(type)) {
            return WrapperType.UNKNOWN;
        }
        return null;
    }

    private static Optional<Wrapper> getWrapperOfWrapper(Type type) {
        if (Classes.isArray(type)) {
            Type typeInArray = type.asArrayType().component();
            return createWrapper(typeInArray);
        } else if (Classes.isParameterized(type)) {
            Type typeInCollection = type.asParameterizedType().arguments().get(0);
            return createWrapper(typeInCollection);
        }
        return Optional.empty();
    }


}

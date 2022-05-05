package io.iamcyw.tower.messaging.responsetype;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MultipleInstancesResponseType<R> extends AbstractResponseType<List<R>> {

    /**
     * Instantiate a {@link MultipleInstancesResponseType} with the given
     * {@code expectedCollectionGenericType} as the type to be matched against and which the convert function will use
     * as the generic for the {@link List} return value.
     *
     * @param expectedCollectionGenericType the response type which is expected to be matched against and returned
     */
    public MultipleInstancesResponseType(Class<R> expectedCollectionGenericType) {
        super(expectedCollectionGenericType);
    }

    /**
     * Match the query handler its response {@link Type} with this implementation its responseType
     * {@code R}.
     * Will return true in the following scenarios:
     * <ul>
     * <li>If the response type is an array of the expected type. For example a {@code ExpectedType[]}</li>
     * <li>If the response type is a {@link java.lang.reflect.GenericArrayType} of the expected type.
     * For example a {@code <E extends ExpectedType> E[]}</li>
     * <li>If the response type is a {@link java.lang.reflect.ParameterizedType} containing a single
     * {@link java.lang.reflect.TypeVariable} which is assignable to the response type, taking generic types into
     * account. For example a {@code List<ExpectedType>} or {@code <E extends ExpectedType> List<E>}.</li>
     * <li>If the response type is a {@link java.lang.reflect.ParameterizedType} containing a single
     * {@link java.lang.reflect.WildcardType} which is assignable to the response type, taking generic types into
     * account. For example a {@code <E extends ExpectedType> List<? extends E>}.</li>
     * </ul>
     *
     * @param responseType the response {@link Type} of the query handler which is matched against
     * @return true for arrays, generic arrays and {@link java.lang.reflect.ParameterizedType}s (like a
     * {@link Iterable}) for which the contained type is assignable to the expected type
     */
    @Override
    public boolean matches(Type responseType) {
        Type unwrapped = unwrapIfTypeFuture(responseType);
        return isIterableOfExpectedType(unwrapped) || isStreamOfExpectedType(unwrapped) ||
                isGenericArrayOfExpectedType(unwrapped) || isArrayOfExpectedType(unwrapped);
    }

    /**
     * Converts the given {@code response} of type {@link Object} into the type {@link List} with
     * generic type {@code R} from this {@link ResponseType} instance.
     * Will ensure that if the given {@code response} is of another collections format (e.g. an array, or a
     * {@link java.util.stream.Stream}) that it will be converted to a List.
     * Should only be called if {@link ResponseType#matches(Type)} returns true. Will throw an
     * {@link IllegalArgumentException} if the given response is not convertible to a List of the expected
     * response type.
     *
     * @param response the {@link Object} to convert into a {@link List} of generic type {@code R}
     * @return a {@link List} of generic type {@code R}, based on the given {@code response}
     */
    @SuppressWarnings("unchecked") // Suppress cast to array R, since in proper use of this function it is allowed
    @Override
    public List<R> convert(Object response) {
        Class<?> responseType = response.getClass();

        if (isArrayOfExpectedType(responseType)) {
            return Arrays.asList((R[]) response);
        } else if (isIterableOfExpectedType(response)) {
            return convertToList((Iterable) response);
        }

        throw new IllegalArgumentException(
                "Retrieved response [" + responseType + "] is not convertible to a List of " +
                        "the expected response type [" + expectedResponseType + "]");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class responseMessagePayloadType() {
        return List.class;
    }

    private boolean isIterableOfExpectedType(Object response) {
        Class<?> responseType = response.getClass();

        boolean isIterableType = Iterable.class.isAssignableFrom(responseType);
        if (!isIterableType) {
            return false;
        }
        Iterator responseIterator = ((Iterable) response).iterator();

        boolean canMatchContainedType = responseIterator.hasNext();
        if (!canMatchContainedType) {
            // logger.info("The given response is an Iterable without any contents, hence we cannot verify if the " +
            //                     "contained type is assignable to the expected type.");
            return true;
        }

        return isAssignableFrom(responseIterator.next().getClass());
    }

    @SuppressWarnings("unchecked") // Suppress cast to R, since in proper use of this function it is allowed
    private List<R> convertToList(Iterable responseIterable) {
        List<R> response = new ArrayList<>();
        Iterator responseIterator = responseIterable.iterator();
        responseIterator.forEachRemaining(responseInstance -> response.add((R) responseInstance));
        return response;
    }

}

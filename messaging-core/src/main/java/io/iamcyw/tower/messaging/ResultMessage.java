package io.iamcyw.tower.messaging;

import io.iamcyw.tower.common.HandlerExecutionException;
import io.iamcyw.tower.serialization.SerializedObject;
import io.iamcyw.tower.serialization.Serializer;

import java.util.Map;
import java.util.Optional;

public interface ResultMessage<R> extends Message<R> {

    /**
     * Indicates whether the ResultMessage represents unsuccessful execution.
     *
     * @return {@code true} if execution was unsuccessful, {@code false} otherwise
     */
    boolean isExceptional();

    /**
     * Returns the Exception in case of exceptional result message or an empty {@link Optional} in case of successful
     * execution.
     *
     * @return an {@link Optional} containing exception result or an empty Optional in case of a successful execution
     */
    Optional<Throwable> optionalExceptionResult();

    /**
     * Returns the exception result. This method is to be called if {@link #isExceptional()} returns {@code true}.
     *
     * @return a {@link Throwable} defining the exception result
     * @throws IllegalStateException if this ResultMessage is not exceptional
     */
    default Throwable exceptionResult() throws IllegalStateException {
        return optionalExceptionResult().orElseThrow(IllegalStateException::new);
    }

    /**
     * If the this message contains an exception result, returns the details provided in the exception, if available.
     * If this message does not carry an exception result, or the exception result doesn't provide any
     * application-specific details, an empty optional is returned.
     *
     * @param <D> The type of application-specific details expected
     * @return an optional containing application-specific error details, if present
     */
    default <D> Optional<D> exceptionDetails() {
        return optionalExceptionResult().flatMap(HandlerExecutionException::resolveDetails);
    }

    /**
     * Serializes the exception result. Will create a {@link RemoteExceptionDescription} from the {@link Optional}
     * exception in this ResultMessage instead of serializing the original exception.
     *
     * @param serializer             the {@link Serializer} used to serialize the exception
     * @param expectedRepresentation a {@link Class} representing the expected format
     * @param <T>                    the generic type representing the expected format
     * @return the serialized exception as a {@link SerializedObject}
     */
    default <T> SerializedObject<T> serializeExceptionResult(Serializer serializer, Class<T> expectedRepresentation) {
        return serializer.serialize(optionalExceptionResult().map(RemoteExceptionDescription::describing)
                                            .orElse(null), expectedRepresentation);
    }

    @Override
    ResultMessage<R> withMetaData(Map<String, ?> metaData);

    @Override
    ResultMessage<R> andMetaData(Map<String, ?> metaData);

    @Override
    default <S> SerializedObject<S> serializePayload(Serializer serializer, Class<S> expectedRepresentation) {
        if (isExceptional()) {
            return serializer.serialize(exceptionDetails().orElse(null), expectedRepresentation);
        }
        return serializer.serialize(getPayload(), expectedRepresentation);
    }

}

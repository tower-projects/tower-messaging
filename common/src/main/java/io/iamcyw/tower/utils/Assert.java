package io.iamcyw.tower.utils;

import io.iamcyw.tower.exception.ErrorMessage;
import io.iamcyw.tower.exception.Errors;
import io.iamcyw.tower.exception.MessageIllegalArgumentException;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Assert {

    private Assert() {
    }

    public static void state(boolean state, ErrorMessage transform) {
        state(state, transform.toString());
    }

    public static void state(boolean state, String message) {
        if (!state) {
            throw new IllegalStateException(message);
        }
    }

    public static void isFalse(boolean expression, ErrorMessage message) {
        isTrue(!expression, message);
    }

    public static void isTrue(boolean expression, ErrorMessage message) {
        if (!expression) {
            throw new MessageIllegalArgumentException(message);
        }
    }

    public static <T> void nonNull(T value, String object) {
        nonNull(value,
                Errors.create("common.argument_nonNull").content("argument: {} cannot null").args(object).apply());
    }

    public static <T> void nonNull(T value, ErrorMessage message) {
        assertThat(value, Objects::nonNull, () -> new MessageIllegalArgumentException(message));
    }

    public static <T, X extends Throwable> void assertThat(T value, Predicate<T> assertion,
                                                           Supplier<? extends X> exceptionSupplier) throws X {
        if (!assertion.test(value)) {
            throw exceptionSupplier.get();
        }
    }

}
package io.iamcyw.tower.utils;

import io.iamcyw.tower.exception.ErrorMessage;
import io.iamcyw.tower.utils.i18n.I18nTransform;
import io.iamcyw.tower.utils.i18n.I18ns;
import io.iamcyw.tower.utils.i18n.transform.MessageKeyFunc;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Assert {

    private Assert() {
    }

    public static void state(boolean state, String message) {
        if (!state) {
            throw new IllegalStateException(message);
        }
    }

    public static void isFalse(boolean expression, String message) {
        isTrue(!expression, message);
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <T> void nonNull(T value, String object) {
        nonNull(value, ErrorMessage.COMMON_OBJECT_NULL.andThen(I18ns.args(object)));
    }

    public static <T> void nonNull(T value, I18nTransform transform) {
        assertThat(value, Objects::nonNull, () -> new IllegalArgumentException(
                transform.compose(new MessageKeyFunc("common.obj_null")).apply()));
    }

    public static <T, X extends Throwable> void assertThat(T value, Predicate<T> assertion,
                                                           Supplier<? extends X> exceptionSupplier) throws X {
        if (!assertion.test(value)) {
            throw exceptionSupplier.get();
        }
    }

}
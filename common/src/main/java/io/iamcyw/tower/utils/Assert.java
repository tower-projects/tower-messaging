package io.iamcyw.tower.utils;

import io.iamcyw.tower.exception.ErrorMessage;
import io.iamcyw.tower.exception.Errors;
import io.iamcyw.tower.exception.MessageIllegalArgumentException;
import io.iamcyw.tower.exception.MessageIllegalStateException;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public abstract class Assert {

    private Assert() {
        throw new IllegalStateException("Utility class");
    }

    private static final String invalidNameErrorMessage =
            "Name must be non-null, non-empty and match " + "[_A-Za-z][_0-9A-Za-z]* - was '%s'";

    private static final Pattern validNamePattern = Pattern.compile("[_A-Za-z][_0-9A-Za-z]*");

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

    public static <T> T assertNotNull(T object, Supplier<String> msg) {
        if (object != null) {
            return object;
        }
        throw new MessageIllegalArgumentException(Errors.create().content(msg.get()).apply());
    }

    public static <T> T assertNotNullWithNPE(T object, Supplier<String> msg) {
        if (object != null) {
            return object;
        }
        throw new NullPointerException(msg.get());
    }

    public static <T> T assertNotNull(T object) {
        if (object != null) {
            return object;
        }
        throw new MessageIllegalArgumentException(Errors.create().content("Object required to be not null").apply());
    }

    public static <T> void assertNull(T object, Supplier<String> msg) {
        if (object == null) {
            return;
        }
        throw new MessageIllegalArgumentException(Errors.create().content(msg.get()).apply());
    }

    public static <T> void assertNull(T object) {
        if (object == null) {
            return;
        }
        throw new MessageIllegalArgumentException(Errors.create().content("Object required to be null").apply());
    }

    public static <T> T assertNeverCalled() {
        throw new MessageIllegalStateException(Errors.create().content("Should never been called").apply());
    }

    public static <T> T assertShouldNeverHappen(String format, Object... args) {
        throw new MessageIllegalStateException(
                Errors.create().content("Internal error: should never happen: " + format).args(args).apply());
    }

    public static <T> T assertShouldNeverHappen() {
        throw new MessageIllegalStateException(Errors.create().content("Internal error: should never happen").apply());
    }

    public static <T> Collection<T> assertNotEmpty(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            throw new MessageIllegalArgumentException(
                    Errors.create().content("collection must be not null and not empty").apply());
        }
        return collection;
    }

    public static <T> Collection<T> assertNotEmpty(Collection<T> collection, Supplier<String> msg) {
        if (collection == null || collection.isEmpty()) {
            throw new MessageIllegalStateException(Errors.create().content(msg.get()).apply());
        }
        return collection;
    }

    public static void assertTrue(boolean condition, Supplier<String> msg) {
        if (condition) {
            return;
        }
        throw new MessageIllegalStateException(Errors.create().content(msg.get()).apply());
    }

    public static void assertTrue(boolean condition) {
        if (condition) {
            return;
        }
        throw new MessageIllegalStateException(Errors.create().content("condition expected to be true").apply());
    }

    public static void assertFalse(boolean condition, Supplier<String> msg) {
        if (!condition) {
            return;
        }
        throw new MessageIllegalStateException(Errors.create().content(msg.get()).apply());
    }

    public static void assertFalse(boolean condition) {
        if (!condition) {
            return;
        }
        throw new MessageIllegalStateException(Errors.create().content("condition expected to be false").apply());
    }

    /**
     * Validates that the Lexical token name matches the current spec.
     * currently non null, non empty,
     *
     * @param name - the name to be validated.
     * @return the name if valid, or AssertException if invalid.
     */
    public static String assertValidName(String name) {
        if (name != null && !name.isEmpty() && validNamePattern.matcher(name).matches()) {
            return name;
        }
        throw new MessageIllegalArgumentException(Errors.create().content(invalidNameErrorMessage).args(name).apply());
    }

}
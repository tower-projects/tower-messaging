package io.iamcyw.tower.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Supplier;

public class CommonUtils {

    public static final String EXPRESSION_BRACE = "{}";

    /**
     * Returns the given instance, if not {@code null}, or otherwise the value provided by {@code defaultProvider}.
     *
     * @param instance        the value to return, if not {@code null}
     * @param defaultProvider to provide the value, when {@code instance} is {@code null}
     * @param <T>             the type of value to return
     * @return {@code instance} if not {@code null}, otherwise the value provided by {@code defaultProvider}
     */
    public static <T> T getOrDefault(T instance, Supplier<T> defaultProvider) {
        if (instance == null) {
            return defaultProvider.get();
        }
        return instance;
    }

    /**
     * Returns the given instance, if not {@code null}, or otherwise the given {@code defaultValue}.
     *
     * @param instance     the value to return, if not {@code null}
     * @param defaultValue the value, when {@code instance} is {@code null}
     * @param <T>          the type of value to return
     * @return {@code instance} if not {@code null}, otherwise {@code defaultValue}
     */
    public static <T> T getOrDefault(T instance, T defaultValue) {
        if (instance == null) {
            return defaultValue;
        }
        return instance;
    }

    /**
     * Returns the given instance, if not {@code null} or of zero length, or otherwise the given {@code defaultValue}.
     *
     * @param instance     the value to return, if not {@code null}
     * @param defaultValue the value, when {@code instance} is {@code null}
     * @param <T>          the type of value to return
     * @return {@code instance} if not {@code null}, otherwise {@code defaultValue}
     */
    public static <T extends CharSequence> T getNonEmptyOrDefault(T instance, T defaultValue) {
        if (instance == null || instance.length() == 0) {
            return defaultValue;
        }
        return instance;
    }

    /**
     * 将字符串中的'{}'按照数组中的值依次替换
     *
     * @param messagePattern 待替换的字符串
     * @param argArray       替换数据的数组
     * @return 替换后的字符串
     */
    public static String arrayFormat(String messagePattern, Object[] argArray) {
        if (argArray != null && argArray.length > 0) {
            for (Object arg : argArray) {
                messagePattern = StringUtils.replaceOnce(messagePattern, EXPRESSION_BRACE, ArrayUtils.toString(arg));
            }
        }
        return messagePattern;
    }

    public static String toString(Object object) {
        if (object == null) {
            return null;
        }

        if (object instanceof String) {
            return (String) object;
        }

        return object.toString();
    }

    /**
     * Gets number of millis which are remaining of current deadline to be reached by {@link
     * System#currentTimeMillis()}. If deadline is passed, 0 will be returned.
     *
     * @param deadline deadline to be met
     * @return number of millis to deadline
     */
    public static long getRemainingOfDeadline(long deadline) {
        long leftTimeout = deadline - System.currentTimeMillis();
        leftTimeout = leftTimeout < 0 ? 0 : leftTimeout;
        return leftTimeout;
    }

}
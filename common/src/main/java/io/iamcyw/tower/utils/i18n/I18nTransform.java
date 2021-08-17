package io.iamcyw.tower.utils.i18n;

import io.iamcyw.tower.utils.lang.StringPool;

import java.util.Objects;

@FunctionalInterface
public interface I18nTransform {
    String apply(String msg);

    default String apply() {
        return apply(null);
    }

    default I18nTransform andThen(I18nTransform after) {
        Objects.requireNonNull(after);
        return m -> after.apply(apply(m));
    }

    default I18nTransform compose(I18nTransform before) {
        Objects.requireNonNull(before);
        return m -> apply(before.apply(m));
    }

    default I18nTransform append(I18nTransform second) {
        Objects.requireNonNull(second);

        return m -> apply(m) + StringPool.SPACE + second.apply(m);
    }

}

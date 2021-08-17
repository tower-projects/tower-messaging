package io.iamcyw.tower.utils.i18n;

import io.iamcyw.tower.utils.i18n.transform.MessageArgsFunc;
import io.iamcyw.tower.utils.i18n.transform.MessageContentFunc;
import io.iamcyw.tower.utils.i18n.transform.MessageKeyFunc;
import org.apache.commons.lang3.StringUtils;

public class I18ns {

    public static Builder create() {
        return new Builder(msg -> msg);
    }

    public static Builder create(I18nTransform transformFunc) {
        return new Builder(transformFunc);
    }

    public static I18nTransform key(String key) {
        return new MessageKeyFunc(key);
    }

    public static I18nTransform content(String content) {
        return new MessageContentFunc(content);
    }

    public static I18nTransform args(Object... args) {
        return new MessageArgsFunc(args);
    }

    public static class Builder {
        private I18nTransform messageFunc;

        public Builder(I18nTransform func) {
            this.messageFunc = func;
        }

        public Builder key(String key) {
            if (StringUtils.isNoneBlank(key)) {
                messageFunc = messageFunc.andThen(I18ns.key(key));
            }
            return this;
        }

        public Builder content(String content) {
            if (StringUtils.isNoneBlank(content)) {
                messageFunc = messageFunc.andThen(I18ns.content(content));
            }
            return this;
        }

        public Builder args(Object... args) {
            if (args != null) {
                messageFunc = messageFunc.andThen(I18ns.args(args));
            }
            return this;
        }

        public Builder append(I18nTransform second) {
            messageFunc = messageFunc.append(second);
            return this;
        }

        public I18nTransform build() {
            return messageFunc;
        }

        public String apply() {
            return messageFunc.apply();
        }

    }

}

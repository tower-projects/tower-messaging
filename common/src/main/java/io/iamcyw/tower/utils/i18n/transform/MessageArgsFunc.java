package io.iamcyw.tower.utils.i18n.transform;

import io.iamcyw.tower.utils.i18n.I18nTransform;
import io.iamcyw.tower.utils.lang.StringPool;
import org.apache.commons.lang3.StringUtils;

public class MessageArgsFunc implements I18nTransform {

    private final Object[] args;

    public MessageArgsFunc(Object[] args) {
        this.args = args;
    }

    private String arrayFormat(String messagePattern, Object[] argArray) {
        if (argArray != null && argArray.length > 0) {
            for (Object arg : argArray) {
                messagePattern = StringUtils.replaceOnce(messagePattern, StringPool.LEFT_BRACE + StringPool.RIGHT_BRACE,
                                                         String.valueOf(arg));
            }
        }
        return messagePattern;
    }

    @Override
    public String apply(String msg) {
        return arrayFormat(msg, args);
    }

}

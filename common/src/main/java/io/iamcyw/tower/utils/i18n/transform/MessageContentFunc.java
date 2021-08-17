package io.iamcyw.tower.utils.i18n.transform;

import io.iamcyw.tower.utils.i18n.I18nTransform;
import org.apache.commons.lang3.StringUtils;

public class MessageContentFunc implements I18nTransform {

    private final String content;

    public MessageContentFunc(String content) {
        this.content = content;
    }

    @Override
    public String apply(String msg) {
        if (StringUtils.isBlank(msg)) {
            return content;
        } else {
            return msg;
        }
    }

}

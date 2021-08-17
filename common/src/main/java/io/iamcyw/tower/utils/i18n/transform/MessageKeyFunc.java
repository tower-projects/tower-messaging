package io.iamcyw.tower.utils.i18n.transform;

import io.iamcyw.tower.utils.i18n.I18nTransform;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

public class MessageKeyFunc implements I18nTransform {

    private final String messageKey;

    public MessageKeyFunc(String messageKey) {
        this.messageKey = messageKey;
    }

    @Override
    public String apply(String msg) {
        // todo key loader
        return msg;
    }

}

package io.iamcyw.tower.exception;

import io.iamcyw.tower.utils.i18n.I18nTransform;
import io.iamcyw.tower.utils.i18n.I18ns;

public interface ErrorMessage {

    I18nTransform COMMON_OBJECT_NULL = I18ns.create().key("common.object_null").args("{} cannot be null").build();

}

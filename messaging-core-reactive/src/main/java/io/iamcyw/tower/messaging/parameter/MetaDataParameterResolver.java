package io.iamcyw.tower.messaging.parameter;

import io.iamcyw.tower.messaging.Message;

public class MetaDataParameterResolver implements ParameterResolver {

    public static final MetaDataParameterResolver INSTANCE = new MetaDataParameterResolver();

    @Override
    public Object resolveParameterValue(Message message) {
        return message.getMetaData();
    }

    @Override
    public boolean matches(Message message) {
        return true;
    }

}

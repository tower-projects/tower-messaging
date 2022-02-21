package io.iamcyw.tower.messaging.handle.resolve;

import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.MetaData;

public class MetaDataParameterResolver implements ParameterResolver<MetaData> {
    @Override
    public MetaData resolveParameterValue(Message message) {
        return message.getMetaData();
    }

    @Override
    public boolean matches(Message message) {
        return true;
    }

}

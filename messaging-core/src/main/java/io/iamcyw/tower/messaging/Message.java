package io.iamcyw.tower.messaging;

import java.util.Map;

public interface Message {

    String getIdentifier();

    MetaData getMetaData();

    Object getPayload();

    Class<?> getPayloadType();

    /**
     * 覆盖metadata
     */
    Message withMetaData(Map<String, Object> metaData);

    /**
     * 合并metadata
     */
    Message andMetaData(Map<String, Object> metaData);

}

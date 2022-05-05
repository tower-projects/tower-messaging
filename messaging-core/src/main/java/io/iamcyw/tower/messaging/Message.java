package io.iamcyw.tower.messaging;

import io.iamcyw.tower.messaging.handle.Identifier;
import io.iamcyw.tower.schema.model.OperationType;

import java.util.Map;

public interface Message<R> {

    Identifier getIdentifier();

    MetaData getMetaData();

    Object getPayload();

    OperationType getOperationType();

    /**
     * 覆盖metadata
     */
    Message<R> withMetaData(Map<String, Object> metaData);

    /**
     * 合并metadata
     */
    Message<R> andMetaData(Map<String, Object> metaData);

}

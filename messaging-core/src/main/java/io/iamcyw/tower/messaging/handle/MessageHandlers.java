package io.iamcyw.tower.messaging.handle;

import io.iamcyw.tower.messaging.Message;

import java.util.List;
import java.util.stream.Stream;

public interface MessageHandlers {

    Stream<MessageHandle> get(Message message);

}

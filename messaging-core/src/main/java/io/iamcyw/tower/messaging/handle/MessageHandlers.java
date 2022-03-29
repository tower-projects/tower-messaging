package io.iamcyw.tower.messaging.handle;

import io.iamcyw.tower.messaging.Message;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface MessageHandlers {

    CompletableFuture<List<MessageHandle>> get(Message message);

}

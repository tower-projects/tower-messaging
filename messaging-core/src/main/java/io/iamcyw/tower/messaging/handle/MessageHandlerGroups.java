package io.iamcyw.tower.messaging.handle;

import io.iamcyw.tower.collect.ImmutableKit;
import io.iamcyw.tower.messaging.Message;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MessageHandlerGroups implements MessageHandlers {

    private List<MessageHandlerGroup> messageHandlerGroupList;

    public MessageHandlerGroups() {
        messageHandlerGroupList = List.of();
    }

    public MessageHandlerGroups(List<MessageHandlerGroup> messageHandlerGroupList) {
        this.messageHandlerGroupList = messageHandlerGroupList;
    }

    @Override
    public CompletableFuture<List<MessageHandle>> get(Message message) {
        return CompletableFuture.supplyAsync(() -> {
            List<MessageHandlerGroup> allowHandlerGroup = ImmutableKit.filter(getMessageHandlerGroupList(),
                                                                              messageHandlerGroup -> messageHandlerGroup.predicate(
                                                                                      message));

            return ImmutableKit.flatMap(allowHandlerGroup, messageHandlerGroup -> messageHandlerGroup.handles(message));
        });
    }

    public List<MessageHandlerGroup> getMessageHandlerGroupList() {
        return messageHandlerGroupList;
    }

    public void setMessageHandlerGroupList(List<MessageHandlerGroup> messageHandlerGroupList) {
        this.messageHandlerGroupList = messageHandlerGroupList;
    }

}

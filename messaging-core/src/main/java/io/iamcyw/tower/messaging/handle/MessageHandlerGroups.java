package io.iamcyw.tower.messaging.handle;

import io.iamcyw.tower.messaging.Message;

import java.util.List;
import java.util.stream.Stream;

public class MessageHandlerGroups implements MessageHandlers {

    private List<MessageHandlerGroup> messageHandlerGroups;

    public MessageHandlerGroups() {
        messageHandlerGroups = List.of();
    }

    public MessageHandlerGroups(List<MessageHandlerGroup> messageHandlerGroups) {
        this.messageHandlerGroups = messageHandlerGroups;
    }

    @Override
    public Stream<MessageHandle> get(Message message) {
        return messageHandlerGroups.stream()
                                   .filter(messageHandlerGroup -> messageHandlerGroup.predicate(message))
                                   .flatMap(messageHandlerGroup -> messageHandlerGroup.handle(message));
    }

    public List<MessageHandlerGroup> getMessageHandlerGroups() {
        return messageHandlerGroups;
    }

    public void setMessageHandlerGroups(List<MessageHandlerGroup> messageHandlerGroups) {
        this.messageHandlerGroups = messageHandlerGroups;
    }

}

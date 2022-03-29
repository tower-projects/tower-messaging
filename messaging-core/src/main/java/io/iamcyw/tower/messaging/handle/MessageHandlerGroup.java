package io.iamcyw.tower.messaging.handle;

import io.iamcyw.tower.collect.ImmutableKit;
import io.iamcyw.tower.messaging.Message;

import java.util.List;

public class MessageHandlerGroup {
    private String name;

    private List<MessageHandle> handles;

    public MessageHandlerGroup() {
        this("", List.of());
    }

    public MessageHandlerGroup(String name, List<MessageHandle> handles) {
        this.name = name;
        this.handles = handles;
    }

    public List<MessageHandle> handles(Message message) {
        return ImmutableKit.filter(handles, messageHandle -> messageHandle.predicate(message));
    }

    public boolean predicate(Message message) {
        return message.getPayloadType().getName().equals(name);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MessageHandle> getHandles() {
        return handles;
    }

    public void setHandles(List<MessageHandle> handles) {
        this.handles = handles;
    }

}

package io.iamcyw.tower.messaging.handle;

import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.utils.collect.ListKit;

import java.util.List;

public class MessageHandlerGroup {
    /**
     * 实现的domain类
     */
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
        return ListKit.filter(handles, messageHandle -> messageHandle.predicate(message));
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

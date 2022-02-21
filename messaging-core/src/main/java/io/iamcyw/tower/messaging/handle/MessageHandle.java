package io.iamcyw.tower.messaging.handle;

import io.iamcyw.tower.messaging.Message;

public abstract class MessageHandle {
    public String handleTarget;

    public MessageHandle(String handleTarget) {
        this.handleTarget = handleTarget;
    }

    public abstract <R> R handle(Message message);

    public abstract boolean predicate(Message message);

}
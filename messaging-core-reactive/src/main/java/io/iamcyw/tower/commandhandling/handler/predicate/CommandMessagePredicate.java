package io.iamcyw.tower.commandhandling.handler.predicate;

import io.iamcyw.tower.commandhandling.CommandMessage;
import io.iamcyw.tower.messaging.predicate.MessagePredicate;

public interface CommandMessagePredicate<T> extends MessagePredicate<CommandMessage<T>> {

}

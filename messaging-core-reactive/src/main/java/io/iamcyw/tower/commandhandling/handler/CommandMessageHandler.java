package io.iamcyw.tower.commandhandling.handler;

import io.iamcyw.tower.commandhandling.CommandMessage;
import io.iamcyw.tower.messaging.ReactorMessageHandler;

public interface CommandMessageHandler<T> extends ReactorMessageHandler<CommandMessage<T>> {


}

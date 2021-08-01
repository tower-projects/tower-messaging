package io.iamcyw.tower.commandhandling.handler;

import io.smallrye.mutiny.Multi;

public interface CommandMessageHandles {

    Multi<CommandMessageHandler> getHandles();

}

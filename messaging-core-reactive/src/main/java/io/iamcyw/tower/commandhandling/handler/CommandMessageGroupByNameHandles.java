package io.iamcyw.tower.commandhandling.handler;

import io.smallrye.mutiny.GroupedMulti;
import io.smallrye.mutiny.Multi;

public class CommandMessageGroupByNameHandles {
    private final Multi<GroupedMulti<String, CommandMessageHandler>> handles;

    public CommandMessageGroupByNameHandles(CommandMessageHandles handles) {
        this.handles = handles.getHandles().group().by(CommandMessageHandler::getCommandName);
    }

    public <C> Multi<CommandMessageHandler<C>> lookupHandler(String commandName) {
        return this.handles.filter(c -> c.key().equals(commandName)).onItem()
                           .transformToMultiAndConcatenate(group -> group.<CommandMessageHandler<C>>map(c -> c))
                           .collect().asList().onItem()
                           .transformToMulti(handleList -> Multi.createFrom().items(handleList.stream().sorted()));
    }

}

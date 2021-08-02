package io.iamcyw.tower.commandhandling;


import io.iamcyw.tower.messaging.GenericMessage;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.MessageDecorator;
import io.iamcyw.tower.messaging.MetaData;

import java.util.Map;

public class GenericCommandMessage extends MessageDecorator implements CommandMessage {
    private static final long serialVersionUID = 3282528436414939876L;

    private final String commandName;

    /**
     * Create a CommandMessage with the given {@code command} as payload and empty metaData
     *
     * @param payload the payload for the Message
     */
    public GenericCommandMessage(Object payload) {
        this(payload, MetaData.emptyInstance());
    }

    /**
     * Create a CommandMessage with the given {@code command} as payload.
     *
     * @param payload  the payload for the Message
     * @param metaData The meta data for this message
     */
    public GenericCommandMessage(Object payload, Map<String, ?> metaData) {
        this(new GenericMessage(payload, metaData), payload.getClass().getName());
    }

    /**
     * Create a CommandMessage from the given {@code delegate} message containing payload, metadata and message
     * identifier, and the given {@code commandName}.
     *
     * @param delegate    the delegate message
     * @param commandName The name of the command
     */
    public GenericCommandMessage(Message delegate, String commandName) {
        super(delegate);
        this.commandName = commandName;
    }

    /**
     * Returns the given command as a CommandMessage. If {@code command} already implements CommandMessage, it is
     * returned as-is. Otherwise, the given {@code command} is wrapped into a GenericCommandMessage as its
     * payload.
     *
     * @param command the command to wrap as CommandMessage
     * @return a CommandMessage containing given {@code command} as payload, or {@code command} if it already implements
     * CommandMessage.
     */
    @SuppressWarnings("unchecked")
    public static CommandMessage asCommandMessage(Object command) {
        if (CommandMessage.class.isInstance(command)) {
            return (CommandMessage) command;
        }
        return new GenericCommandMessage(command, MetaData.emptyInstance());
    }

    @Override
    public String getCommandName() {
        return commandName;
    }

    @Override
    public GenericCommandMessage withMetaData(Map<String, ?> metaData) {
        return new GenericCommandMessage(getDelegate().withMetaData(metaData), commandName);
    }

    @Override
    public GenericCommandMessage andMetaData(Map<String, ?> metaData) {
        return new GenericCommandMessage(getDelegate().andMetaData(metaData), commandName);
    }

    @Override
    protected void describeTo(StringBuilder stringBuilder) {
        super.describeTo(stringBuilder);
        stringBuilder.append(", commandName='").append(getCommandName()).append('\'');
    }

    @Override
    protected String describeType() {
        return "GenericCommandMessage";
    }

}

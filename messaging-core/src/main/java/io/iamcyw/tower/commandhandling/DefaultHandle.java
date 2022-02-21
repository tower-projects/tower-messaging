package io.iamcyw.tower.commandhandling;

import io.iamcyw.tower.exception.Errors;
import io.iamcyw.tower.exception.MessageIllegalStateException;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.handle.MessageHandle;
import io.iamcyw.tower.messaging.interceptor.Handle;

import java.util.stream.Stream;

public class DefaultHandle<R> implements Handle<R> {
    @Override
    public R handle(Message message) {
        Stream<MessageHandle> messageHandles = message.getMetaData().getMessageHandlers();
        return messageHandles.findFirst()
                             .orElseThrow(() -> new MessageIllegalStateException(Errors.create()
                                                                                       .content(
                                                                                               "{} not match Any " +
                                                                                                       "Command Handle")
                                                                                       .args(message.getPayloadType()
                                                                                                    .getName())
                                                                                       .apply()))
                             .handle(message);
    }

}

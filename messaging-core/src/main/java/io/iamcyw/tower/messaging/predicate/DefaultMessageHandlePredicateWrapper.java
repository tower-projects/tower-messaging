package io.iamcyw.tower.messaging.predicate;

import io.iamcyw.tower.messaging.Message;

import java.util.Map;

public class DefaultMessageHandlePredicateWrapper implements MessageHandlePredicate {
    private final MessageHandlePredicate delegate;

    private final String[] parameter;

    public DefaultMessageHandlePredicateWrapper(MessageHandlePredicate delegate, String[] parameter) {
        this.delegate = delegate;
        this.parameter = parameter;
    }

    @Override
    public boolean test(Message message) {
        return delegate.test(message.andMetaData(Map.of("PREDICATE_PARAMETER", parameter)));
    }

}

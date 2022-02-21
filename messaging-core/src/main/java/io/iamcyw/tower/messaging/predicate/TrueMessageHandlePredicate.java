package io.iamcyw.tower.messaging.predicate;

import io.iamcyw.tower.messaging.Message;

public class TrueMessageHandlePredicate implements MessageHandlePredicate {
    @Override
    public boolean test(Message message) {
        return true;
    }

}

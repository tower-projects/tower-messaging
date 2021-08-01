package io.iamcyw.tower.queryhandling;

import io.iamcyw.tower.messaging.responsetypes.ResponseTypes;
import org.junit.jupiter.api.Test;

class ReactorQueryBusTest {

    @Test
    void query() {
        ReactorQueryBus queryBus = new DefaultReactorQueryBus(null);

        queryBus.query(new GenericQueryMessage<>("", ResponseTypes.instanceOf(Integer.class)))
                .map(integerQueryResponseMessage -> integerQueryResponseMessage.getPayload());

    }

}
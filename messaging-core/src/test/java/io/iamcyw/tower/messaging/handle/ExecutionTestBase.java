package io.iamcyw.tower.messaging.handle;

import io.iamcyw.tower.messaging.bootstrap.Bootstrap;
import io.iamcyw.tower.messaging.gateway.MessageGateway;
import io.iamcyw.tower.schema.SchemaBuilder;
import io.iamcyw.tower.schema.model.Schema;
import org.jboss.jandex.IndexView;
import org.junit.jupiter.api.BeforeEach;


public class ExecutionTestBase {

    protected MessageGateway messageGateway;

    @BeforeEach
    public void init() {
        IndexView index = getIndex();
        Schema schema = SchemaBuilder.build(index);

        Bootstrap bootstrap = new Bootstrap(schema);

        messageGateway = bootstrap.getMessageGateway();
    }

    protected IndexView getIndex() {
        return Indexer.getAllTestIndex();
    }

}

package io.iamcyw.tower.messaging.cdi.producer;

import io.iamcyw.tower.messaging.bootstrap.Bootstrap;
import io.iamcyw.tower.messaging.gateway.MessageGateway;
import io.iamcyw.tower.schema.model.Schema;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

@ApplicationScoped
public class MessageProducer {

    private static final Logger LOGGER = Logger.getLogger(MessageProducer.class);

    @Singleton
    @Produces
    public MessageGateway messageGateway;

    public boolean initialize(Schema schema) {
        try {
            Bootstrap bootstrap = new Bootstrap(schema);

            messageGateway = bootstrap.getMessageGateway();

            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}

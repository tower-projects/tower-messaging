package io.iamcyw.tower.messaging.cdi.producer;

import io.iamcyw.tower.messaging.bootstrap.Bootstrap;
import io.iamcyw.tower.messaging.cdi.CDIMessageBus;
import io.iamcyw.tower.messaging.gateway.MessageGateway;
import io.iamcyw.tower.messaging.handle.interceptor.MessageInterceptor;
import io.iamcyw.tower.schema.model.Schema;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.context.ThreadContext;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Comparator;

@ApplicationScoped
public class MessageProducer {

    private static final Logger LOGGER = Logger.getLogger(MessageProducer.class);

    @Singleton
    @Produces
    public MessageGateway messageGateway;

    @Singleton
    @Produces
    public Schema schema;

    @Singleton
    @Produces
    public Bootstrap bootstrap;

    @Inject
    ThreadContext threadContext;

    @Inject
    ManagedExecutor managedExecutor;

    @Inject
    Instance<MessageInterceptor> messageInterceptors;

    public boolean initialize(Schema schema) {
        try {

            bootstrap = new Bootstrap(schema);

            bootstrap.setMessageBus(new CDIMessageBus(bootstrap, threadContext, managedExecutor));
            bootstrap.setMessageInterceptors(
                    messageInterceptors.stream().sorted(Comparator.comparing(MessageInterceptor::order)).toList());

            this.schema = schema;
            messageGateway = bootstrap.getMessageGateway();

            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}

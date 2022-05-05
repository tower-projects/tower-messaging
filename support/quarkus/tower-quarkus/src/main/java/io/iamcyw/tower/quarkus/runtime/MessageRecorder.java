package io.iamcyw.tower.quarkus.runtime;

import io.iamcyw.tower.messaging.cdi.producer.MessageProducer;
import io.iamcyw.tower.schema.model.Schema;
import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class MessageRecorder {

    public RuntimeValue<Boolean> createMessageService(BeanContainer beanContainer, Schema schema) {
        MessageProducer graphQLProducer = beanContainer.instance(MessageProducer.class);
        boolean initialize = graphQLProducer.initialize(schema);
        return new RuntimeValue<>(initialize);
    }

}

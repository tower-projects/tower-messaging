package io.iamcyw.tower.quarkus.runtime;

import io.iamcyw.tower.commandhandling.DefaultCommandBus;
import io.iamcyw.tower.messaging.BeanFactory;
import io.iamcyw.tower.messaging.handle.MessageHandlers;
import io.iamcyw.tower.messaging.handle.MethodInvoker;
import io.iamcyw.tower.quarkus.runtime.producer.MessageProducer;
import io.iamcyw.tower.queryhandling.DefaultQueryBus;
import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.runtime.annotations.Recorder;

import java.util.Map;
import java.util.function.Supplier;

@Recorder
public class MessageRecorder {

    private static final Map<String, Class<?>> primitiveTypes;

    static {
        primitiveTypes = Map.of(byte.class.getName(), byte.class, boolean.class.getName(), boolean.class,
                                char.class.getName(), char.class, short.class.getName(), short.class,
                                int.class.getName(), int.class, float.class.getName(), float.class,
                                double.class.getName(), double.class, long.class.getName(), long.class);
    }

    public static <T> Class<T> loadClass(String name) {
        if (primitiveTypes.containsKey(name)) {
            return (Class<T>) primitiveTypes.get(name);
        }
        try {
            return (Class<T>) Class.forName(name, false, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void config(BeanContainer beanContainer, MessageHandlers queryHandles, MessageHandlers commandHandles,
                       Map<String, Class<?>> domainMaps) {
        MessageProducer messageProducer = beanContainer.instance(MessageProducer.class);
        messageProducer.setQueryBus(new DefaultQueryBus(queryHandles));
        messageProducer.setCommandBus(new DefaultCommandBus(commandHandles));
        messageProducer.setDomainMaps(domainMaps);
    }

    public Supplier<MethodInvoker> invoker(String baseName) {
        return () -> {
            try {
                return (MethodInvoker) loadClass(baseName).getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Unable to generate endpoint invoker", e);
            }
        };
    }

    public <T> BeanFactory<T> factory(String targetClass, BeanContainer beanContainer) {
        return new ArcBeanFactory<>(loadClass(targetClass), beanContainer);
    }

}

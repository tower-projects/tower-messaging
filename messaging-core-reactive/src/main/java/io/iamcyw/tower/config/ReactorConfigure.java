package io.iamcyw.tower.config;

import io.iamcyw.tower.serialization.Serializer;

import java.util.function.Function;

public interface ReactorConfigure {
    void start();

    <C> ReactorConfigure registerComponent(Class<C> componentType,
                                           Function<ReactorConfiguration, ? extends C> componentBuilder);

    ReactorConfigure registerCommandHandler(Function<ReactorConfiguration, Object> annotatedCommandHandlerBuilder);

    ReactorConfigure registerQueryHandler(Function<ReactorConfiguration, Object> annotatedQueryHandlerBuilder);

    ReactorConfigure configureMessageSerializer(Function<ReactorConfiguration, Serializer> messageSerializerBuilder);

    ReactorConfiguration buildConfiguration();

}

package io.iamcyw.tower.config;

import io.iamcyw.tower.messaging.MessageClass;

import java.util.function.Function;

public interface ReactorConfigure {
    void start();

    <C> ReactorConfigure registerComponent(Class<C> componentType,
                                           Function<ReactorConfiguration, ? extends C> componentBuilder);

    ReactorConfigure registerCommandHandler(Function<ReactorConfiguration, MessageClass> commandClassBuilder);

    ReactorConfiguration buildConfiguration();

    ReactorConfigure registerQueryHandler(Function<ReactorConfiguration, MessageClass> annotatedQueryHandlerBuilder);

}

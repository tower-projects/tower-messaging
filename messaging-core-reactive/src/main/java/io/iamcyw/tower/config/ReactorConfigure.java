package io.iamcyw.tower.config;

import java.util.function.Function;

public interface ReactorConfigure {
    void start();

    <C> ReactorConfigure registerComponent(Class<C> componentType,
                                           Function<ReactorConfiguration, ? extends C> componentBuilder);

    ReactorConfigure registerCommandHandler(Function<ReactorConfiguration, Object> annotatedCommandHandlerBuilder);

    ReactorConfigure registerQueryHandler(Function<ReactorConfiguration, Object> annotatedQueryHandlerBuilder);

    ReactorConfiguration buildConfiguration();

}

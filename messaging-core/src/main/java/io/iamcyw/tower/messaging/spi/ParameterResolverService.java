package io.iamcyw.tower.messaging.spi;

import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.schema.model.Argument;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public interface ParameterResolverService {

    ServiceLoader<ParameterResolverService> parameterResolverService = ServiceLoader.load(
            ParameterResolverService.class);

    static List<ParameterResolverService> load() {
        return parameterResolverService.stream().map(ServiceLoader.Provider::get).collect(Collectors.toList());
    }

    Object resolveParameterValue(Message<?> message);

    boolean matches(Argument argument);


}

package io.iamcyw.tower.messaging.handle.helper;

import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.spi.ParameterResolverService;
import io.iamcyw.tower.schema.model.Argument;

import java.util.List;
import java.util.function.BiFunction;

public class ArgumentHelper {

    private final List<Argument> arguments;

    private final BiFunction<Argument, Message<?>, Object> customGetFc;

    public ArgumentHelper(List<Argument> arguments) {
        this(arguments, (argument, message) -> null);
    }

    public ArgumentHelper(List<Argument> arguments, BiFunction<Argument, Message<?>, Object> customGetFc) {
        this.arguments = arguments;
        this.customGetFc = customGetFc;
    }

    public Object[] getArguments(Message<?> message) {
        Object[] argumentObjects = new Object[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            Argument argument = arguments.get(i);
            if (argument.isDomainArgument()) {
                argumentObjects[i] = message.getPayload();
            } else if (argument.isMetaArgument()) {
                argumentObjects[i] = message.getMetaData();
            } else {
                Object result = customGetFc.apply(argument, message);
                if (result != null) {
                    argumentObjects[i] = result;
                    continue;
                }

                List<ParameterResolverService> parameterResolverServices = ParameterResolverService.load();
                for (ParameterResolverService parameterResolverService : parameterResolverServices) {
                    if (parameterResolverService.matches(argument)) {
                        argumentObjects[i] = parameterResolverService.resolveParameterValue(message);
                    }
                }

            }
        }
        return argumentObjects;
    }

}

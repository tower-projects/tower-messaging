package io.iamcyw.tower.messaging.handle.helper;

import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.spi.ParameterResolverService;
import io.iamcyw.tower.schema.model.Argument;

import java.util.LinkedList;
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
        List<Object> argumentObjects = new LinkedList<>();
        for (Argument argument : arguments) {
            if (argument.isDomainArgument()) {
                argumentObjects.add(message.getPayload());
            } else if (argument.isMetaArgument()) {
                argumentObjects.add(message.getMetaData());
            } else {
                Object result = customGetFc.apply(argument, message);
                if (result != null) {
                    argumentObjects.add(result);
                    continue;
                }

                List<ParameterResolverService> parameterResolverServices = ParameterResolverService.load();
                for (ParameterResolverService parameterResolverService : parameterResolverServices) {
                    if (parameterResolverService.matches(argument)) {
                        argumentObjects.add(parameterResolverService.resolveParameterValue(message));
                    }
                }
            }
        }
        return argumentObjects.toArray();
    }

}

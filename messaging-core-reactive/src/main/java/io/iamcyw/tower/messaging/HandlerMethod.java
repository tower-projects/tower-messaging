package io.iamcyw.tower.messaging;

import io.iamcyw.tower.messaging.annotation.MessageHandlerInvocationException;
import io.iamcyw.tower.messaging.annotation.ParameterResolver;
import io.iamcyw.tower.messaging.annotation.ParameterResolverFactory;
import io.iamcyw.tower.messaging.annotation.UnsupportedHandlerException;
import io.iamcyw.tower.utils.ReflectionUtils;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class HandlerMethod {

    private final Class<?> payloadType;

    private final int parameterCount;

    private final ParameterResolver<?>[] parameterResolvers;

    private final Method method;

    private final Object target;

    private final String commandName;

    public HandlerMethod(String commandName, Method method, Object target, Class<?> explicitPayloadType,
                         ParameterResolverFactory parameterResolverFactory) {
        this.method = method;
        this.target = target;
        ReflectionUtils.ensureAccessible(this.method);
        Parameter[] parameters = method.getParameters();
        this.parameterCount = method.getParameterCount();
        parameterResolvers = new ParameterResolver[parameterCount];
        Class<?> supportedPayloadType = explicitPayloadType;
        this.commandName = StringUtils.isBlank(commandName) ? parameters[0].getType().getSimpleName() : commandName;
        for (int i = 0; i < parameterCount; i++) {
            parameterResolvers[i] = parameterResolverFactory.createInstance(method, parameters, i);
            if (parameterResolvers[i] == null) {
                throw new UnsupportedHandlerException(
                        "Unable to resolve parameter " + i + " (" + parameters[i].getType().getSimpleName() +
                                ") in handler " + method.toGenericString() + ".", method);
            }
            if (supportedPayloadType.isAssignableFrom(parameterResolvers[i].supportedPayloadType())) {
                supportedPayloadType = parameterResolvers[i].supportedPayloadType();
            } else if (!parameterResolvers[i].supportedPayloadType().isAssignableFrom(supportedPayloadType)) {
                throw new UnsupportedHandlerException(String.format(
                        "The method %s seems to have parameters that put conflicting requirements on the payload type" +
                                " applicable on that method: %s vs %s", method.toGenericString(), supportedPayloadType,
                        parameterResolvers[i].supportedPayloadType()), method);
            }
        }
        this.payloadType = supportedPayloadType;
    }

    public Class<?> getPayloadType() {
        return payloadType;
    }

    public <R> Multi<R> handle(Message<?> message) {
        try {
            if (method instanceof Method) {
                Object result = method.invoke(target, resolveParameterValues(message));
                if (result instanceof Uni) {
                    return ((Uni<R>) result).toMulti();
                } else if (result instanceof Multi) {
                    return (Multi<R>) result;
                } else {
                    return Multi.createFrom().<R>item((R) result);
                }
            } else {
                throw new IllegalStateException("What kind of handler is this?");
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            checkAndRethrowForExceptionOrError(e);
            throw new MessageHandlerInvocationException(
                    String.format("Error handling an object of type [%s]", message.getPayloadType()), e);
        }
    }

    private void checkAndRethrowForExceptionOrError(ReflectiveOperationException e) {
        if (e.getCause() instanceof Exception) {
            throw new RuntimeException(e.getMessage(), e);
        } else if (e.getCause() instanceof Error) {
            throw (Error) e.getCause();
        }
    }

    private Object[] resolveParameterValues(Message<?> message) {
        Object[] params = new Object[parameterCount];
        for (int i = 0; i < parameterCount; i++) {
            params[i] = parameterResolvers[i].resolveParameterValue(message);
        }
        return params;
    }

}

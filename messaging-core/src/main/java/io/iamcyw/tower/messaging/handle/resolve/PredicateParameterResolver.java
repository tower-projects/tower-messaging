package io.iamcyw.tower.messaging.handle.resolve;

import io.iamcyw.tower.messaging.Message;

public class PredicateParameterResolver<T> implements ParameterResolver<T> {
    private final Class<T> parameterType;

    public PredicateParameterResolver(Class<T> parameterType) {
        this.parameterType = parameterType;
    }

    @Override
    public T resolveParameterValue(Message message) {
        String[] parameter = message.getMetaData().getPredicateParameter();
        if (parameterType.isArray()) {
            return parameterType.cast(parameter);
        } else if (parameterType == String.class) {
            return parameterType.cast(parameter[0]);
        }
        return null;
    }

    @Override
    public boolean matches(Message message) {
        return false;
    }

}

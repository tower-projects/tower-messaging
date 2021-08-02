package io.iamcyw.tower.messaging.annotation;


import io.iamcyw.tower.common.Priority;
import io.iamcyw.tower.common.annotation.AnnotationUtils;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.MetaData;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

/**
 * Factory for the default parameter resolvers. This factory is capable for providing parameter resolvers for Message,
 * MetaData and @MetaDataValue annotated parameters.
 */
@Priority(Priority.FIRST)
public class DefaultParameterResolverFactory implements ParameterResolverFactory {

    @Override
    public ParameterResolver createInstance(Executable executable, Parameter[] parameters, int parameterIndex) {

        Class<?> parameterType = parameters[parameterIndex].getType();
        if (Message.class.isAssignableFrom(parameterType)) {
            return new MessageParameterResolver(parameterType);
        }
        MetaDataValue metaDataValueAnnotation = AnnotationUtils.findAnnotation(parameters[parameterIndex],
                                                                               MetaDataValue.class);
        if (metaDataValueAnnotation != null) {
            return new AnnotatedMetaDataParameterResolver(metaDataValueAnnotation, parameterType);
        }
        if (MetaData.class.isAssignableFrom(parameterType)) {
            return MetaDataParameterResolver.INSTANCE;
        }
        if (parameterIndex == 0) {
            return new PayloadParameterResolver(parameterType);
        }
        return null;
    }

    private static class AnnotatedMetaDataParameterResolver implements ParameterResolver<Object> {

        private final MetaDataValue metaDataValue;

        private final Class parameterType;

        public AnnotatedMetaDataParameterResolver(MetaDataValue metaDataValue, Class parameterType) {
            this.metaDataValue = metaDataValue;
            this.parameterType = parameterType;
        }

        @Override
        public Object resolveParameterValue(Message message) {
            return message.getMetaData().get(metaDataValue.value());
        }

        @Override
        public boolean matches(Message message) {
            return !(parameterType.isPrimitive() || metaDataValue.required()) ||
                    (message.getMetaData().containsKey(metaDataValue.value()) &&
                            parameterType.isInstance(message.getMetaData().get(metaDataValue.value())));
        }

    }

    private static final class MetaDataParameterResolver implements ParameterResolver {

        private static final MetaDataParameterResolver INSTANCE = new MetaDataParameterResolver();

        private MetaDataParameterResolver() {
        }

        @Override
        public Object resolveParameterValue(Message message) {
            return message.getMetaData();
        }

        @Override
        public boolean matches(Message message) {
            return true;
        }

    }

    private static class MessageParameterResolver implements ParameterResolver {

        private final Class<?> parameterType;

        public MessageParameterResolver(Class<?> parameterType) {
            this.parameterType = parameterType;
        }

        @Override
        public Object resolveParameterValue(Message message) {
            return message;
        }

        @Override
        public boolean matches(Message message) {
            return parameterType.isInstance(message);
        }

    }

}

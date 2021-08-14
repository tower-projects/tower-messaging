package io.iamcyw.tower.messaging.annotation;


import io.iamcyw.tower.common.Priority;
import io.iamcyw.tower.common.annotation.AnnotationUtils;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.MetaData;
import io.iamcyw.tower.messaging.parameter.*;

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
            return new AnnotatedMetaDataParameterResolver(metaDataValueAnnotation.value(),
                                                          metaDataValueAnnotation.required(), parameterType);
        }
        if (MetaData.class.isAssignableFrom(parameterType)) {
            return MetaDataParameterResolver.INSTANCE;
        }
        if (parameterIndex == 0) {
            return new PayloadParameterResolver(parameterType);
        }
        return null;
    }

}

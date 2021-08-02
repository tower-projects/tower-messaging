package io.iamcyw.tower.messaging.annotation;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

@FunctionalInterface
public interface ParameterResolverFactory {

    /**
     * If available, creates a ParameterResolver instance that can provide a parameter of type
     * {@code parameterType} for a given message.
     * <p>
     * If the ParameterResolverFactory cannot provide a suitable ParameterResolver, returns {@code null}.
     *
     * @param executable     The executable (constructor or method) to inspect
     * @param parameters     The parameters on the executable to inspect
     * @param parameterIndex The index of the parameter to return a ParameterResolver for
     * @return a suitable ParameterResolver, or {@code null} if none is found
     */
    ParameterResolver createInstance(Executable executable, Parameter[] parameters, int parameterIndex);

}

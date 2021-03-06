/*
 * Copyright (c) 2010-2017. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.iamcyw.tower.spring.config.annotation;

import io.iamcyw.tower.messaging.annotation.ParameterResolverFactory;
import io.iamcyw.tower.queryhandling.QueryHandler;
import io.iamcyw.tower.queryhandling.QueryHandlerAdapter;
import io.iamcyw.tower.queryhandling.annotation.AnnotationQueryHandlerAdapter;
import io.iamcyw.tower.spring.config.AbstractAnnotationHandlerBeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Spring Bean post processor that automatically generates an adapter for each bean containing {@link QueryHandler}
 * annotated methods.
 *
 * @author Marc Gathier
 * @since 3.1
 */
public class AnnotationQueryHandlerBeanPostProcessor extends AbstractAnnotationHandlerBeanPostProcessor<QueryHandlerAdapter, AnnotationQueryHandlerAdapter> {
    @Override
    protected Class<?>[] getAdapterInterfaces() {
        return new Class[]{QueryHandlerAdapter.class};
    }

    @Override
    protected boolean isPostProcessingCandidate(Class<?> targetClass) {
        return this.hasQueryHandlerMethod(targetClass);
    }

    private boolean hasQueryHandlerMethod(Class<?> beanClass) {
        AtomicBoolean result = new AtomicBoolean(false);
        ReflectionUtils.doWithMethods(beanClass, new HasQueryHandlerAnnotationMethodCallback(result));
        return result.get();
    }

    @Override
    protected AnnotationQueryHandlerAdapter initializeAdapterFor(Object o,
                                                                 ParameterResolverFactory parameterResolverFactory) {
        return new AnnotationQueryHandlerAdapter<>(o, parameterResolverFactory);
    }

    private class HasQueryHandlerAnnotationMethodCallback implements ReflectionUtils.MethodCallback {
        private final AtomicBoolean result;

        public HasQueryHandlerAnnotationMethodCallback(AtomicBoolean result) {
            this.result = result;
        }

        @Override
        public void doWith(Method method) throws IllegalArgumentException {
            if (method.isAnnotationPresent(QueryHandler.class)) {
                result.set(true);
            }
        }

    }

}

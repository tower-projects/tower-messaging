/*
 * Copyright (c) 2010-2018. Axon Framework
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

import io.iamcyw.tower.commandhandling.AnnotationCommandHandlerAdapter;
import io.iamcyw.tower.commandhandling.CommandHandler;
import io.iamcyw.tower.commandhandling.CommandMessage;
import io.iamcyw.tower.commandhandling.CommandMessageHandler;
import io.iamcyw.tower.common.annotation.AnnotationUtils;
import io.iamcyw.tower.messaging.MessageHandler;
import io.iamcyw.tower.messaging.annotation.HandlerDefinition;
import io.iamcyw.tower.messaging.annotation.ParameterResolverFactory;
import io.iamcyw.tower.spring.config.AbstractAnnotationHandlerBeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Spring Bean post processor that automatically generates an adapter for each bean containing {@link CommandHandler}
 * annotated methods.
 *
 * @author Allard Buijze
 * @since 0.5
 */
public class AnnotationCommandHandlerBeanPostProcessor extends AbstractAnnotationHandlerBeanPostProcessor<MessageHandler<CommandMessage<?>>, AnnotationCommandHandlerAdapter<?>> {

    @Override
    protected Class<?>[] getAdapterInterfaces() {
        return new Class[]{CommandMessageHandler.class};
    }

    @Override
    protected boolean isPostProcessingCandidate(Class<?> targetClass) {
        return hasCommandHandlerMethod(targetClass);
    }

    @Override
    protected AnnotationCommandHandlerAdapter<?> initializeAdapterFor(Object bean, ParameterResolverFactory parameterResolverFactory, HandlerDefinition handlerDefinition) {
        return new AnnotationCommandHandlerAdapter<>(bean, parameterResolverFactory, handlerDefinition);
    }

    private boolean hasCommandHandlerMethod(Class<?> beanClass) {
        final AtomicBoolean result = new AtomicBoolean(false);
        ReflectionUtils.doWithMethods(beanClass, new HasCommandHandlerAnnotationMethodCallback(result));
        return result.get();
    }

    private static final class HasCommandHandlerAnnotationMethodCallback implements ReflectionUtils.MethodCallback {

        private final AtomicBoolean result;

        private HasCommandHandlerAnnotationMethodCallback(AtomicBoolean result) {
            this.result = result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void doWith(Method method) throws IllegalArgumentException {
            if (AnnotationUtils.findAnnotationAttributes(method, CommandHandler.class)
                    .isPresent()) {
                result.set(true);
            }
        }

    }

}

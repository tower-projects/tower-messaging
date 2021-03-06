/*
 * Copyright (c) 2010-2016. Axon Framework
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.iamcyw.tower.commandhandling;

import io.iamcyw.tower.common.Priority;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.annotation.ParameterResolver;
import io.iamcyw.tower.messaging.annotation.ParameterResolverFactory;
import io.iamcyw.tower.messaging.unitofwork.CurrentUnitOfWork;
import io.iamcyw.tower.messaging.unitofwork.UnitOfWork;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

/**
 * ParameterResolverFactory that add support for the UnitOfWork parameter type in annotated handlers.
 */
@Priority(Priority.FIRST)
public class CurrentUnitOfWorkParameterResolverFactory implements ParameterResolverFactory, ParameterResolver {

    @Override
    public ParameterResolver createInstance(Executable executable, Parameter[] parameters, int parameterIndex) {
        if (UnitOfWork.class.equals(parameters[parameterIndex].getType())) {
            return this;
        }
        return null;
    }

    @Override
    public Object resolveParameterValue(Message message) {
        if (!CurrentUnitOfWork.isStarted()) {
            return null;
        }
        return CurrentUnitOfWork.get();
    }

    @Override
    public boolean matches(Message message) {
        return true;
    }

}

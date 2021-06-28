/*
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.iamcyw.tower.messaging.unitofwork;


import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.utils.Assert;
import io.iamcyw.tower.utils.i18n.I18ns;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * Implementation of the UnitOfWork that processes a single message.
 */
public class DefaultUnitOfWork<T extends Message<?>> extends AbstractUnitOfWork<T> {

    private final MessageProcessingContext<T> processingContext;

    /**
     * Initializes a Unit of Work (without starting it).
     *
     * @param message the message that will be processed in the context of the unit of work
     */
    public DefaultUnitOfWork(T message) {
        processingContext = new MessageProcessingContext<>(message);
    }

    /**
     * Starts a new DefaultUnitOfWork instance, registering it a CurrentUnitOfWork. This methods returns the started
     * UnitOfWork instance.
     * <p>
     * Note that this Unit Of Work type is not meant to be shared among different Threads. A single DefaultUnitOfWork
     * instance should be used exclusively by the Thread that created it.
     *
     * @param message the message that will be processed in the context of the unit of work
     * @return the started UnitOfWork instance
     */
    public static <T extends Message<?>> DefaultUnitOfWork<T> startAndGet(T message) {
        DefaultUnitOfWork<T> uow = new DefaultUnitOfWork<>(message);
        uow.start();
        return uow;
    }

    @Override
    public <R> R executeWithResult(Callable<R> task, RollbackConfiguration rollbackConfiguration) throws Exception {
        if (phase() == Phase.NOT_STARTED) {
            start();
        }
        Assert.state(phase() == Phase.STARTED,
                     I18ns.create().content("The UnitOfWork has an incompatible phase: ").args(phase()).apply());
        R result;
        try {
            result = task.call();
        } catch (Error | Exception e) {
            if (rollbackConfiguration.rollBackOn(e)) {
                rollback(e);
            } else {
                setExecutionResult(new ExecutionResult(e));
                commit();
            }
            throw e;
        }
        setExecutionResult(new ExecutionResult(result));
        commit();
        return result;
    }

    @Override
    protected void setRollbackCause(Throwable cause) {
        setExecutionResult(new ExecutionResult(cause));
    }

    @Override
    protected void notifyHandlers(Phase phase) {
        processingContext.notifyHandlers(this, phase);
    }

    @Override
    protected void addHandler(Phase phase, Consumer<UnitOfWork<T>> handler) {
        Assert.state(!phase.isBefore(phase()), I18ns.create().content(
                "Cannot register a listener for phase: {} because the Unit of Work is already in a later phase: {}")
                                                    .args(phase, phase()).apply());
        processingContext.addHandler(phase, handler);
    }

    @Override
    public T getMessage() {
        return processingContext.getMessage();
    }

    @Override
    public UnitOfWork<T> transformMessage(Function<T, ? extends Message<?>> transformOperator) {
        processingContext.transformMessage(transformOperator);
        return this;
    }

    @Override
    public ExecutionResult getExecutionResult() {
        return processingContext.getExecutionResult();
    }

    @Override
    protected void setExecutionResult(ExecutionResult executionResult) {
        processingContext.setExecutionResult(executionResult);
    }

}

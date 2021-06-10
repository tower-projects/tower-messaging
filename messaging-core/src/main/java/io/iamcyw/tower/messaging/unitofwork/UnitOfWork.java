package io.iamcyw.tower.messaging.unitofwork;

import io.iamcyw.tower.common.transaction.Transaction;
import io.iamcyw.tower.common.transaction.TransactionManager;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.MetaData;
import io.iamcyw.tower.messaging.ResultMessage;
import io.iamcyw.tower.messaging.correlation.CorrelationDataProvider;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public interface UnitOfWork<T extends Message<?>> {

    /**
     * 开始当前的工作单元。 UnitOfWork实例已在CurrentUnitOfWork中注册
     */
    void start();

    void commit();

    default void rollback() {
        rollback(null);
    }

    void rollback(Throwable cause);

    default boolean isActive() {
        return phase().isStarted();
    }

    /**
     * Check if the Unit of Work has been rolled back.
     *
     * @return {@code true} if the unit of work was rolled back, {@code false} otherwise.
     */
    boolean isRolledBack();

    /**
     * Check if the Unit of Work is the 'currently' active Unit of Work returned by {@link CurrentUnitOfWork#get()}.
     *
     * @return {@code true} if the Unit of Work is the currently active Unit of Work
     */
    default boolean isCurrent() {
        return CurrentUnitOfWork.isStarted() && CurrentUnitOfWork.get() == this;
    }

    Phase phase();

    void onPrepareCommit(Consumer<UnitOfWork<T>> handler);

    void onCommit(Consumer<UnitOfWork<T>> handler);

    void afterCommit(Consumer<UnitOfWork<T>> handler);

    void onRollback(Consumer<UnitOfWork<T>> handler);

    void onCleanup(Consumer<UnitOfWork<T>> handler);

    Optional<UnitOfWork<?>> parent();

    default boolean isRoot() {
        return !parent().isPresent();
    }

    default UnitOfWork<?> root() {
        //noinspection unchecked // cast is used to remove inspection error in IDE
        return parent().map(UnitOfWork::root)
                .orElse((UnitOfWork) this);
    }

    T getMessage();

    UnitOfWork<T> transformMessage(Function<T, ? extends Message<?>> transformOperator);

    MetaData getCorrelationData();

    void registerCorrelationDataProvider(CorrelationDataProvider correlationDataProvider);

    Map<String, Object> resources();

    default <R> R getResource(String name) {
        return (R) resources().get(name);
    }

    default <R> R getOrComputeResource(String key, Function<? super String, R> mappingFunction) {
        return (R) resources().computeIfAbsent(key, mappingFunction);
    }

    default <R> R getOrDefaultResource(String key, R defaultValue) {
        return (R) resources().getOrDefault(key, defaultValue);
    }

    default void attachTransaction(TransactionManager transactionManager) {
        try {
            Transaction transaction = transactionManager.startTransaction();
            onCommit(u -> transaction.commit());
            onRollback(u -> transaction.rollback());
        } catch (Throwable t) {
            rollback(t);
            throw t;
        }
    }

    default void execute(Runnable task) {
        execute(task, RollbackConfigurationType.ANY_THROWABLE);
    }

    default void execute(Runnable task, RollbackConfiguration rollbackConfiguration) {
        ResultMessage<?> resultMessage = executeWithResult(() -> {
            task.run();
            return null;
        }, rollbackConfiguration);
        if (resultMessage.isExceptional()) {
            throw (RuntimeException) resultMessage.exceptionResult();
        }
    }

    default <R> ResultMessage<R> executeWithResult(Callable<R> task) {
        return executeWithResult(task, RollbackConfigurationType.ANY_THROWABLE);
    }

    <R> ResultMessage<R> executeWithResult(Callable<R> task, RollbackConfiguration rollbackConfiguration);

    ExecutionResult getExecutionResult();

    enum Phase {

        /**
         * Indicates that the unit of work has been created but has not been registered with the {@link
         * CurrentUnitOfWork} yet.
         */
        NOT_STARTED(false, false, false),

        /**
         * Indicates that the Unit of Work has been registered with the {@link CurrentUnitOfWork} but has not been
         * committed, because its Message has not been processed yet.
         */
        STARTED(true, false, false),

        /**
         * Indicates that the Unit of Work is preparing its commit. This means that {@link #commit()} has been invoked
         * on the Unit of Work, indicating that the Message {@link #getMessage()} of the Unit of Work has been
         * processed.
         * <p/>
         * All handlers registered to be notified before commit {@link #onPrepareCommit} will be invoked. If no
         * exception is raised by any of the handlers the Unit of Work will go into the {@link #COMMIT} phase, otherwise
         * it will be rolled back.
         */
        PREPARE_COMMIT(true, false, false),

        /**
         * Indicates that the Unit of Work has been committed and is passed the {@link #PREPARE_COMMIT} phase.
         */
        COMMIT(true, true, false),

        /**
         * Indicates that the Unit of Work is being rolled back. Generally this is because an exception was raised while
         * processing the {@link #getMessage() message} or while the Unit of Work was being committed.
         */
        ROLLBACK(true, true, true),

        /**
         * Indicates that the Unit of Work is after a successful commit. In this phase the Unit of Work cannot be rolled
         * back anymore.
         */
        AFTER_COMMIT(true, true, true),

        /**
         * Indicates that the Unit of Work is after a successful commit or after a rollback. Any resources tied to this
         * Unit of Work should be released.
         */
        CLEANUP(false, true, true),

        /**
         * Indicates that the Unit of Work is at the end of its life cycle. This phase is final.
         */
        CLOSED(false, true, true);

        private final boolean started;

        private final boolean reverseCallbackOrder;

        private final boolean suppressHandlerErrors;

        Phase(boolean started, boolean reverseCallbackOrder, boolean suppressHandlerErrors) {
            this.started = started;
            this.reverseCallbackOrder = reverseCallbackOrder;
            this.suppressHandlerErrors = suppressHandlerErrors;
        }

        /**
         * Check if a Unit of Work in this phase has been started, i.e. is registered with the {@link
         * CurrentUnitOfWork}.
         *
         * @return {@code true} if the Unit of Work is started when in this phase, {@code false} otherwise
         */
        public boolean isStarted() {
            return started;
        }

        /**
         * Check whether registered handlers for this phase should be invoked in the order of registration (first
         * registered handler is invoked first) or in the reverse order of registration (last registered handler is
         * invoked first).
         *
         * @return {@code true} if the order of invoking handlers in this phase should be in the reverse order of
         * registration, {@code false} otherwise.
         */
        public boolean isReverseCallbackOrder() {
            return reverseCallbackOrder;
        }

        /**
         * Indicates whether the handlers triggered for this phase should have their exceptions suppressed. This is the
         * case for phases where cleanup or post-error processing is done. Exceptions in those phases should not trigger
         * exceptions, but to a best-effort attempt to clean up.
         *
         * @return {@code true} when errors should be suppressed, otherise {@code false}
         */
        public boolean isSuppressHandlerErrors() {
            return suppressHandlerErrors;
        }

        /**
         * Check if this Phase comes before given other {@code phase}.
         *
         * @param phase The other Phase
         * @return {@code true} if this comes before the given {@code phase}, {@code false} otherwise.
         */
        public boolean isBefore(Phase phase) {
            return ordinal() < phase.ordinal();
        }

        /**
         * Check if this Phase comes after given other {@code phase}.
         *
         * @param phase The other Phase
         * @return {@code true} if this comes after the given {@code phase}, {@code false} otherwise.
         */
        public boolean isAfter(Phase phase) {
            return ordinal() > phase.ordinal();
        }
    }

}

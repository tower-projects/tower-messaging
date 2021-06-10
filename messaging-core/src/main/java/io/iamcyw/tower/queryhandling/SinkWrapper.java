package io.iamcyw.tower.queryhandling;

/**
 * Abstraction interface to bridge old {@code FluxSink} and {@link reactor.core.publisher.Sinks.Many} API with a common
 * API.
 */
public interface SinkWrapper<T> {

    /**
     * Wrapper around Sink complete().
     */
    void complete();

    /**
     * Wrapper around Sink next(Object).
     *
     * @param value to be passed to the delegate sink
     */
    void next(T value);

    /**
     * Wrapper around Sink error(Throwable).
     *
     * @param t to be passed to the delegate sink
     */
    void error(Throwable t);

}

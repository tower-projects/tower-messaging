package io.iamcyw.tower.commandhandling;

import io.smallrye.mutiny.Uni;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.function.Function;

public class DefaultReactorCommandFilterChain implements ReactorCommandFilterChain {

    private final ReactorCommandFilter filter;

    private final ReactorCommandFilterChain nextChain;

    public DefaultReactorCommandFilterChain(ReactorCommandFilter filter, ReactorCommandFilterChain nextChain) {
        this.filter = filter;
        this.nextChain = nextChain;
    }

    public static <A> ReactorCommandFilterChain buildChain(final List<ReactorCommandFilter> filters,
                                                           Function<CommandMessage, Uni<A>> target) {
        return build(new ArrayDeque<>(filters), new ReactorCommandFilterChain() {
            @Override
            public <B> Uni<B> filter(CommandMessage commandMessage) {
                return (Uni<B>) target.apply(commandMessage);
            }
        });
    }

    private static ReactorCommandFilterChain build(Deque<ReactorCommandFilter> filters,
                                                   ReactorCommandFilterChain nextChain) {
        if (filters.isEmpty()) {
            return nextChain;
        } else {
            return build(filters, new DefaultReactorCommandFilterChain(filters.pollLast(), nextChain));
        }
    }

    @Override
    public <R> Uni<R> filter(CommandMessage commandMessage) {
        return filter.filter(commandMessage, nextChain);
    }

}

package io.iamcyw.tower.commandhandling;

import io.smallrye.mutiny.Multi;

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

    public static <A> ReactorCommandFilterChain build(List<ReactorCommandFilter> filters,
                                                      Function<CommandMessage, Multi<A>> target) {

        return buildChain(new ArrayDeque(filters), new ReactorCommandFilterChain() {
            @Override
            public <B> Multi<B> filter(CommandMessage commandMessage) {
                return (Multi<B>) target.apply(commandMessage);
            }
        });
    }

    public static ReactorCommandFilterChain buildChain(Deque<ReactorCommandFilter> filters,
                                                       ReactorCommandFilterChain nextChain) {
        if (filters.isEmpty()) {
            return nextChain;
        } else {
            return buildChain(filters, new DefaultReactorCommandFilterChain(filters.pollLast(), nextChain));
        }
    }

    @Override
    public <R> Multi<R> filter(CommandMessage commandMessage) {
        return filter.filter(commandMessage, nextChain);
    }

}

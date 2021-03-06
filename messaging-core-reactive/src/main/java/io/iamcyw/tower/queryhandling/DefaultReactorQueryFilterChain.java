package io.iamcyw.tower.queryhandling;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.function.Function;

public class DefaultReactorQueryFilterChain implements ReactorQueryFilterChain {

    private final ReactorQueryFilter filter;

    private final ReactorQueryFilterChain nextChain;

    public DefaultReactorQueryFilterChain(ReactorQueryFilter filter, ReactorQueryFilterChain nextChain) {
        this.filter = filter;
        this.nextChain = nextChain;
    }

    public static <A> ReactorQueryFilterChain buildChain(List<ReactorQueryFilter> filters,
                                                         Function<QueryMessage, Uni<A>> target) {
        return build(new ArrayDeque<>(filters), new ReactorQueryFilterChain() {
            @Override
            public <R> Uni<R> filter(QueryMessage queryMessage) {
                return (Uni<R>) target.apply(queryMessage);
            }
        });
    }

    private static ReactorQueryFilterChain build(Deque<ReactorQueryFilter> filters, ReactorQueryFilterChain nextChain) {
        if (filters.isEmpty()) {
            return nextChain;
        } else {
            return build(filters, new DefaultReactorQueryFilterChain(filters.pollLast(), nextChain));
        }
    }


    @Override
    public <R> Uni<R> filter(QueryMessage queryMessage) {
        return filter.filter(queryMessage, nextChain);
    }

}

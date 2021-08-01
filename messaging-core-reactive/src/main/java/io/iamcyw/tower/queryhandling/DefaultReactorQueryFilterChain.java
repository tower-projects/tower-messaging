package io.iamcyw.tower.queryhandling;

import io.smallrye.mutiny.Multi;

import java.util.List;
import java.util.function.Function;

public class DefaultReactorQueryFilterChain implements ReactorQueryFilterChain {
    private final int index;

    private final List<ReactorQueryFilter> filters;

    public DefaultReactorQueryFilterChain(List<ReactorQueryFilter> filters) {
        this.filters = filters;
        this.index = 0;
    }

    private DefaultReactorQueryFilterChain(DefaultReactorQueryFilterChain parent, int index) {
        this.filters = parent.getFilters();
        this.index = index;
    }

    public List<ReactorQueryFilter> getFilters() {
        return filters;
    }

    @Override
    public <Q, R> Multi<QueryResponseMessage<R>> filter(QueryMessage<Q, R> queryMessage,
                                                        Function<QueryMessage<Q, R>, Multi<QueryResponseMessage<R>>> target) {
        return Multi.createFrom().deferred(() -> {
            if (this.index < filters.size()) {
                ReactorQueryFilter filter = filters.get(this.index);
                DefaultReactorQueryFilterChain chain = new DefaultReactorQueryFilterChain(this, this.index + 1);
                return filter.filter(queryMessage, chain);
            } else {
                return target.apply(queryMessage);
            }
        });
    }

}

package io.iamcyw.tower.queryhandling;

import io.smallrye.mutiny.Multi;

import java.util.List;

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
    public <Q, R> Multi<R> filter(QueryMessage queryMessage) {
        return null;
    }

}

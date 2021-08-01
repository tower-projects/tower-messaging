package io.iamcyw.tower.commandhandling;

import io.smallrye.mutiny.Multi;

import java.util.List;
import java.util.function.Function;

public class DefaultReactorCommandFilterChain implements ReactorCommandFilterChain {

    private final int index;

    private final List<ReactorCommandFilter> filters;

    public DefaultReactorCommandFilterChain(List<ReactorCommandFilter> filters) {
        this.filters = filters;
        this.index = 0;
    }

    private DefaultReactorCommandFilterChain(DefaultReactorCommandFilterChain parent, int index) {
        this.filters = parent.getFilters();
        this.index = index;
    }

    public List<ReactorCommandFilter> getFilters() {
        return filters;
    }

    @Override
    public <C, R> Multi<R> filter(CommandMessage<C> commandMessage, Function<CommandMessage<C>, Multi<R>> target) {
        return Multi.createFrom().deferred(() -> {
            if (this.index < filters.size()) {
                ReactorCommandFilter filter = filters.get(this.index);
                DefaultReactorCommandFilterChain chain = new DefaultReactorCommandFilterChain(this, this.index + 1);
                return filter.filter(commandMessage, chain);
            } else {
                return target.apply(commandMessage);
            }
        });
    }

}

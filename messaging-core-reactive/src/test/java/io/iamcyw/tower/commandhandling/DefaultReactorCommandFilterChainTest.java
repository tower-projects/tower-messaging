package io.iamcyw.tower.commandhandling;

import io.smallrye.mutiny.Multi;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

class DefaultReactorCommandFilterChainTest {

    @Test
    void build() {
        ReactorCommandFilter filter1 = new ReactorCommandFilter() {
            @Override
            public <C, R> Multi<R> filter(CommandMessage exchange, ReactorCommandFilterChain chain) {
                Logger.getLogger("filter-1").info("pre 1");
                return chain.<List<String>>filter(exchange).<R>map(string -> {
                    Logger.getLogger("filter-1").info("post 1");
                    string.add("filter1");
                    return (R) string;
                });
            }
        };
        ReactorCommandFilter filter2 = new ReactorCommandFilter() {
            @Override
            public <C, R> Multi<R> filter(CommandMessage exchange, ReactorCommandFilterChain chain) {
                Logger.getLogger("filter-2").info("pre 2");
                return chain.<List<String>>filter(exchange).<R>map(string -> {
                    Logger.getLogger("filter-2").info("post 2");
                    string.add("filter2");
                    return (R) string;
                });
            }
        };

        Function<CommandMessage, Multi<Object>> target = commandMessage -> Multi.createFrom().item(() -> {
            List<String> array = new ArrayList<>(3);
            Logger.getLogger("handle").info("handle");
            array.add("handle");
            return array;
        });

        DefaultReactorCommandFilterChain.buildChain(Arrays.asList(filter1, filter2), target)
                                        .filter(GenericCommandMessage.asCommandMessage("send")).collect().asList()
                                        .await().indefinitely();
    }

}
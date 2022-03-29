package io.iamcyw.tower.utils.collect;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static io.iamcyw.tower.utils.Assert.assertNotNull;

public final class ListKit {

    private ListKit() {
        throw new IllegalStateException("Utility class");
    }

    public static <T, R> List<R> flatMap(Iterable<? extends T> iterable, Function<T, List<R>> mapper) {
        assertNotNull(iterable);
        assertNotNull(mapper);
        List<R> list = new ArrayList<>();
        for (T t : iterable) {
            List<R> r = mapper.apply(t);
            list.addAll(r);
        }
        return list;
    }

    /**
     * This is more efficient than `c.stream().map().collect()` because it does not create the intermediate objects
     * needed
     * for the flexible style.  Benchmarking has shown this to outperform `stream()`.
     *
     * @param iterable the iterable to map
     * @param mapper   the mapper function
     * @param <T>      for two
     * @param <R>      for result
     * @return a map immutable list of results
     */
    public static <T, R> List<R> map(Iterable<? extends T> iterable, Function<? super T, ? extends R> mapper) {
        assertNotNull(iterable);
        assertNotNull(mapper);
        List<R> builder = new ArrayList<>();
        for (T t : iterable) {
            R r = mapper.apply(t);
            builder.add(r);
        }
        return builder;
    }

    public static <R> List<R> filter(Iterable<R> iterable, Function<R, Boolean> filter) {
        assertNotNull(iterable);
        assertNotNull(filter);
        List<R> builder = new ArrayList<>();
        for (R r : iterable) {
            if (Boolean.TRUE.equals(filter.apply(r))) {
                builder.add(r);
            }
        }
        return builder;
    }

}

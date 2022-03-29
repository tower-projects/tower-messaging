package io.iamcyw.tower.collect;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.function.Function;

import static io.iamcyw.tower.utils.Assert.assertNotNull;

public final class ImmutableKit {

    private ImmutableKit() {
        throw new IllegalStateException("Utility class");
    }

    public static <T, R> ImmutableList<R> flatMap(Iterable<? extends T> iterable, Function<T, List<R>> mapper) {
        assertNotNull(iterable);
        assertNotNull(mapper);
        ImmutableList.Builder<R> builder = ImmutableList.builder();
        for (T t : iterable) {
            List<R> r = mapper.apply(t);
            builder.addAll(r);
        }
        return builder.build();
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
    public static <T, R> ImmutableList<R> map(Iterable<? extends T> iterable, Function<? super T, ? extends R> mapper) {
        assertNotNull(iterable);
        assertNotNull(mapper);
        ImmutableList.Builder<R> builder = ImmutableList.builder();
        for (T t : iterable) {
            R r = mapper.apply(t);
            builder.add(r);
        }
        return builder.build();
    }

    public static <R> ImmutableList<R> filter(Iterable<R> iterable, Function<R, Boolean> filter) {
        assertNotNull(iterable);
        assertNotNull(filter);
        ImmutableList.Builder<R> builder = ImmutableList.builder();
        for (R r : iterable) {
            if (Boolean.TRUE.equals(filter.apply(r))) {
                builder.add(r);
            }
        }
        return builder.build();
    }

}

package com.msavitskiy.rwep;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toMap;

/**
 * The Producer of randomized weighted elements.
 *
 * @param <E> Type of elements.
 */
public class Producer<E> {
    private final Map<E, Double> source;

    /**
     * Initialize  {@link com.msavitskiy.rwep.Producer} with source.
     * After initialization changes in original source not affect the producer.
     *
     * @param source source with weighted elements.
     * @throws NullPointerException in case when source is null.
     */
    public Producer(Map<E, Double> source) {
        Objects.requireNonNull(source, "source is null");
        this.source = Collections.unmodifiableMap(source);
    }

    /**
     * Shortcut for {@link #produce(int)}.
     *
     * @return Random element.
     * @see #produce(int)
     */
    public E produce() {
        return produce(1).stream().findFirst().orElse(null);
    }

    /**
     * Shortcut for {@link #produce(int, boolean)}.
     *
     * @param count Size of result collection.
     * @return Collection of random elements. Duplicates can be present.
     * @see #produce(int, boolean)
     */
    public Collection<E> produce(int count) {
        return produce(count, false);
    }

    /**
     * Shortcut for {@link #produce(Map, int, boolean)} .
     *
     * @param count  Size of result collection.
     * @param unique Flag to prevent duplication of elements in result collection.
     * @return Collection of random elements.
     * @see #produce(Map, int, boolean)
     */

    public Collection<E> produce(int count, boolean unique) {
        return produce(this.source, count, unique);
    }

    /**
     * Static shortcut for {@link #produce(Map, int, boolean)} .
     *
     * @param source Source of elements.
     * @param count  Size of result collection.
     * @param <E>    Type of elements.
     * @return Collection of random elements. Duplicates can be present.
     * @see #produce(Map, int, boolean)
     */
    public static <E> Collection<E> produce(Map<E, Double> source, int count) {
        return produce(source, count, false);
    }

    /**
     * Provide collection of random elements.
     *
     * @param source Source of elements.
     * @param count  Size of result collection.
     * @param unique Flag to prevent duplication of elements in result collection.
     * @param <E>    Type of elements.
     * @return Collection of random elements.
     * @throws NullPointerException     in case if source is null.
     * @throws IllegalArgumentException in case if count is less then one.
     * @see #uniquePick(Map, int)
     * @see #simplePick(Map, int)
     */
    public static <E> Collection<E> produce(Map<E, Double> source, int count, boolean unique) {
        Objects.requireNonNull(source, "source is null");
        if (0 >= count) throw new IllegalArgumentException("count must be positive");
        return unique ? uniquePick(source, count) : simplePick(source, count);
    }

    /**
     * Provide random element form source.
     *
     * @param source Source of elements.
     * @param <E>    Type of element.
     * @return Random element. In case when source is empty return null.
     * @throws NullPointerException in case when got null as source
     */
    public static <E> E produce(Map<E, Double> source) {
        Objects.requireNonNull(source, "source is null");
        double rnd = Math.random() * source.values().stream().mapToDouble(e -> e).sum();
        double tmp = 0D;
        for (Map.Entry<E, Double> entry : source.entrySet()) {
            if ((tmp += entry.getValue()) >= rnd) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static <E> Collection<E> uniquePick(Map<E, Double> source, int count) {
        final Collection<E> res = new HashSet<>();
        Map<E, Double> tmpSource = source.entrySet().stream().collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        while (res.size() != count && tmpSource.size() != 0) {
            E e = pick(res, tmpSource);
            tmpSource.remove(e);
        }
        return res;
    }

    private static <E> Collection<E> simplePick(Map<E, Double> source, int count) {
        final Collection<E> res = new ArrayList<>();
        while (res.size() != count && source.size() != 0) {
            pick(res, source);
        }
        return res;
    }

    private static <E> E pick(Collection<E> res, Map<E, Double> source) {
        E e = produce(source);
        res.add(e);
        return e;
    }
}

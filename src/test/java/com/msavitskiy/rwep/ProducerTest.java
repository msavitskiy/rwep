package com.msavitskiy.rwep;

import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ProducerTest {

    @Test
    public void returnResultStatic() {
        HashMap<String, Double> source = new HashMap<>();
        String key = UUID.randomUUID().toString();
        source.put(key, Math.random());
        String res = Producer.produce(source);
        assertEquals(key, res);
    }

    @Test
    public void returnResult() {
        HashMap<String, Double> source = new HashMap<>();
        String key = UUID.randomUUID().toString();
        source.put(key, Math.random());
        String res = new Producer<>(source).produce();
        assertEquals(key, res);
    }

    @Test
    public void returnUniqueResults() {
        Map<Double, Double> source = generateSource(35);
        Producer<Double> wrp = new Producer<>(source);
        Collection<Double> pick = wrp.produce(ThreadLocalRandom.current().nextInt(1, source.size()), true);
        assertEquals(new TreeSet<>(pick).size(), pick.size());
    }

    @Test
    public void returnProperResults() {
        Map<Double, Double> source = generateSource(55);
        HashMap<Double, AtomicInteger> results = new HashMap<>();
        for (int i = 0; i < 3_000_000; i++) {
            Producer<Double> wrp = new Producer<>(source);
            Collection<Double> pick = wrp.produce(ThreadLocalRandom.current().nextInt(1, source.size()));
            pick.forEach(e -> results.computeIfAbsent(e, k -> new AtomicInteger()).incrementAndGet());
        }
        double sum = results.values().stream().mapToDouble(AtomicInteger::doubleValue).sum();
        results.forEach((key, value) -> assertEquals("bad results" + results, key, value.doubleValue() / (sum / 100), 0.05));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenGotNull() {
        new Producer<>(null);
    }

    @Test
    public void returnNullIfEmpty() {
        assertNull(new Producer<>(Collections.emptyMap()).produce());
    }

    @Test
    public void returnEmptyIfEmpty() {
        assertTrue(new Producer<>(Collections.emptyMap()).produce(1).isEmpty());
        assertTrue(new Producer<>(Collections.emptyMap()).produce(1, false).isEmpty());
        assertTrue(new Producer<>(Collections.emptyMap()).produce(1, true).isEmpty());
    }

    private Map<Double, Double> generateSource(int bound) {
        Map<Double, Double> source = new HashMap<>();
        while (source.values().stream().mapToDouble(e -> e).sum() < 100) {
            double value = ThreadLocalRandom.current().nextInt(1, bound);
            double sum = source.values().stream().mapToDouble(e -> e).sum();
            if (sum + value > 100) {
                double v = 100 - sum;
                source.put(v, v);
            } else {
                source.put(value, value);
            }
        }
        return source;
    }

}
package ru.spbau.lazy;

import org.junit.jupiter.api.Test;
import ru.spbau.lazy.util.ConstructorCounter;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LazyTest {
    @Test
    void testSingleThreadedLazy() {
        Supplier<ConstructorCounter> supplier = ConstructorCounter::new;

        Lazy<ConstructorCounter> lazy = LazyFactory.createSingleThreadLazy(supplier);

        assertEquals(ConstructorCounter.INITIAL_OBJ_NUMBER, lazy.get().getCurObjNumber());
        assertEquals(ConstructorCounter.INITIAL_OBJ_NUMBER, lazy.get().getCurObjNumber());
        assertEquals(ConstructorCounter.INITIAL_OBJ_NUMBER, lazy.get().getCurObjNumber());
    }

    @Test
    void testMultiThreadedLazy() {
        testLazyThreadedImpl(LazyFactory.createMultiThreadLazy(ConstructorCounter::new));
    }

    @Test
    void testLockfreeLazy() {
        testLazyThreadedImpl(LazyFactory.createLockFreeLazy(ConstructorCounter::new));
    }

    private void testLazyThreadedImpl(Lazy<ConstructorCounter> lazy) {
        final int NUM_THREADS = 1_000;

        CyclicBarrier barrier = new CyclicBarrier(NUM_THREADS);
        Thread[] threads = new Thread[NUM_THREADS];

        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i] = new Thread(() -> {
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                assertEquals(ConstructorCounter.INITIAL_OBJ_NUMBER, lazy.get().getCurObjNumber());
            });
        }

        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i].start();
        }
    }
}

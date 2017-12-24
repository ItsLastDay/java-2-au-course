package ru.au.java2;

import org.junit.jupiter.api.Test;
import ru.au.java2.util.RunnableWithNumber;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LockFreeListImplTest {

    @Test
    void testAppend() {
        LockFreeList<String> lst = new LockFreeListImpl<>();

        lst.append("1");
        lst.append("2");
        lst.append("3");

        assertFalse(lst.isEmpty());
        assertTrue(lst.contains("1"));
        assertTrue(lst.contains("2"));
        assertTrue(lst.contains("3"));
    }

    @Test
    void testRemove() {
        LockFreeList<String> lst = new LockFreeListImpl<>();

        lst.append("qwe");
        lst.append("wert");
        assertTrue(lst.remove("qwe"));
        assertFalse(lst.remove("nonexistant"));

        assertFalse(lst.contains("qwe"));
        assertTrue(lst.contains("wert"));
    }

    @Test
    void testContains() {
        LockFreeList<Integer> lst = new LockFreeListImpl<>();
        assertFalse(lst.contains(123));
        lst.append(123);
        assertTrue(lst.contains(123));
    }

    @Test
    void testIsEmpty() {
        LockFreeList<Integer> lst = new LockFreeListImpl<>();
        assertTrue(lst.isEmpty());

        lst.append(5);
        assertFalse(lst.isEmpty());
    }


    @Test
    void testSimultaneousAddThenDelete() throws InterruptedException {
        final int numThreads = 300;

        final LockFreeList<Integer> lst = new LockFreeListImpl<>();
        final CyclicBarrier barrier = new CyclicBarrier(numThreads);

        final Thread workers[] = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            workers[i] = new Thread(new RunnableWithNumber(i) {
                public void run() {
                    try {
                        barrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }

                    lst.append(runnableNumber);
                }
            });
            workers[i].start();
        }

        for (int i = 0; i < numThreads; i++) {
            workers[i].join();
        }

        assertFalse(lst.isEmpty());
        for (int i = 0; i < numThreads; i++) {
            assertTrue(lst.contains(i));
        }

        barrier.reset();


        for (int i = 0; i < numThreads; i++) {
            workers[i] = new Thread(new RunnableWithNumber(i) {
                public void run() {
                    try {
                        barrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }

                    assertTrue(lst.remove(runnableNumber));
                }
            });

            workers[i].start();
        }

        for (int i = 0; i < numThreads; i++) {
            workers[i].join();
        }

        assertTrue(lst.isEmpty());
        for (int i = 0; i < numThreads; i++) {
            assertFalse(lst.contains(i));
        }
    }

    private void appendAndThenDeleteSeveralThreads(boolean intersectedElems) throws InterruptedException {
        final int numThreads = 20;

        final LockFreeList<Integer> lst = new LockFreeListImpl<>();
        final CyclicBarrier barrier = new CyclicBarrier(numThreads);

        final Thread workers[] = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            workers[i] = new Thread(new RunnableWithNumber(i) {
                public void run() {
                    try {
                        barrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }

                    int start = 100 * runnableNumber;
                    int end = 100 * (runnableNumber + 1);

                    if (intersectedElems) {
                        start = 0;
                        end = 100;
                    }

                    for (int i = start; i < end; i++) {
                        lst.append(i);
                        assertTrue(lst.remove(i));
                    }
                }
            });

            workers[i].start();
        }

        for (int i = 0; i < numThreads; i++) {
            workers[i].join();
        }

        assertTrue(lst.isEmpty());
    }

    @Test
    void testConcurrentAppendDeleteSeparate() throws InterruptedException {
        appendAndThenDeleteSeveralThreads(false);
    }

    @Test
    void testConcurrentAppendDeleteIntersected() throws InterruptedException {
        appendAndThenDeleteSeveralThreads(true);
    }
}

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
        LockFreeList<String> lst = new LockFreeListImpl<String>();

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
        LockFreeList<String> lst = new LockFreeListImpl<String>();

        lst.append("qwe");
        lst.append("wert");
        assertTrue(lst.remove("qwe"));
        assertFalse(lst.remove("nonexistant"));

        assertFalse(lst.contains("qwe"));
        assertTrue(lst.contains("wert"));
    }

    @Test
    void testContains() {
        LockFreeList<Integer> lst = new LockFreeListImpl<Integer>();
        assertFalse(lst.contains(123));
        lst.append(123);
        assertTrue(lst.contains(123));
    }

    @Test
    void testIsEmpty() {
        LockFreeList<Integer> lst = new LockFreeListImpl<Integer>();
        assertTrue(lst.isEmpty());

        lst.append(5);
        assertFalse(lst.isEmpty());
    }


    @Test
    void testSimultaneousAddThenDelete() throws InterruptedException {
        final int numThreads = 300;

        final LockFreeList<Integer> lst = new LockFreeListImpl<Integer>();
        final CyclicBarrier barrier = new CyclicBarrier(numThreads);

        final Thread workers[] = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            workers[i] = new Thread(new RunnableWithNumber(i) {
                public void run() {
                    try {
                        barrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
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
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
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

        System.out.println(((LockFreeListImpl)lst).debugSingleThreadSnapshot());
        assertTrue(lst.isEmpty());
        for (int i = 0; i < numThreads; i++) {
            assertFalse(lst.contains(i));
        }
    }

    @Test
    void testConcurrentAppendDeleteGoodNumberOfElems() throws InterruptedException {
        final int numThreads = 20;

        final LockFreeList<Integer> lst = new LockFreeListImpl<Integer>();
        final CyclicBarrier barrier = new CyclicBarrier(numThreads);

        final Thread workers[] = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            workers[i] = new Thread(new RunnableWithNumber(i) {
                public void run() {
                    try {
                        barrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }

                    for (int i = 100 * runnableNumber; i < 100 * (runnableNumber + 1); i++) {
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
}

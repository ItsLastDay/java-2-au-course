package ru.au;

import org.junit.Test;
import ru.spbau.LightFuture;
import ru.spbau.ThreadPoolImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static ru.au.TestUtils.longTask;

public class ThreadPoolImplTest {
    @Test
    public void testNotLessThanNThreads() {
        final int numThreads = 10;
        ThreadPoolImpl pool = new ThreadPoolImpl(numThreads);

        List<LightFuture<Integer>> results = new ArrayList<>();

        TestUtils.assertTakesHalfSleepDurationToSleepDurAndHalf(x -> {
            for (int i = 0; i < numThreads; i++) {
                results.add(pool.send(TestUtils.longTask));
            }

            for (int i = 0; i < numThreads; i++) {
                results.get(i).get();
            }
        });

    }

    @Test
    public void testPoolTaskQueue() {
        final int numTasks = 5;
        ThreadPoolImpl pool = new ThreadPoolImpl(1);

        List<LightFuture<Integer>> tasks = new ArrayList<>();
        for (int i = 0; i < numTasks; i++) {
            tasks.add(pool.send(TestUtils.longTask));
        }

        for (int i = 0; i < numTasks; i++) {
            tasks.get(i).get();
            for (int j = i + 1; j < numTasks; j++) {
                assertFalse(tasks.get(j).isReady());
            }
        }
    }

    @Test
    public void testShutdownUnfinishedTasks() throws InterruptedException {
        ThreadPoolImpl pool = new ThreadPoolImpl(1);

        LightFuture<Integer> future = pool.send(TestUtils.longTask);

        pool.shutdown();
        Thread.sleep(TestUtils.sleepDuration * 2);
        assertFalse(future.isReady());
    }

    @Test
    public void testStartAndShutdown() {
        ThreadPoolImpl pool = new ThreadPoolImpl(3);
        pool.shutdown();
    }

    @Test
    public void testThenApplyDoesNotBlockWork() throws InterruptedException {
        ThreadPoolImpl pool = new ThreadPoolImpl(2);

        LightFuture<Integer> future = pool.send(longTask);
        // This task should not block the next one.
        future.thenApply(x -> x * 2);
        LightFuture<Integer> futureNotDelayed = pool.send(() -> 5);

        // Sleep for little time, so that:
        //   `future` is not done yet
        //   `futureNotDelayed` had a chance to complete
        Thread.sleep(20);
        assertTrue(futureNotDelayed.isReady());
        assertFalse(future.isReady());
    }
}

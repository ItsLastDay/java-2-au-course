package ru.au;

import org.junit.Test;
import ru.spbau.LightExecutionException;
import ru.spbau.LightFuture;
import ru.spbau.ThreadPoolImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static ru.au.TestUtils.assertTakesHalfSleepDurationToSleepDurAndHalf;
import static ru.au.TestUtils.longTask;

public class LightFutureTest {
    @Test
    public void testThenApplyWorks() {
        ThreadPoolImpl pool = new ThreadPoolImpl(1);

        LightFuture<Integer> fut1 = pool.send(() -> 1);
        LightFuture<Boolean> futConverted = fut1.thenApply(x -> x % 2 == 0);

        assertEquals(false, futConverted.get());
    }

    @Test
    public void testThenApplyTaskOrder() {
        // Two threads in pool, but "thenApply"-ed task is started only after
        // the original task end.
        ThreadPoolImpl pool = new ThreadPoolImpl(2);

        LightFuture<Integer> fut1 = pool.send(longTask);
        LightFuture<Boolean> futConverted = fut1.thenApply(x -> x % 2 == 0);

        futConverted.get();
        assertTrue(fut1.isReady());
    }

    @Test
    public void testThenApplySeveralTimes() {
        ThreadPoolImpl pool = new ThreadPoolImpl(4);

        LightFuture<Integer> fut1 = pool.send(longTask);
        LightFuture<Integer> futId1 = fut1.thenApply(x -> longTask.get());
        LightFuture<Integer> futId2 = fut1.thenApply(x -> longTask.get());
        LightFuture<Integer> futId3 = fut1.thenApply(x -> longTask.get());

        fut1.get();


        assertTakesHalfSleepDurationToSleepDurAndHalf(x -> {
            assertFalse(futId1.isReady());
            assertFalse(futId2.isReady());
            assertFalse(futId3.isReady());

            futId1.get();
            futId2.get();
            futId3.get();
        });
    }


    @Test
    public void testGetWaitsForCompletion() {
        ThreadPoolImpl pool = new ThreadPoolImpl(1);

        LightFuture<Integer> future = pool.send(longTask);
        assertFalse(future.isReady());
        future.get();
        assertTrue(future.isReady());
    }


    @Test(expected = LightExecutionException.class)
    public void testFutureException() {
        ThreadPoolImpl pool = new ThreadPoolImpl(1);

        LightFuture<String> futureThrower = pool.send(() -> { throw new RuntimeException(); });
        futureThrower.get();
    }

}

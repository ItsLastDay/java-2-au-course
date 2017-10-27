package ru.au;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.Assert.assertTrue;

public class TestUtils {
    static final long sleepDuration = TimeUnit.SECONDS.toMillis(1);
    static final Supplier<Integer> longTask = () -> {
        try {
            Thread.sleep(sleepDuration);
        } catch (InterruptedException e) {
        }
        return 1;
    };

    static void assertTakesHalfSleepDurationToSleepDurAndHalf(Consumer<Integer> cons) {
        final long timeStart = System.currentTimeMillis();

        cons.accept(1);

        final long timeEnd = System.currentTimeMillis();
        final long timeDelta = timeEnd - timeStart;
        assertTrue(timeDelta >= 0.5 * sleepDuration && timeDelta <= 1.5 * sleepDuration);
    }
}

package ru.spbau;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadPoolImpl {
    private int num_threads;
    private Thread[] workers;
    private final Queue<LightFutureImpl<?>> taskQueue = new ArrayDeque<>();

    private void initializeWorkers() {
        workers = new Thread[num_threads];
        for (int i = 0; i < num_threads; i++) {
            workers[i] = new Thread(() -> {
                while (!Thread.interrupted()) {
                    LightFutureImpl<?> task;
                    synchronized (taskQueue) {
                        while (taskQueue.isEmpty()) {
                            try {
                                taskQueue.wait();
                            } catch (InterruptedException e) {
                                return;
                            }
                        }
                        task = taskQueue.poll();
                    }
                    task.setResult();
                }
            });
        }

        for (int i = 0; i < num_threads; i++) {
            workers[i].start();
        }
    }

    public ThreadPoolImpl(int n) {
        num_threads = n;
        initializeWorkers();
    }

    public <T> LightFuture<T> send(Supplier<T> supplier) {
        LightFutureImpl<T> pendingTask = new LightFutureImpl<>(supplier);

        synchronized (taskQueue) {
            taskQueue.add(pendingTask);
            taskQueue.notify();
        }

        return pendingTask;
    }

    public synchronized void shutdown() {
        for (Thread t: workers) {
            t.interrupt();
        }
    }


    private class LightFutureImpl<T> implements LightFuture<T> {
        volatile T result;
        volatile boolean isReady;
        volatile boolean hadException;
        Supplier<T> supplier;

        LightFutureImpl(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        private synchronized void setResult() {
            try {
                result = supplier.get();
            } catch (Exception ex) {
                hadException = true;
            }
            isReady = true;
            this.notifyAll();
        }

        @Override
        public boolean isReady() {
            return isReady;
        }

        @Override
        public synchronized T get() {
            if (!isReady()) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                }
            }

            if (hadException) {
                throw new LightExecutionException();
            }

            return result;
        }

        @Override
        public <R> LightFuture<R> thenApply(Function<T, R> f) {
            return ThreadPoolImpl.this.send(() -> {
                synchronized (LightFutureImpl.this) {
                    while (!isReady()) try {
                        LightFutureImpl.this.wait();
                    } catch (InterruptedException e) {
                    }
                }
                return f.apply(LightFutureImpl.this.get());
            });
        }
    }
}

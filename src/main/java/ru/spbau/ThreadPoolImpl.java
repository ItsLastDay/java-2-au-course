package ru.spbau;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadPoolImpl {
    private final int numThreads;
    private final Thread[] workers;
    private final Queue<LightFuture<?>> taskQueue = new ArrayDeque<>();

    private void initializeWorkers() {
        for (int i = 0; i < numThreads; i++) {
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
                        task = (LightFutureImpl<?>) taskQueue.poll();
                    }
                    task.calcAndStoreResult();
                }
            });
        }

        for (int i = 0; i < numThreads; i++) {
            workers[i].start();
        }
    }

    public ThreadPoolImpl(int n) {
        numThreads = n;
        workers = new Thread[numThreads];
        initializeWorkers();
    }

    private <T> void addFuture(LightFuture<T> future) {
        synchronized (taskQueue) {
            taskQueue.add(future);
            taskQueue.notify();
        }
    }

    public <T> LightFuture<T> send(Supplier<T> supplier) {
        LightFuture<T> pendingTask = new LightFutureImpl<>(supplier);
        addFuture(pendingTask);
        return pendingTask;
    }

    public synchronized void shutdown() {
        for (Thread t: workers) {
            t.interrupt();
        }
    }

    private class LightFutureImpl<T> implements LightFuture<T> {
        private volatile T result;
        private volatile boolean isReady;
        private volatile boolean hadException;
        private Throwable excThrown = null;
        private Supplier<T> supplier;

        private final List<LightFuture<?>> thenTasks = new ArrayList<>();

        private <R> LightFuture<R> sendToPool(Supplier<R> supplier) {
            return ThreadPoolImpl.this.send(supplier);
        }

        LightFutureImpl(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        private synchronized void calcAndStoreResult() {
            try {
                result = supplier.get();
            } catch (Exception ex) {
                hadException = true;
                excThrown = ex;
            }
            isReady = true;
            synchronized (thenTasks) {
                thenTasks.forEach(ThreadPoolImpl.this::addFuture);
            }
            notifyAll();
        }

        @Override
        public boolean isReady() {
            return isReady;
        }

        @Override
        public synchronized T get() {
            try {
                while (!isReady()) {
                    this.wait();
                }
            } catch (InterruptedException e) {
                throw new LightExecutionException(e);
            }

            if (hadException) {
                throw new LightExecutionException(excThrown);
            }

            return result;
        }

        @Override
        public <R> LightFuture<R> thenApply(Function<? super T, R> f) {
            Supplier<R> supplier = () -> f.apply(LightFutureImpl.this.get());
            if (isReady()) {
                return sendToPool(supplier);
            }

            synchronized (thenTasks) {
                if (!isReady()) {
                    LightFuture<R> future = new LightFutureImpl<>(supplier);
                    thenTasks.add(future);
                    return future;
                }
                
                return sendToPool(supplier);
            }
        }
    }
}

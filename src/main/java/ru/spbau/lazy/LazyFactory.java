package ru.spbau.lazy;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class LazyFactory {
    public static <T> Lazy<T> createSingleThreadLazy(Supplier<T> supplier) {
        return new Lazy<T>() {
            private T result = null;
            private boolean wasComputed = false;

            @Override
            public T get() {
                if (!wasComputed) {
                    result = supplier.get();
                    wasComputed = true;
                }

                return result;
            }
        };
    }


    public static <T> Lazy<T> createMultiThreadLazy(Supplier<T> supplier) {
        return new Lazy<T>() {
            private volatile T result = null;
            private volatile boolean wasComputed = false;

            @Override
            public T get() {
                if (!wasComputed) {
                    synchronized(this) {
                        if (!wasComputed) {
                            result = supplier.get();
                            wasComputed = true;
                        }
                    }
                }

                return result;
            }
        };
    }


    public static <T> Lazy<T> createLockFreeLazy(Supplier<T> supplier) {
        return new Lazy<T>() {
            private AtomicReference<T> result = new AtomicReference<>(null);
            @Override
            public T get() {
                if (result.get() == null) {
                    result.compareAndSet(null, supplier.get());
                }

                return result.get();
            }
        };
    }


}

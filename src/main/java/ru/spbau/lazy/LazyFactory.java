package ru.spbau.lazy;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class LazyFactory {
    public static <T> Lazy<T> createSingleThreadLazy(Supplier<T> supplier) {
        return new Lazy<T>() {
            private T result = null;

            @Override
            public T get() {
                if (result == null) {
                    result = supplier.get();
                }

                return result;
            }
        };
    }


    public static <T> Lazy<T> createMultiThreadLazy(Supplier<T> supplier) {
        return new Lazy<T>() {
            private volatile T result = null;

            @Override
            public T get() {
                if (result == null) {
                    synchronized(this) {
                        if (result == null) {
                            result = supplier.get();
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

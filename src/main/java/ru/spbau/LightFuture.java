package ru.spbau;

import java.util.function.Function;

public interface LightFuture <T> {
    boolean isReady();
    T get();
    <R> LightFuture<R> thenApply(Function<? super T, R> f);
}

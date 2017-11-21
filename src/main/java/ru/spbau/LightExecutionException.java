package ru.spbau;

public class LightExecutionException extends RuntimeException {
    LightExecutionException(Throwable thr) {
        super(thr);
    }
}

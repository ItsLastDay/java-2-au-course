package ru.spbau.mit.library;

import ru.spbau.mit.util.Util;

public class UtilsFactory {
    public static Object createUtil() {
        return new Util();
    }

    public static Util createTypedUtil() {
        return new Util();
    }

    static Object createPackagePrivateUtl() {
        return new Util();
    }
}

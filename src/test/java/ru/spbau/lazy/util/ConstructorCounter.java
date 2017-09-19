package ru.spbau.lazy.util;

public class ConstructorCounter {
    public static final int INITIAL_OBJ_NUMBER = 0;

    private static int numConstructions = INITIAL_OBJ_NUMBER;
    private int curObjNumber;

    public ConstructorCounter() {
        curObjNumber = numConstructions;
        ++numConstructions;
    }

    public int getCurObjNumber() {
        return curObjNumber;
    }
}

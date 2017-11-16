package ru.au.ftp;

// Enum code from
// https://codingexplained.com/coding/java/enum-to-integer-and-integer-to-enum

import java.util.HashMap;
import java.util.Map;

public enum Commands {
    LIST(1),
    GET(2);

    private int value;

    private static Map<Integer, Commands> intToCommand = new HashMap<>();

    Commands(int value) {
        this.value = value;
    }

    static {
        for (Commands command: Commands.values()) {
            intToCommand.put(command.value, command);
        }
    }

    public static Commands valueOf(int pageType) {
        return intToCommand.get(pageType);
    }

    public int getValue() {
        return value;
    }
}

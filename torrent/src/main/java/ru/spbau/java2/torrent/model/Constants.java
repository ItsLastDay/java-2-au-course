package ru.spbau.java2.torrent.model;

import java.util.Arrays;

public class Constants {
    public static final int SERVER_PORT = 8081;
    public static final int FILE_PART_SIZE_BYTES = 10_000;
    public static final int UPDATE_TIMEOUT_SEC = 5 * 60;

    public static byte[] normalizeBytesToFilepart(byte[] content) {
        if (content.length < FILE_PART_SIZE_BYTES) {
            content = Arrays.copyOf(content, FILE_PART_SIZE_BYTES);
        }
        return content;
    }
}

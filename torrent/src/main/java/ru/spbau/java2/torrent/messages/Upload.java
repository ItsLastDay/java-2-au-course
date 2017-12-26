package ru.spbau.java2.torrent.messages;

public class Upload implements Message {
    private final String name;
    private final long size;

    public Upload(String name, long size) {
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }
}

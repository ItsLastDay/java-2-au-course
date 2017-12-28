package ru.spbau.java2.torrent.messages;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Upload upload = (Upload) o;
        return getSize() == upload.getSize() &&
                Objects.equals(getName(), upload.getName());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getName(), getSize());
    }
}

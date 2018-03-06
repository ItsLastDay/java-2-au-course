package ru.spbau.java2.torrent.messages;

import ru.spbau.java2.torrent.model.FileId;

import java.util.Objects;

public class Stat implements Message {
    private final FileId id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stat stat = (Stat) o;
        return Objects.equals(getId(), stat.getId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId());
    }

    public Stat(FileId id) {
        this.id = id;
    }

    public FileId getId() {
        return id;
    }
}

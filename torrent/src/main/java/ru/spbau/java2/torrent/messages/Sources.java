package ru.spbau.java2.torrent.messages;

import ru.spbau.java2.torrent.model.FileId;

import java.util.Objects;

public class Sources implements Message {
    private final FileId id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sources sources = (Sources) o;
        return Objects.equals(getId(), sources.getId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId());
    }

    public Sources(FileId id) {
        this.id = id;
    }

    public FileId getId() {
        return id;
    }
}

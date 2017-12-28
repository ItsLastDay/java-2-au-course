package ru.spbau.java2.torrent.messages;

import ru.spbau.java2.torrent.model.FileId;

import java.util.Objects;

public class UploadAnswer implements Message {
    private final FileId id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UploadAnswer that = (UploadAnswer) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId());
    }

    public UploadAnswer(FileId id) {
        this.id = id;
    }

    public FileId getId() {
        return id;
    }
}

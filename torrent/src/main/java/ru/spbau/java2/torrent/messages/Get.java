package ru.spbau.java2.torrent.messages;

import ru.spbau.java2.torrent.model.FileId;
import ru.spbau.java2.torrent.model.PartId;

import java.util.Objects;

public class Get implements Message {
    private final FileId id;
    private final PartId partIndex;

    public Get(FileId id, PartId partIndex) {
        this.id = id;
        this.partIndex = partIndex;
    }

    public FileId getId() {
        return id;
    }

    public PartId getPartIndex() {
        return partIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Get get = (Get) o;
        return Objects.equals(getId(), get.getId()) &&
                Objects.equals(getPartIndex(), get.getPartIndex());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getPartIndex());
    }
}

package ru.spbau.java2.torrent.messages;

import ru.spbau.java2.torrent.model.FileId;

public class Get implements Message {
    private final FileId id;
    private final int partIndex;

    public Get(FileId id, int partIndex) {
        this.id = id;
        this.partIndex = partIndex;
    }

    public FileId getId() {
        return id;
    }

    public int getPartIndex() {
        return partIndex;
    }
}

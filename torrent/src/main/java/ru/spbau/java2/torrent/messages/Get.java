package ru.spbau.java2.torrent.messages;

import ru.spbau.java2.torrent.model.FileId;
import ru.spbau.java2.torrent.model.PartId;

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
}

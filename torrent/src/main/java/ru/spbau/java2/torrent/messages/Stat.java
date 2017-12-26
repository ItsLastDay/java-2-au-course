package ru.spbau.java2.torrent.messages;

import ru.spbau.java2.torrent.model.FileId;

public class Stat implements Message {
    private final FileId id;

    public Stat(FileId id) {
        this.id = id;
    }

    public FileId getId() {
        return id;
    }
}

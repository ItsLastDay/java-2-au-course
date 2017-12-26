package ru.spbau.java2.torrent.messages;

import ru.spbau.java2.torrent.model.FileId;

public class UploadAnswer implements Message {
    private final FileId id;

    public UploadAnswer(FileId id) {
        this.id = id;
    }

    public FileId getId() {
        return id;
    }
}

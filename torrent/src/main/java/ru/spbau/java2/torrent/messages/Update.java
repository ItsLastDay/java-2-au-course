package ru.spbau.java2.torrent.messages;

import ru.spbau.java2.torrent.model.FileId;

import java.util.Collection;

public class Update implements Message {
    private final short port;
    private final Collection<FileId> fileIds;

    public Update(short port, Collection<FileId> fileIds) {
        this.port = port;
        this.fileIds = fileIds;
    }

    public short getPort() {
        return port;
    }

    public Collection<FileId> getFileIds() {
        return fileIds;
    }
}

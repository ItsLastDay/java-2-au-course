package ru.spbau.java2.torrent.messages;

import ru.spbau.java2.torrent.model.FileId;

import java.util.Collection;
import java.util.Objects;

public class Update implements Message {
    private final short port;
    private final Collection<FileId> fileIds;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Update update = (Update) o;
        return getPort() == update.getPort() &&
                Objects.equals(getFileIds(), update.getFileIds());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getPort(), getFileIds());
    }

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

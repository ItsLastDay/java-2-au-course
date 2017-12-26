package ru.spbau.java2.torrent.messages;

import ru.spbau.java2.torrent.model.FileDescriptor;

import java.util.Collection;

public class ListAnswer implements Message {
    private final Collection<FileDescriptor> files;

    public ListAnswer(Collection<FileDescriptor> files) {
        this.files = files;
    }

    public Collection<FileDescriptor> getFiles() {
        return files;
    }
}

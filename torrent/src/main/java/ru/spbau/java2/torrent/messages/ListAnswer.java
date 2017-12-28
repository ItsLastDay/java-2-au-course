package ru.spbau.java2.torrent.messages;

import ru.spbau.java2.torrent.model.FileDescriptor;

import java.util.Collection;
import java.util.Objects;

public class ListAnswer implements Message {
    private final Collection<FileDescriptor> files;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListAnswer that = (ListAnswer) o;
        return Objects.equals(getFiles(), that.getFiles());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getFiles());
    }

    public ListAnswer(Collection<FileDescriptor> files) {
        this.files = files;
    }

    public Collection<FileDescriptor> getFiles() {
        return files;
    }
}

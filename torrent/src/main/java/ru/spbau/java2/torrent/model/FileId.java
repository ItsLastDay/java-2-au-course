package ru.spbau.java2.torrent.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public class FileId {
    private final int id;

    public int getId() {
        return id;
    }

    public FileId(int id) {
        this.id = id;
    }

    public void writeTo(DataOutputStream out) throws IOException {
        out.writeInt(id);
    }

    public static FileId fromStream(DataInputStream in) throws IOException {
        int id = in.readInt();
        return new FileId(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileId fileId = (FileId) o;
        return getId() == fileId.getId();
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId());
    }
}

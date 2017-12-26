package ru.spbau.java2.torrent.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PartId {
    // Delegate everything to FileId, since they are same.
    private final FileId id;

    public PartId(int id) {
        this.id = new FileId(id);
    }

    public int getId() {
        return id.getId();
    }

    public void writeTo(DataOutputStream out) throws IOException {
        id.writeTo(out);
    }

    static PartId fromStream(DataInputStream in) throws IOException {
        return new PartId(FileId.fromStream(in).getId());
    }
}

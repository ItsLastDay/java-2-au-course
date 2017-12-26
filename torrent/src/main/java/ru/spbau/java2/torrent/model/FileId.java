package ru.spbau.java2.torrent.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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

    static FileId fromStream(DataInputStream in) throws IOException {
        int id = in.readInt();
        return new FileId(id);
    }
}

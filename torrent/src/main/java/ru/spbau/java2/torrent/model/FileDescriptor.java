package ru.spbau.java2.torrent.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FileDescriptor {
    private FileId id;
    private String name;
    private long size;

    public FileDescriptor(FileId id, String name, long size) {
        this.id = id;
        this.name = name;
        this.size = size;
    }

    public FileId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public void writeTo(DataOutputStream  out) throws IOException {
        id.writeTo(out);
        out.writeUTF(name);
        out.writeLong(size);
    }

    public static FileDescriptor fromStream(DataInputStream in) throws IOException {
        FileId fileId = FileId.fromStream(in);
        String s = in.readUTF();
        long sz = in.readLong();
        return new FileDescriptor(fileId, s, sz);
    }
}

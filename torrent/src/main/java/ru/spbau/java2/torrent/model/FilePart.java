package ru.spbau.java2.torrent.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePart {
    private final FileId id;
    private final PartId partIdx;
    private final Path backingFilePath;

    public Path getBackingFilePath() {
        return backingFilePath;
    }


    public FilePart(FileId id, PartId partIdx,
                    Path backingFilePath) {
        this.id = id;
        this.partIdx = partIdx;
        this.backingFilePath = backingFilePath;
    }

    public PartId getPartIdx() {
        return partIdx;
    }

    public FileId getId() {
        return id;
    }

    public void writeTo(DataOutputStream out) throws IOException {
        id.writeTo(out);
        partIdx.writeTo(out);
        out.writeUTF(backingFilePath.toString());
    }

    public static FilePart fromStream(DataInputStream in) throws IOException {
        FileId fileId = FileId.fromStream(in);
        PartId partId = PartId.fromStream(in);
        String path = in.readUTF();
        return new FilePart(fileId, partId, Paths.get(path));
    }
}

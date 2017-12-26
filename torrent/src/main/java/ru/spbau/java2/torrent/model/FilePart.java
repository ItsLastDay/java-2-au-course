package ru.spbau.java2.torrent.model;

public class FilePart {
    private final byte[] content;
    private final FileId id;
    private final PartId partIdx;

    public FilePart(byte[] content, FileId id, PartId partIdx) {
        this.content = Constants.normalize(content);
        this.id = id;
        this.partIdx = partIdx;
    }

    public PartId getPartIdx() {
        return partIdx;
    }

    public FileId getId() {
        return id;
    }

    public byte[] getContent() {
        return content;
    }
}

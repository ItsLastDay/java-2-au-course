package ru.spbau.java2.torrent.files;

import ru.spbau.java2.torrent.model.ClientDescriptor;
import ru.spbau.java2.torrent.model.FileId;

public interface FileDownloader {
    void fetchPart(FileId id, ClientDescriptor client, int blockIdx);
}

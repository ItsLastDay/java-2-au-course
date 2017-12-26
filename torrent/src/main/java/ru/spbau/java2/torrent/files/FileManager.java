package ru.spbau.java2.torrent.files;

import ru.spbau.java2.torrent.model.FileId;
import ru.spbau.java2.torrent.model.FilePart;

import java.io.File;

public interface FileManager {
    void savePartOfFile(FileId id, int idx);
    FilePart getFile(FileId id);
    FilePart getFilePart(FileId id, int idx);
    FileId fileToId(File f);
}

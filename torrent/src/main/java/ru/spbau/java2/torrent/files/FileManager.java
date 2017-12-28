package ru.spbau.java2.torrent.files;

import ru.spbau.java2.torrent.model.Constants;
import ru.spbau.java2.torrent.model.FileId;
import ru.spbau.java2.torrent.model.FilePart;
import ru.spbau.java2.torrent.model.PartId;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileManager {
    public List<FilePart> pathToParts(Path path, long size, FileId id) {
        long needBlocks = size / Constants.FILE_PART_SIZE_BYTES;
        List<FilePart> ret = new ArrayList<>();

        for (int i = 0; i < needBlocks; i++) {
            ret.add(new FilePart(id, new PartId(i), path));
        }

        return ret;
    }

    public byte[] getContent(FilePart filePart) throws IOException {
        Path backingFilePath = filePart.getBackingFilePath();

        long sz = backingFilePath.toFile().length();
        try (
                RandomAccessFile file = new RandomAccessFile(backingFilePath.toString(), "r");
                ) {
            long startOffset = filePart.getPartIdx().getId() * Constants.FILE_PART_SIZE_BYTES;
            file.seek(startOffset);

            long endOffset = startOffset + Constants.FILE_PART_SIZE_BYTES;
            if (endOffset > sz) {
                endOffset = sz;
            }

            byte[] content = new byte[(int) (endOffset - startOffset)]; // no more than BLOCK_SIZE
            file.readFully(content);

            return content;
        }
    }

    public Path getBackingFile(FileId id) {
        return Paths.get(String.format("File_%d", id.getId()));
    }

    public void writeContent(FilePart part, long fileSize, byte[] content) {
        long startOffset = part.getPartIdx().getId() * Constants.FILE_PART_SIZE_BYTES;
        long endOffset = startOffset + Constants.FILE_PART_SIZE_BYTES;
        if (endOffset >= fileSize)
            endOffset = fileSize;

        try (
                RandomAccessFile file = new RandomAccessFile(part.getBackingFilePath().toString(), "w")
        ) {
            content = Arrays.copyOf(content, (int) (endOffset - startOffset));
            file.seek(startOffset);
            file.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

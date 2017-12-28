package ru.spbau.java2.torrent.state;

import ru.spbau.java2.torrent.exceptions.ProtocolViolation;
import ru.spbau.java2.torrent.files.FileManager;
import ru.spbau.java2.torrent.messages.ListAnswer;
import ru.spbau.java2.torrent.model.FileDescriptor;
import ru.spbau.java2.torrent.model.FileId;
import ru.spbau.java2.torrent.model.FilePart;
import ru.spbau.java2.torrent.model.PartId;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientState implements AbstractState {
    private final Map<FileId, List<FilePart>> fileToParts = new ConcurrentHashMap<>();
    private final FileManager fileManager = new FileManager();
    private final Set<FileDescriptor> descriptors = ConcurrentHashMap.newKeySet();

    public Map<FileId, List<FilePart>> getFileToParts() {
        return fileToParts;
    }

    public ClientState() {
    }

    public ClientState(Map<FileId, List<FilePart>> map) {
        map.forEach(fileToParts::put);
    }


    public void registerNewFile(Path path, long size, FileId id) {
        List<FilePart> parts = fileManager.pathToParts(path, size, id);
        fileToParts.put(id, parts);
    }

    private FilePart findExistingFilePart(FileId id, PartId partId) {
        return fileToParts.get(id).stream().filter(x -> x.getPartIdx().equals(partId))
                .findFirst().orElse(null);
    }

    private FileDescriptor findFileDescriptor(FileId id) {
        return descriptors.stream()
                .filter(x -> x.getId().equals(id))
                .findFirst()
                .orElseThrow(ProtocolViolation::new);
    }

    private FilePart findPossiblyNonexistantFilePart(FileId id, PartId partId) {
        FilePart part = findExistingFilePart(id, partId);
        if (part != null)
            return part;
        Path path = descriptors.stream()
                .filter(x -> x.getId().equals(id))
                .map(x -> String.format("%d_%s", id.getId(), x.getName()))
                .map(s -> Paths.get(s))
                .findFirst().orElse(fileManager.getBackingFile(id));
        return new FilePart(id, partId, path);
    }

    public byte[] getContent(FileId id, PartId partIndex) throws IOException {
        return fileManager.getContent(findExistingFilePart(id, partIndex));
    }

    public void writePart(FileId fileId, PartId partId, byte[] content) {
        FilePart part = findPossiblyNonexistantFilePart(fileId, partId);
        fileManager.writeContent(part, findFileDescriptor(fileId).getSize(), content);
    }

    public void updateListing(ListAnswer listAnswer) {
        descriptors.clear();
        descriptors.addAll(listAnswer.getFiles());
    }
}

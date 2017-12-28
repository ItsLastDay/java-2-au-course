package ru.spbau.java2.torrent.state;

import ru.spbau.java2.torrent.main_entities.ServerWorker;
import ru.spbau.java2.torrent.messages.Update;
import ru.spbau.java2.torrent.model.ClientDescriptor;
import ru.spbau.java2.torrent.model.Constants;
import ru.spbau.java2.torrent.model.FileDescriptor;
import ru.spbau.java2.torrent.model.FileId;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServerStateImpl implements ServerState {
    private final Set<FileDescriptor> allFiles = ConcurrentHashMap.newKeySet();

    public int getFreeId() {
        return freeId;
    }

    private int freeId = 0;

    private final Map<ClientDescriptor, Long> clientToLastTimeUpdated = new ConcurrentHashMap<>();
    private final Map<ClientDescriptor, Set<FileId>> clientToFiles = new ConcurrentHashMap<>();
    private final Map<ClientDescriptor, ServerWorker> clientToWorker = new ConcurrentHashMap<>();

    public ServerStateImpl(int freeId, Set<FileDescriptor> allFiles) {
        this.allFiles.addAll(allFiles);
        this.freeId = freeId;
    }

    public ServerStateImpl() {
    }

    @Override
    public Collection<FileDescriptor> getAllFiles() {
        return allFiles;
    }

    @Override
    public Map<ClientDescriptor, Set<FileId>> getClientToFiles() {
        return clientToFiles;
    }

    private void dropInactiveClients() {
        final long curTime = System.currentTimeMillis();
        clientToLastTimeUpdated.values().removeIf(tm -> tm < curTime - Constants.UPDATE_TIMEOUT_SEC * 1000);
        clientToFiles.keySet().removeIf(desc -> !clientToLastTimeUpdated.containsKey(desc));

        clientToWorker.forEach((key, value) -> {
            if (!clientToLastTimeUpdated.containsKey(key)) {
                value.stopWorker();
            }
        });
        clientToWorker.keySet().removeIf(client -> !clientToLastTimeUpdated.containsKey(client));
    }

    @Override
    public boolean performUpdate(Update msg, ClientDescriptor client) {
        boolean statusOk = true; // When is it false?

        clientToFiles.put(client, new HashSet<>());
        clientToFiles.get(client).addAll(msg.getFileIds());

        if (statusOk) {
            clientToLastTimeUpdated.put(client, System.currentTimeMillis());
        }

        dropInactiveClients();

        return statusOk;
    }

    @Override
    public void registerWorker(ClientDescriptor client, ServerWorker serverWorker) {
        clientToWorker.put(client, serverWorker);
    }

    @Override
    public synchronized FileId uploadFile(ClientDescriptor client, String name, long size) {
        // Assume that each file is new and added only once.
        FileId fileId = new FileId(freeId++);
        FileDescriptor fileDescriptor = new FileDescriptor(fileId, name, size);
        allFiles.add(fileDescriptor);

        clientToFiles.get(client).add(fileId);

        return fileId;
    }
}

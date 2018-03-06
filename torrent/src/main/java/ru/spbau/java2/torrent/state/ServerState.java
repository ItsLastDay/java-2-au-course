package ru.spbau.java2.torrent.state;

import ru.spbau.java2.torrent.main_entities.ServerWorker;
import ru.spbau.java2.torrent.messages.Update;
import ru.spbau.java2.torrent.model.ClientDescriptor;
import ru.spbau.java2.torrent.model.FileDescriptor;
import ru.spbau.java2.torrent.model.FileId;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface ServerState extends AbstractState {
    Collection<FileDescriptor> getAllFiles();
    public Map<ClientDescriptor, Integer> getClientToListenerPort();

    Map<ClientDescriptor, Set<FileId>> getClientToFiles();

    boolean performUpdate(Update msg, ClientDescriptor client);

    void registerWorker(ClientDescriptor client, ServerWorker serverWorker);

    FileId uploadFile(ClientDescriptor client, String name, long size);
}

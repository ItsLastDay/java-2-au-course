package ru.spbau.java2.torrent.messages;

import ru.spbau.java2.torrent.model.ClientDescriptor;

import java.util.Collection;

public class SourcesAnswer implements Message {
    private final Collection<ClientDescriptor> clients;

    public SourcesAnswer(Collection<ClientDescriptor> clients) {
        this.clients = clients;
    }

    public Collection<ClientDescriptor> getClients() {
        return clients;
    }
}

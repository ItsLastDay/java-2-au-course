package ru.spbau.java2.torrent.messages;

import ru.spbau.java2.torrent.model.ClientDescriptor;

import java.util.Collection;
import java.util.Objects;

public class SourcesAnswer implements Message {
    private final Collection<ClientDescriptor> clients;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SourcesAnswer that = (SourcesAnswer) o;
        return Objects.equals(getClients(), that.getClients());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getClients());
    }

    public SourcesAnswer(Collection<ClientDescriptor> clients) {
        this.clients = clients;
    }

    public Collection<ClientDescriptor> getClients() {
        return clients;
    }
}

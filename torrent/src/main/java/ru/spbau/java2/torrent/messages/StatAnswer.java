package ru.spbau.java2.torrent.messages;

import ru.spbau.java2.torrent.model.PartId;

import java.util.Collection;

public class StatAnswer implements Message {
    private final Collection<PartId> partIds;

    public StatAnswer(Collection<PartId> partIds) {
        this.partIds = partIds;
    }

    public Collection<PartId> getPartIds() {
        return partIds;
    }
}

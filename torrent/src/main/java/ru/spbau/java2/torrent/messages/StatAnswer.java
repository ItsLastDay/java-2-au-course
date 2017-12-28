package ru.spbau.java2.torrent.messages;

import ru.spbau.java2.torrent.model.PartId;

import java.util.Collection;
import java.util.Objects;

public class StatAnswer implements Message {
    private final Collection<PartId> partIds;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatAnswer that = (StatAnswer) o;
        return Objects.equals(getPartIds(), that.getPartIds());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getPartIds());
    }

    public StatAnswer(Collection<PartId> partIds) {
        this.partIds = partIds;
    }

    public Collection<PartId> getPartIds() {
        return partIds;
    }
}

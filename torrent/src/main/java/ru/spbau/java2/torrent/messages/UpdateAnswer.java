package ru.spbau.java2.torrent.messages;

import java.util.Objects;

public class UpdateAnswer implements Message {
    private final boolean ok;

    public UpdateAnswer(boolean status) {
        this.ok = status;
    }

    public boolean isOk() {
        return ok;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateAnswer that = (UpdateAnswer) o;
        return isOk() == that.isOk();
    }

    @Override
    public int hashCode() {

        return Objects.hash(isOk());
    }
}

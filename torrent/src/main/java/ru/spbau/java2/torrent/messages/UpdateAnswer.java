package ru.spbau.java2.torrent.messages;

public class UpdateAnswer implements Message {
    private final boolean ok;

    public UpdateAnswer(boolean status) {
        this.ok = status;
    }

    public boolean isOk() {
        return ok;
    }
}

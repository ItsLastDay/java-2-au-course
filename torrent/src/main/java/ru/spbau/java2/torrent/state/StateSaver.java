package ru.spbau.java2.torrent.state;

public interface StateSaver {
    void saveState();
    void recoverState();
}

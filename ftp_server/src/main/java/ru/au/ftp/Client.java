package ru.au.ftp;

import javafx.util.Pair;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.List;

public interface Client {
    void connect(InetAddress addr, int port) throws IOException;
    void disconnect() throws IOException;

    List<Pair<String, Boolean>> executeList(Path path) throws IOException;
    byte[] executeGet(Path path) throws IOException;
}

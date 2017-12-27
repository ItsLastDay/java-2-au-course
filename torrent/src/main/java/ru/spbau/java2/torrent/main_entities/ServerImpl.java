package ru.spbau.java2.torrent.main_entities;

import ru.spbau.java2.torrent.messages.ListAnswer;
import ru.spbau.java2.torrent.messages.SourcesAnswer;
import ru.spbau.java2.torrent.messages.UpdateAnswer;
import ru.spbau.java2.torrent.messages.UploadAnswer;
import ru.spbau.java2.torrent.model.Constants;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerImpl {
    private final Thread serverListenerThread;

    ServerImpl() throws IOException {
        ServerSocket serverSocket = new ServerSocket(Constants.SERVER_PORT);
        serverListenerThread = new Thread(new ServerNetworkListener(serverSocket));
    }

    public void startServer() {
        serverListenerThread.start();
    }

    public void stopServer() {
        serverListenerThread.interrupt();
    }

}

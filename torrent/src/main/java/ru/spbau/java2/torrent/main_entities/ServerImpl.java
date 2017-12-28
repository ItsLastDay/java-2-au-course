package ru.spbau.java2.torrent.main_entities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.java2.torrent.model.Constants;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerImpl {
    private final static Logger logger = LogManager.getLogger(ServerImpl.class);
    private final Thread serverListenerThread;

    public ServerImpl() throws IOException {
        ServerSocket serverSocket = new ServerSocket(Constants.SERVER_PORT);
        serverListenerThread = new Thread(new ServerNetworkListener(serverSocket));
    }

    public void startServer() {
        serverListenerThread.start();
    }

    public void stopServer() throws InterruptedException {
        logger.info("Server is shutting down");
        serverListenerThread.interrupt();
        serverListenerThread.join();
    }

}

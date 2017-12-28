package ru.spbau.java2.torrent.main_entities;

import ru.spbau.java2.torrent.state.ClientState;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientNetworkListener implements Runnable {
    private final ServerSocket clientListenerSocket;
    private final ClientState state;
    private ExecutorService clientToClient = Executors.newCachedThreadPool();


    public ClientNetworkListener(ClientState state, ServerSocket clientListenerSocket) {
        this.clientListenerSocket = clientListenerSocket;
        this.state = state;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Socket client = clientListenerSocket.accept();
                clientToClient.submit(new ClientWorker(state, client));
            } catch (IOException e) {
                System.out.println("Error processing client");
                e.printStackTrace();
            }
        }
    }
}

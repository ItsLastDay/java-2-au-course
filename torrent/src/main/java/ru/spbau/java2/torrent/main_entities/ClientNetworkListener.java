package ru.spbau.java2.torrent.main_entities;

import ru.spbau.java2.torrent.model.Constants;
import ru.spbau.java2.torrent.state.ClientState;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientNetworkListener implements Runnable {
    private final ServerSocket clientListenerSocket;
    private final ClientState state;
    private ExecutorService clientToClient = Executors.newCachedThreadPool();


    public ClientNetworkListener(ClientState state, ServerSocket clientListenerSocket) throws SocketException {
        clientListenerSocket.setSoTimeout(Constants.SOCKET_TIMEOUT_MILI);
        this.clientListenerSocket = clientListenerSocket;
        this.state = state;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Socket client = clientListenerSocket.accept();
                clientToClient.submit(new ClientWorker(state, client));
            } catch (InterruptedIOException e) {
                // Do nothing, timeout occurs to check "interrupted" flag.
            } catch (IOException e) {
                System.out.println("Error processing client");
                e.printStackTrace();
            }
        }

        clientToClient.shutdown();
    }
}

package ru.spbau.java2.torrent.main_entities;

import ru.spbau.java2.torrent.exceptions.ProtocolViolation;
import ru.spbau.java2.torrent.state.ServerState;
import ru.spbau.java2.torrent.state.ServerStateSaver;
import ru.spbau.java2.torrent.state.StateSaver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerNetworkListener implements Runnable {
    private final ExecutorService workers = Executors.newCachedThreadPool();
    private final ServerSocket serverSocket;
    private final StateSaver saver = new ServerStateSaver();
    private final ServerState state = (ServerState) saver.recoverState();

    ServerNetworkListener(ServerSocket sock) {
        serverSocket = sock;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Socket client = serverSocket.accept();
                workers.submit(new ServerWorker(client, state));
            } catch (IOException e) {
                System.out.println("Client failed to connect or start");
                e.printStackTrace();
            } catch (ProtocolViolation e) {
                System.out.println("Client violated protocol");
                e.printStackTrace();
            }
        }

        saver.saveState(state);
    }
}

package ru.spbau.java2.torrent.main_entities;

import ru.spbau.java2.torrent.messages.Message;
import ru.spbau.java2.torrent.model.ClientDescriptor;
import ru.spbau.java2.torrent.model.WireFormat;
import ru.spbau.java2.torrent.state.ServerState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerWorker implements Runnable {
    private final ClientDescriptor client;
    private final ServerState state;
    private final WireFormat wireFormatter;
    private final ServerQueryExecutor executor;
    private volatile boolean working = true;

    public ServerWorker(Socket client, ServerState state) throws IOException {
        this.client = new ClientDescriptor(client.getInetAddress(),
                (short) client.getPort());
        this.state = state;

        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        DataInputStream in = new DataInputStream(client.getInputStream());

        wireFormatter = new WireFormat(out, in);
        executor = new ServerQueryExecutor(this.client, state, wireFormatter);
        state.registerWorker(this.client, this);
    }

    public void stopWorker() {
        working = false;
    }

    @Override
    public void run() {
        while (working) {
            try {
                Message msg = wireFormatter.serverReadSomeMessage();
                Message response = executor.executeQuery(msg);
                wireFormatter.serverWriteSomeMessage(response);
            } catch (IOException e) {
                e.printStackTrace();
                working = false;
            }
        }
    }
}

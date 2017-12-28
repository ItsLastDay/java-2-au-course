package ru.spbau.java2.torrent.main_entities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.java2.torrent.messages.*;
import ru.spbau.java2.torrent.model.ClientDescriptor;
import ru.spbau.java2.torrent.model.Constants;
import ru.spbau.java2.torrent.model.FileId;
import ru.spbau.java2.torrent.model.PartId;
import ru.spbau.java2.torrent.state.ClientState;
import ru.spbau.java2.torrent.state.ClientStateSaver;
import ru.spbau.java2.torrent.state.StateSaver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;


public class ClientImpl {
    private final static Logger logger = LogManager.getLogger(ClientImpl.class);

    private final StateSaver saver = new ClientStateSaver();
    private final ClientState state = (ClientState) saver.recoverState();

    private Thread clientListener;
    private final Thread updater = new Thread(new Updater());

    private ClientServerInteractor clientServerInteractor;
    private ClientNetworkSender clientNetworkSender;

    private class Updater implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    ClientImpl.this.executeUpdate();
                    Thread.sleep(Constants.UPDATE_TIMEOUT_SEC * 500 / 2);
                } catch (InterruptedException e) {
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public UpdateAnswer executeUpdate() throws IOException {
        return clientServerInteractor.executeUpdate();
    }

    public ListAnswer executeList() throws IOException {
        return clientServerInteractor.executeList();
    }

    public SourcesAnswer executeSources(FileId id) throws IOException {
        return clientServerInteractor.executeSources(id);
    }

    public UploadAnswer executeUpload(String path, long size) throws IOException {
        return clientServerInteractor.executeUpload(path, size);
    }

    public StatAnswer executeStat(ClientDescriptor client,
                                  FileId fileId) throws ExecutionException, InterruptedException {
        return clientNetworkSender.executeStat(client, fileId);
    }

    public void executeGet(ClientDescriptor client,
                                FileId fileId, PartId partId) {
        clientNetworkSender.executeGet(client, fileId, partId);
    }

    public void connectToServer(InetAddress addr) throws IOException {
        Socket socketToServer = new Socket(addr, Constants.SERVER_PORT);
        ServerSocket clientListenerSocket = new ServerSocket(0);

        logger.info(String.format("Connection from client to server established, port %d", socketToServer.getPort()));
        logger.info(String.format("Client: accepting other clients on port %d", clientListenerSocket.getLocalPort()));

        clientNetworkSender = new ClientNetworkSender(state);
        clientListener = new Thread(new ClientNetworkListener(state, clientListenerSocket));
        clientServerInteractor = new ClientServerInteractor(socketToServer, state,
                clientListenerSocket.getLocalPort());

        updater.start();
        clientListener.start();
    }

    public void stopClient() throws InterruptedException {
        logger.info("Stopping client");
        clientListener.interrupt();
        clientListener.join();
        updater.interrupt();
        updater.join();

        saver.saveState(state);
    }
}

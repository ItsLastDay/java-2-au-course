package ru.spbau.java2.torrent.main_entities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.java2.torrent.exceptions.ProtocolViolation;
import ru.spbau.java2.torrent.model.Constants;
import ru.spbau.java2.torrent.state.ServerState;
import ru.spbau.java2.torrent.state.ServerStateSaver;
import ru.spbau.java2.torrent.state.StateSaver;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerNetworkListener implements Runnable {
    private final static Logger logger = LogManager.getLogger(ServerNetworkListener.class);

    private final ExecutorService workers = Executors.newCachedThreadPool();
    private final ServerSocket serverSocket;
    private final StateSaver saver = new ServerStateSaver();
    private final ServerState state = (ServerState) saver.recoverState();

    ServerNetworkListener(ServerSocket sock) throws SocketException {
        sock.setSoTimeout(Constants.SOCKET_TIMEOUT_MILI);
        serverSocket = sock;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Socket client = serverSocket.accept();
                workers.submit(new ServerWorker(client, state));
            } catch (InterruptedIOException e) {
                // Do nothing, timeout occurs to check "interrupted" flag.
            } catch (IOException e) {
                logger.error("Client failed to connect or start");
                e.printStackTrace();
            } catch (ProtocolViolation e) {
                logger.error("Client violated protocol");
                e.printStackTrace();
            }
        }

        workers.shutdown();;
        saver.saveState(state);
    }
}

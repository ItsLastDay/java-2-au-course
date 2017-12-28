package ru.spbau.java2.torrent.main_entities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.java2.torrent.messages.Get;
import ru.spbau.java2.torrent.messages.Stat;
import ru.spbau.java2.torrent.messages.StatAnswer;
import ru.spbau.java2.torrent.model.ClientDescriptor;
import ru.spbau.java2.torrent.model.FileId;
import ru.spbau.java2.torrent.model.PartId;
import ru.spbau.java2.torrent.model.WireFormat;
import ru.spbau.java2.torrent.state.ClientState;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientNetworkSender {
    public static Logger logger = LogManager.getLogger(ClientNetworkSender.class);

    private final ClientState state;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public ClientNetworkSender(ClientState state) {
        this.state = state;
    }

    public void executeGet(ClientDescriptor client, FileId fileId, PartId partId) {
        CompletableFuture.supplyAsync(() -> {
            try {
                Socket socket = new Socket(client.getAddress(), client.getPort());
                WireFormat wireFormat = new WireFormat(socket);
                Get msg = new Get(fileId, partId);
                wireFormat.serializeGet(msg);
                return wireFormat.deserializeGetAnswer();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }, executorService)
        .thenAccept(answer -> {
            logger.info(String.format("Part %d of file %d downloaded!", partId.getId(), fileId.getId()));
            state.writePart(fileId, partId, answer.getContent());
        });
    }

    public StatAnswer executeStat(ClientDescriptor client, FileId fileId) throws ExecutionException, InterruptedException {
        return executorService.submit(() -> {
            try {
                Socket socket = new Socket(client.getAddress(), client.getPort());
                WireFormat wireFormat = new WireFormat(socket);
                Stat msg = new Stat(fileId);
                wireFormat.serializeStat(msg);
                return wireFormat.deserializeStatAnswer();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }).get();
    }
}

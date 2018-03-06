package ru.spbau.java2.torrent.main_entities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.java2.torrent.messages.Get;
import ru.spbau.java2.torrent.messages.Stat;
import ru.spbau.java2.torrent.messages.StatAnswer;
import ru.spbau.java2.torrent.model.*;
import ru.spbau.java2.torrent.state.ClientState;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientNetworkSender {
    private static Logger logger = LogManager.getLogger(ClientNetworkSender.class);

    private final ClientState state;
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    public ClientNetworkSender(ClientState state) {
        this.state = state;
    }

    private Socket connectToClient(ClientDescriptor client) throws IOException {
        logger.info(String.format("Client trying to connect to other client with ip %s, port %d",
                client.getAddress(), client.getPort()));
        return new Socket(client.getAddress(), client.getPort());
    }

    public void executeGet(ClientDescriptor client, FileId fileId, PartId partId) {
        List<FilePart> fileParts = state.getFileToParts().get(fileId);
        if (fileParts != null) {
            if (fileParts.stream().filter(x -> x.getPartIdx().equals(partId))
                    .findFirst().orElse(null) != null) {
                // Part already stored.
                return;
            }
        }
        CompletableFuture.supplyAsync(() -> {
            try {
                Socket socket = connectToClient(client);
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
            state.registerFilePart(fileId, partId);
        });
    }

    public StatAnswer executeStat(ClientDescriptor client, FileId fileId) throws ExecutionException, InterruptedException {
        return executorService.submit(() -> {
            try {
                Socket socket = connectToClient(client);
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

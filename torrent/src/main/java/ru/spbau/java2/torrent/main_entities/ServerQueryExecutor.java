package ru.spbau.java2.torrent.main_entities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.java2.torrent.exceptions.ProtocolViolation;
import ru.spbau.java2.torrent.messages.*;
import ru.spbau.java2.torrent.model.ClientDescriptor;
import ru.spbau.java2.torrent.state.ServerState;

import java.util.Map;
import java.util.stream.Collectors;

public class ServerQueryExecutor {
    private static final Logger logger = LogManager.getLogger(ServerQueryExecutor.class);

    private final ClientDescriptor client;
    private final ServerState state;

    public ServerQueryExecutor(ClientDescriptor client, ServerState state) {
        this.client = client;
        this.state = state;
    }

    public Message executeQuery(Message msg) {
        if (List.class.isInstance(msg))
            return executeList((List) msg);
        if (Upload.class.isInstance(msg))
            return executeUpload((Upload) msg);
        if (Sources.class.isInstance(msg))
            return executeSources((Sources) msg);
        if (Update.class.isInstance(msg))
            return executeUpdate((Update) msg);

        logger.error("Unknown class " + msg.getClass().toString());
        throw new ProtocolViolation();
    }

    public Message executeList(List msg) {
        return new ListAnswer(state.getAllFiles());
    }

    public Message executeUpload(Upload msg) {
        return new UploadAnswer(state.uploadFile(client,
                msg.getName(),
                msg.getSize()));
    }

    public Message executeSources(Sources msg) {
        java.util.List<ClientDescriptor> clientDescrs = state.getClientToFiles().entrySet()
                .stream()
                .filter(entry -> entry.getValue().contains(msg.getId()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        clientDescrs = clientDescrs.stream()
                .map(desc -> new ClientDescriptor(desc.getAddress(),
                        state.getClientToListenerPort().get(desc)))
                .collect(Collectors.toList());
        return new SourcesAnswer(clientDescrs);
    }

    public synchronized Message executeUpdate(Update msg) {
        logger.debug("Update received by server from client that accepts connections to port " + msg.getPort());
        return new UpdateAnswer(state.performUpdate(msg, client));
    }
}

package ru.spbau.java2.torrent.main_entities;

import ru.spbau.java2.torrent.exceptions.ProtocolViolation;
import ru.spbau.java2.torrent.messages.*;
import ru.spbau.java2.torrent.model.FilePart;
import ru.spbau.java2.torrent.model.WireFormat;
import ru.spbau.java2.torrent.state.ClientState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.stream.Collectors;

public class ClientWorker implements Runnable {
    private final ClientState state;
    private final WireFormat wireFormatter;

    public ClientWorker(ClientState state, Socket client) throws IOException {
        wireFormatter = new WireFormat(client);
        this.state = state;
    }



    /**
     * Perform exactly one response here.
     */
    @Override
    public void run() {
        try {
            Message message = wireFormatter.clientReadSomeMessage();
            if (Stat.class.isInstance(message))
                wireFormatter.serializeStatAnswer(executeStat((Stat) message));
            if (Get.class.isInstance(message))
                wireFormatter.serializeGetAnswer(executeGet((Get) message));

            throw new ProtocolViolation();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private GetAnswer executeGet(Get message) throws IOException {
        byte[] content = state.getContent(message.getId(), message.getPartIndex());
        return new GetAnswer(content);
    }

    private StatAnswer executeStat(Stat message) {
        return new StatAnswer(state.getFileToParts()
                .get(message.getId())
                .stream()
                .map(FilePart::getPartIdx)
                .collect(Collectors.toList()));
    }
}

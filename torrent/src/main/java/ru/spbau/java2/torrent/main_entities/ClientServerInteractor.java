package ru.spbau.java2.torrent.main_entities;

import ru.spbau.java2.torrent.messages.*;
import ru.spbau.java2.torrent.model.FileId;
import ru.spbau.java2.torrent.model.WireFormat;
import ru.spbau.java2.torrent.state.ClientState;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientServerInteractor {
    private final Socket socketToServer;
    private final WireFormat wireFormatter;
    private final ClientState state;

    ClientServerInteractor(Socket socketToServer, ClientState state) throws IOException {
        this.socketToServer = socketToServer;
        this.state = state;

        wireFormatter = new WireFormat(socketToServer);
    }

    public synchronized UpdateAnswer executeUpdate() throws IOException {
        Update msg = new Update((short) socketToServer.getPort(),
                state.getFileToParts().keySet());
        wireFormatter.serializeUpdate(msg);
        return wireFormatter.deserializeUpdateAnswer();
    }

    public synchronized ListAnswer executeList() throws IOException {
        wireFormatter.serializeList(new List());
        ListAnswer listAnswer = wireFormatter.deserializeListAnswer();
        state.updateListing(listAnswer);
        return listAnswer;
    }

    public synchronized SourcesAnswer executeSources(FileId id) throws IOException {
        wireFormatter.serializeSources(new Sources(id));
        return wireFormatter.deserializeSourcesAnswer();
    }

    public synchronized UploadAnswer executeUpload(String path_, long size) throws IOException {
        Path path = Paths.get(path_);
        path = path.normalize();
        Path fileName = path.getName(path.getNameCount() - 1);

        Upload msg = new Upload(fileName.toString(), size);
        wireFormatter.serializeUpload(msg);
        UploadAnswer uploadAnswer = wireFormatter.deserializeUploadAnswer();

        state.registerNewFile(path, size, uploadAnswer.getId());

        return uploadAnswer;
    }
}

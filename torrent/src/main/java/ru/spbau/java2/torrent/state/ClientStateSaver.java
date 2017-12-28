package ru.spbau.java2.torrent.state;

import ru.spbau.java2.torrent.model.FileId;
import ru.spbau.java2.torrent.model.FilePart;

import java.io.*;
import java.util.*;

public class ClientStateSaver implements StateSaver {
    private static final String CLIENT_STATE_FILE_NAME = "client.state";

    @Override
    public void saveState(AbstractState state_) {
        if (!ClientState.class.isInstance(state_))
            throw new IllegalArgumentException();
        ClientState state = (ClientState) state_;


        try (
                FileOutputStream fileOutputStream = new FileOutputStream(CLIENT_STATE_FILE_NAME);
                DataOutputStream out = new DataOutputStream(fileOutputStream);
        ) {
            out.writeInt(state.getFileToParts().size());
            for (Map.Entry<FileId, List<FilePart>> entry : state.getFileToParts().entrySet()) {
                entry.getKey().writeTo(out);
                out.writeInt(entry.getValue().size());
                for (FilePart filePart : entry.getValue()) {
                    filePart.writeTo(out);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not save client state!");
            e.printStackTrace();
        }
    }

    @Override
    public AbstractState recoverState() {
        try (
                FileInputStream fileInputStream = new FileInputStream(CLIENT_STATE_FILE_NAME);
                DataInputStream in = new DataInputStream(fileInputStream);
        ) {
            int size = in.readInt();
            Map<FileId, List<FilePart>> map = new HashMap<>();
            for (int i = 0; i < size; i++) {
                FileId fileId = FileId.fromStream(in);
                int lstSize = in.readInt();
                List<FilePart> lst = new ArrayList<>();
                for (int j = 0; j < lstSize; j++) {
                    lst.add(FilePart.fromStream(in));
                }

                map.put(fileId, lst);
            }

            return new ClientState(map);
        } catch (IOException e) {
            System.out.println("Recovered empty client state");
            return new ClientState();
        }

    }
}

package ru.spbau.java2.torrent.state;

import ru.spbau.java2.torrent.model.FileDescriptor;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class ServerStateSaver implements StateSaver {
    private static final String SERVER_STATE_FILE_NAME = "server.state";

    @Override
    public void saveState(AbstractState state_) {
        if (!ServerStateImpl.class.isInstance(state_))
            throw new IllegalArgumentException();
        ServerState state = (ServerState) state_;


        try (
                FileOutputStream fileOutputStream = new FileOutputStream(SERVER_STATE_FILE_NAME);
                DataOutputStream out = new DataOutputStream(fileOutputStream)
                ) {
            out.writeInt(state.getAllFiles().size());
            for (FileDescriptor fileDescriptor : state.getAllFiles()) {
                fileDescriptor.writeTo(out);
            }

            // Not sure this is meaningful to be saved.
            /*out.writeInt(state.getClientToFiles().size());
            for (Map.Entry<ClientDescriptor, Set<FileId>> entry: state.getClientToFiles().entrySet()) {
                entry.getKey().writeTo(out);
                out.writeInt(entry.getValue().size());
                for (FileId fileId : entry.getValue()) {
                    fileId.writeTo(out);
                }
            }*/
        } catch (IOException e) {
            System.out.println("Could not save server state!");
            e.printStackTrace();
        }
    }

    @Override
    public AbstractState recoverState() {
        try (
                FileInputStream fileInputStream = new FileInputStream(SERVER_STATE_FILE_NAME);
                DataInputStream in = new DataInputStream(fileInputStream)
        ) {
            int size = in.readInt();
            Set<FileDescriptor> allFiles = new HashSet<>();
            for (int i = 0; i < size; i++) {
                allFiles.add(FileDescriptor.fromStream(in));
            }
            return new ServerStateImpl(allFiles);
        } catch (IOException e) {
            System.out.println("Recovered empty server state");
            return new ServerStateImpl();
        }

    }
}

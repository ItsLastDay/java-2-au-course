package ru.au.ftp;

import javafx.util.Pair;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ClientImpl implements Client {
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    @Override
    public void connect(InetAddress addr, int port) throws IOException {
        socket = new Socket(addr, port);
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void disconnect() throws IOException {
        socket.close();
    }

    @Override
    public List<Pair<String, Boolean>> executeList(Path path) throws IOException {
        outputStream.writeInt(Commands.LIST.getValue());
        outputStream.writeUTF(path.toString());

        List<Pair<String, Boolean>> result = new ArrayList<>();
        int size = inputStream.readInt();
        for (int i = 0; i < size; i++) {
            String name = inputStream.readUTF();
            Boolean isDir = inputStream.readBoolean();
            result.add(new Pair<>(name, isDir));
        }

        return result;
    }

    @Override
    public byte[] executeGet(Path path) throws IOException {
        outputStream.writeInt(Commands.GET.getValue());
        outputStream.writeUTF(path.toString());

        int size = ((int) inputStream.readLong());
        byte[] content = new byte[size];
        inputStream.readFully(content);

        return content;
    }
}

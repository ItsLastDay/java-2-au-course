package ru.au.ftp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServerImpl implements Server {
    private void serverEventLoop(Socket clientSocket) {
        try (
            DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream())
        ) {
            while (!clientSocket.isClosed()) {
                Commands command = Commands.valueOf(inputStream.readInt());
                Path path;

                switch (command) {
                    case LIST:
                        path = Paths.get(inputStream.readUTF());
                        List<Path> children = new ArrayList<>();

                        if (Files.isDirectory(path)) {
                            children = Files.list(path).collect(Collectors.toList());
                        }

                        // Response format:
                        // <size: Int> (<name: String> <is_dir: Boolean>)*
                        outputStream.writeInt(children.size());
                        for (Path child: children) {
                            outputStream.writeUTF(child.toString());
                            outputStream.writeBoolean(Files.isDirectory(child));
                        }

                        break;

                    case GET:
                        path = Paths.get(inputStream.readUTF());

                        byte[] content = new byte[0];
                        if (Files.isRegularFile(path)) {
                            content = Files.readAllBytes(path);
                        }

                        // Response format:
                        // <size: Long> <content: Bytes>
                        outputStream.writeLong(content.length);
                        outputStream.write(content);

                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("I/O error in server worker thread.");
            e.printStackTrace();
        } catch (Throwable e) {
            System.out.println("Server worker shut down unexpectedly");
            e.printStackTrace();
        }
    }

    @Override
    public void start(int portNumber) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.printf("Server started at port %d, addr %s",
                    serverSocket.getLocalPort(),
                    serverSocket.getInetAddress());
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> serverEventLoop(clientSocket)).start();
            }
        }
    }

    @Override
    public void start() throws IOException {
        // Passing `0` as port number makes ServerSocket
        // chose the port for us.
        // NOTE: if `start(int)` uses something other than ServerSocket,
        //       this method should be revised.
        start(0);
    }
}

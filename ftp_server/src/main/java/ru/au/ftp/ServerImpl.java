package ru.au.ftp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
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
    private int port;
    private volatile boolean portReady = false;
    private Thread serverThread = null;

    private void serverEventLoop(Socket clientSocket) {
        try (
            DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream())
        ) {
            while (!clientSocket.isClosed()) {
                Commands command;
                try {
                    command = Commands.valueOf(inputStream.readInt());
                } catch (EOFException e) {
                    break;
                }

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
    public int getPort() {
        while (!portReady)
            Thread.yield();
        return port;
    }

    private void setPort(int portNumber) {
        port = portNumber;
        portReady = true;
    }

    @Override
    public void start(int portNumber) throws IOException {
        serverThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
                setPort(serverSocket.getLocalPort());

                System.out.printf("Server started at port %d, addr %s\n",
                        port,
                        serverSocket.getInetAddress());
                while (!Thread.currentThread().isInterrupted()) {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(() -> serverEventLoop(clientSocket)).start();
                }
            } catch (IOException e) {
                System.out.println("I/O error in main server thread:\n");
                e.printStackTrace();
            }
        });
        serverThread.start();
    }

    @Override
    public void start() throws IOException {
        // Passing `0` as port number makes ServerSocket
        // chose the port for us.
        // NOTE: if `start(int)` uses something other than ServerSocket,
        //       this method should be revised.
        start(0);
    }

    @Override
    public void shutdown() {
        serverThread.interrupt();
    }
}

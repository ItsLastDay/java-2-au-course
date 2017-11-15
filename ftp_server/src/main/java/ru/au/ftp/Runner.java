package ru.au.ftp;

import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.nio.file.Paths;
import java.util.Objects;

public class Runner {
    public static void main(String[] args) throws IOException {
        if (Objects.equals(args[1], "server")) {
            Server server = new ServerImpl();
            server.start();
        } else {
            Console c = System.console();

            Client client = new ClientImpl();
            client.connect(InetAddress.getLocalHost(), Integer.valueOf(args[2]));
            while (true) {
                int cmd = Integer.valueOf(c.readLine("Enter command (1 - list, 2 - get, 3 - exit): "));
                String path = c.readLine("Enter path: ");
                if (cmd == 1) {
                    System.out.println(client.executeList(Paths.get(path)).size());
                } else if (cmd == 2) {
                    System.out.println(client.executeGet(Paths.get(path)).length);
                } else {
                    client.disconnect();
                    break;
                }
            }
        }
    }
}

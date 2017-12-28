package ru.spbau.java2.torrent;

import ru.spbau.java2.torrent.main_entities.ClientImpl;
import ru.spbau.java2.torrent.main_entities.ServerImpl;
import ru.spbau.java2.torrent.messages.ListAnswer;
import ru.spbau.java2.torrent.model.ClientDescriptor;
import ru.spbau.java2.torrent.model.FileId;
import ru.spbau.java2.torrent.model.PartId;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Scanner;

import static java.lang.System.exit;

public class CLIRunner {
    public static void main(String[] args) {
        if (args[0].equals("server")) {
            ServerImpl server = null;
            try {
                server = new ServerImpl();
            } catch (IOException e) {
                System.out.println("Could not start server");
                e.printStackTrace();
                exit(1);
            }
            server.startServer();

            System.out.println("Enter anything to stop server");
            System.out.println(new Scanner(System.in).nextLine());
            try {
                server.stopServer();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            ClientImpl client = new ClientImpl();
            try {
                client.connectToServer(InetAddress.getLocalHost());
            } catch (IOException e) {
                System.out.println("Could not connect to server");
                e.printStackTrace();
                exit(1);
            }

            Scanner scanner = new Scanner(System.in);
            System.out.println("Commands:\n" +
                    "\t1 - list files on server\n" +
                    "\t2 <path> - upload file\n" +
                    "\t3 <id> - list sources\n" +
                    "\t4 <id> <ip> <port> - stat existing parts\n" +
                    "\t5 <fid> <number-of-parts> <ip> <port> - get all parts of file\n");
            System.out.println("Press Ctrl+D to exit");
            while (scanner.hasNextLine()) {
                String s = scanner.nextLine();
                String[] split = s.split(" ");
                int cmd;
                try {
                    cmd = Integer.valueOf(split[0]);
                } catch (NumberFormatException e) {
                    continue;
                }

                System.out.println("Command output: ");
                try {
                    if (cmd == 1) {
                        ListAnswer listAnswer = client.executeList();
                        listAnswer.getFiles().forEach(fl -> System.out.println(String.format("%d %s %d", fl.getId().getId(), fl.getName(), fl.getSize())));
                    } else if (cmd == 2) {
                        Path path = Paths.get(split[1]);
                        System.out.println(String.format("File id is %d",
                                client.executeUpload(path.toString(),
                                path.toFile().length()).getId().getId()));
                    } else if (cmd == 3) {
                        Integer id = Integer.valueOf(split[1]);
                        client.executeSources(new FileId(id)).getClients().forEach(
                                clientDescriptor -> System.out.println(String.format("%s %s", clientDescriptor.getAddress(), clientDescriptor.getPort()))
                        );
                    } else if (cmd == 4) {
                        Integer id = Integer.valueOf(split[1]);
                        InetAddress ip = InetAddress.getByName(split[2]);
                        Integer port = Integer.valueOf(split[3]);
                        client.executeStat(new ClientDescriptor(ip, port), new FileId(id))
                                .getPartIds()
                                .stream()
                                .sorted(Comparator.comparingInt(PartId::getId))
                                .forEach(partId -> System.out.println(partId.getId()));
                    } else if (cmd == 5) {
                        Integer fid = Integer.valueOf(split[1]);
                        Integer pid = Integer.valueOf(split[2]);
                        InetAddress ip = InetAddress.getByName(split[3]);
                        Integer port = Integer.valueOf(split[4]);
                        for (int i = 0; i < pid; i++) {
                            client.executeGet(new ClientDescriptor(ip, port), new FileId(fid),
                                    new PartId(i));
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Unsuccessfull command");
                    e.printStackTrace();
                }
                System.out.println("End command output");
            }

            try {
                client.stopClient();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

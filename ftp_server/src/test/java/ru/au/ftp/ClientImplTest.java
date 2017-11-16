package ru.au.ftp;

import javafx.util.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class ClientImplTest {
    private Server serverThread;
    private Client client;

    // Paths.get(".") is "/ftp_server/." folder.
    private static Path testDirPath = Paths.get(".", "src", "test", "resources", "testDir");
    private static Path testFilePath = Paths.get(testDirPath.toString(), "file1.txt");

    @Before
    public void setUp() throws IOException {
        serverThread = new ServerImpl();
        serverThread.start();
        client = new ClientImpl();
        client.connect(InetAddress.getLocalHost(),
                serverThread.getPort());
    }

    @After
    public void tearDown() throws IOException {
        serverThread.shutdown();
        client.disconnect();
    }

    @Test(expected = Exception.class)
    public void testConnectFalsePortThrows() throws IOException {
        client.disconnect();
        client.connect(InetAddress.getLocalHost(), 1);
    }

    @Test
    public void testExecuteList() throws IOException {
        Set<Pair<String, Boolean>> files = new HashSet<>(client.executeList(testDirPath));

        String prefix = "./src/test/resources/testDir/";
        Set<Pair<String, Boolean>> expected = new HashSet<>(Arrays.asList(
                new Pair<>(prefix + "file1.txt", false),
                new Pair<>(prefix + "qwe", true),
                new Pair<>(prefix + "file2.txt", false)
        ));
        assertEquals(expected, files);
    }

    @Test
    public void testExecuteGet() throws IOException {
        String fileBytes = new String(client.executeGet(testFilePath));
        String expectedFileBytes = new String(Files.readAllBytes(testFilePath));
        assertEquals(expectedFileBytes, fileBytes);
    }
}

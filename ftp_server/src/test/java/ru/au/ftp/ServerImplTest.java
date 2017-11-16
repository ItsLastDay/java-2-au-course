package ru.au.ftp;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ServerImplTest {
    private Server serverThread;

    @Test
    public void testServerRunsWithoutExceptions() throws IOException {
        serverThread = new ServerImpl();
        serverThread.start();
        serverThread.shutdown();
    }

    @Test(expected = Exception.class)
    public void testUnstartedServerShutdownThrows() {
        serverThread = new ServerImpl();
        serverThread.shutdown();
    }

    @Test
    public void testServerRunsOnRequestedPort() throws IOException {
        serverThread = new ServerImpl();
        int portNumber = 10893;
        serverThread.start(portNumber);
        assertEquals(portNumber, serverThread.getPort());
        serverThread.shutdown();
    }
}

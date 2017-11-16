package ru.au.ftp;

import java.io.IOException;

public interface Server {
    /**
     * Get the port to which the server listens.
     *
     * Precondition: `start` is called on this instance.
     *
     * @return port
     */
    int getPort();

    /**
     * Start server, request it to listen on a specific port.
     *
     * @param portNumber the desired port, to which users can connect
     */
    void start(int portNumber) throws IOException;

    /**
     * Start server and let OS choose the port for us.
     */
    void start() throws IOException;

    /**
     * Shuts the server down.
     *
     * Precondition: `start` is called on this instance.
     */
    void shutdown();
}

package ru.au.ftp;

import java.io.IOException;

public interface Server {
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
}

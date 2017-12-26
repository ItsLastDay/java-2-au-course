package ru.spbau.java2.torrent.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;

public class ClientDescriptor {
    private final InetAddress addr;
    private final short port;

    public ClientDescriptor(InetAddress addr, short port) {
        this.addr = addr;
        this.port = port;
    }

    public InetAddress getAddress() {
        return addr;
    }

    public int getPort() {
        return port;
    }

    public void writeTo(DataOutputStream out) throws IOException {
        out.write(addr.getAddress());
        out.writeShort(port);
    }

    public static ClientDescriptor fromStream(DataInputStream in) throws IOException {
        byte[] addr = new byte[4];
        in.readFully(addr);
        short port = in.readShort();
        return new ClientDescriptor(InetAddress.getByAddress(addr), port);
    }
}

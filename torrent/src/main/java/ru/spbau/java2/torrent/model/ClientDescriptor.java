package ru.spbau.java2.torrent.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Objects;

public class ClientDescriptor {
    private final InetAddress addr;
    private final int port;

    public ClientDescriptor(InetAddress addr, int port) {
        this.addr = addr;
        this.port = Constants.normalizePort(port);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientDescriptor that = (ClientDescriptor) o;
        return getPort() == that.getPort() &&
                Objects.equals(addr, that.addr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addr, getPort());
    }
}

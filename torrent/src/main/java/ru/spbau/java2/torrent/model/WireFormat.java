package ru.spbau.java2.torrent.model;

import ru.spbau.java2.torrent.exceptions.ProtocolViolation;
import ru.spbau.java2.torrent.messages.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

public class WireFormat {
    private final DataOutputStream out;
    private final DataInputStream in;

    private static final byte HEADER_BOGUS_VALUE = 10;
    private byte headerValue = HEADER_BOGUS_VALUE;

    public WireFormat(Socket client) throws IOException {
        out = new DataOutputStream(client.getOutputStream());
        in = new DataInputStream(client.getInputStream());
    }

    public WireFormat(OutputStream out, InputStream in) {
        this.out = new DataOutputStream(out);
        this.in = new DataInputStream(in);
    }

    private void readHeader() throws IOException {
        headerValue = in.readByte();
    }

    public Message serverReadSomeMessage() throws IOException {
        readHeader();
        if (headerValue == 1)
            return deserializeList();
        if (headerValue == 2)
            return deserializeUpload();
        if (headerValue == 3)
            return deserializeSources();
        if (headerValue == 4)
            return deserializeUpdate();
        throw new ProtocolViolation();
    }


    public void serverWriteSomeMessage(Message msg) throws IOException {
        if (ListAnswer.class.isInstance(msg))
            serializeListAnswer((ListAnswer) msg);
        if (UploadAnswer.class.isInstance(msg))
            serializeUploadAnswer((UploadAnswer) msg);
        if (SourcesAnswer.class.isInstance(msg))
            serializeSourcesAnswer((SourcesAnswer) msg);
        if (UpdateAnswer.class.isInstance(msg))
            serializeUpdateAnswer((UpdateAnswer) msg);
    }

    public Message clientReadSomeMessage() throws IOException {
        readHeader();
        if (headerValue == 1)
            return deserializeStat();
        if (headerValue == 2)
            return deserializeGet();
        throw new ProtocolViolation();
    }

    private void writeHeader(int value) throws IOException {
        out.writeByte(value);
    }

    private void assertHeader(int value) throws IOException {
        byte valueToCompare = headerValue;
        if (valueToCompare == HEADER_BOGUS_VALUE) {
            valueToCompare = in.readByte();
        }

        if (valueToCompare != value) {
            headerValue = HEADER_BOGUS_VALUE;
            throw new ProtocolViolation();
        }

        headerValue = HEADER_BOGUS_VALUE;
    }

    public void serializeGet(Get msg) throws IOException {
        writeHeader(2);
        msg.getId().writeTo(out);
        out.writeInt(msg.getPartIndex().getId());
    }

    public Get deserializeGet() throws IOException {
        assertHeader(2);
        FileId id = FileId.fromStream(in);
        int partIdx = in.readInt();
        return new Get(id, new PartId(partIdx));
    }

    public void serializeGetAnswer(GetAnswer msg) throws IOException {
        out.write(msg.getContent());
    }

    public GetAnswer deserializeGetAnswer() throws IOException {
        byte[] content = new byte[Constants.FILE_PART_SIZE_BYTES];
        in.readFully(content);
        return new GetAnswer(content);
    }

    public void serializeList(List msg) throws IOException {
        writeHeader(1);
    }

    public List deserializeList() throws IOException {
        assertHeader(1);
        return new List();
    }

    public void serializeListAnswer(ListAnswer msg) throws IOException {
        out.writeInt(msg.getFiles().size());
        for (FileDescriptor desc: msg.getFiles()) {
            desc.writeTo(out); // Cannot use streams because of possible exception.
        }
    }

    public ListAnswer deserializeListAnswer() throws IOException {
        Collection<FileDescriptor> lst = new ArrayList<>();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            lst.add(FileDescriptor.fromStream(in));
        }
        return new ListAnswer(lst);
    }

    public void serializeSources(Sources msg) throws IOException {
        writeHeader(3);
        msg.getId().writeTo(out);
    }

    public Sources deserializeSources() throws IOException {
        assertHeader(3);
        FileId fileId = FileId.fromStream(in);
        return new Sources(fileId);
    }

    public void serializeSourcesAnswer(SourcesAnswer msg) throws IOException {
        out.writeInt(msg.getClients().size());
        for (ClientDescriptor clientDescriptor : msg.getClients()) {
            clientDescriptor.writeTo(out);
        }
    }

    public SourcesAnswer deserializeSourcesAnswer() throws IOException {
        int size = in.readInt();
        Collection<ClientDescriptor> clientDescriptors = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            clientDescriptors.add(ClientDescriptor.fromStream(in));
        }
        return new SourcesAnswer(clientDescriptors);
    }

    public void serializeStat(Stat msg) throws IOException {
        writeHeader(1);
        msg.getId().writeTo(out);
    }

    public Stat deserializeStat() throws IOException {
        assertHeader(1);
        return new Stat(FileId.fromStream(in));
    }

    public void serializeStatAnswer(StatAnswer msg) throws IOException {
        out.writeInt(msg.getPartIds().size());
        for (PartId partId : msg.getPartIds()) {
            partId.writeTo(out);
        }
    }

    public StatAnswer deserializeStatAnswer() throws IOException {
        int size = in.readInt();
        Collection<PartId> ints = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ints.add(PartId.fromStream(in));
        }
        return new StatAnswer(ints);
    }

    public void serializeUpdate(Update msg) throws IOException {
        writeHeader(4);
        out.writeShort(msg.getPort());
        out.writeInt(msg.getFileIds().size());
        for (FileId fileId : msg.getFileIds()) {
            fileId.writeTo(out);
        }
    }

    public Update deserializeUpdate() throws IOException {
        assertHeader(4);
        short port = in.readShort();
        int size = in.readInt();
        Collection<FileId> fileIds = new ArrayList<>();
        for (int i = 0; i < size; i++) {
             fileIds.add(FileId.fromStream(in));
        }
        return new Update(port, fileIds);
    }

    public void serializeUpdateAnswer(UpdateAnswer msg) throws IOException {
        out.writeBoolean(msg.isOk());
    }

    public UpdateAnswer deserializeUpdateAnswer() throws IOException {
        return new UpdateAnswer(in.readBoolean());
    }

    public void serializeUpload(Upload msg) throws IOException {
        writeHeader(2);
        out.writeUTF(msg.getName());
        out.writeLong(msg.getSize());
    }

    public Upload deserializeUpload() throws IOException {
        assertHeader(2);
        String name = in.readUTF();
        long size = in.readLong();
        return new Upload(name, size);
    }

    public void serializeUploadAnswer(UploadAnswer msg) throws IOException {
        msg.getId().writeTo(out);
    }

    public UploadAnswer deserializeUploadAnswer() throws IOException {
        return new UploadAnswer(FileId.fromStream(in));
    }

}

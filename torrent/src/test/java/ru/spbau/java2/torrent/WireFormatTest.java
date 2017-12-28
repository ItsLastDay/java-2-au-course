package ru.spbau.java2.torrent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.spbau.java2.torrent.messages.*;
import ru.spbau.java2.torrent.model.FileId;
import ru.spbau.java2.torrent.model.PartId;
import ru.spbau.java2.torrent.model.WireFormat;

import java.io.*;
import java.util.Collections;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class WireFormatTest {
    private WireFormat wire;

    private File tmp;

    @Before
    public void setupWire() throws IOException {
        tmp = File.createTempFile("tmp", "fl");
        DataInputStream in = new DataInputStream(new FileInputStream(tmp));
        DataOutputStream out = new DataOutputStream(new FileOutputStream(tmp));
        wire = new WireFormat(out, in);
    }

    @After
    public void deinitWire() {
        tmp.delete();
    }

    @Test
    public void testGet() throws IOException {
        Get msg = new Get(new FileId(123), new PartId(345));
        wire.serializeGet(msg);
        assertEquals(msg, wire.deserializeGet());

        wire.serializeGet(msg);
        assertTrue(Get.class.isInstance(wire.clientReadSomeMessage()));
    }

    @Test
    public void testGetAnswer() throws IOException {
        GetAnswer msg = new GetAnswer(new byte[123]);
        wire.serializeGetAnswer(msg);
        assertEquals(msg, wire.deserializeGetAnswer());
    }

    @Test
    public void testList() throws IOException {
        List msg = new List();
        wire.serializeList(msg);
        assertTrue(List.class.isInstance(wire.deserializeList()));

        wire.serializeList(msg);
        assertTrue(List.class.isInstance(wire.serverReadSomeMessage()));
    }

    @Test
    public void testListAnswer() throws IOException {
        ListAnswer msg = new ListAnswer(Collections.emptyList());
        wire.serializeListAnswer(msg);
        assertEquals(msg, wire.deserializeListAnswer());

        wire.serverWriteSomeMessage(msg);
        assertEquals(msg, wire.deserializeListAnswer());
    }

    @Test
    public void testSources() throws IOException {
        Sources msg = new Sources(new FileId(999));
        wire.serializeSources(msg);
        assertEquals(msg, wire.deserializeSources());

        wire.serializeSources(msg);
        assertTrue(Sources.class.isInstance(wire.serverReadSomeMessage()));
    }

    @Test
    public void testSourcesAnswer() throws IOException {
        SourcesAnswer msg = new SourcesAnswer(Collections.emptyList());
        wire.serializeSourcesAnswer(msg);
        assertEquals(msg, wire.deserializeSourcesAnswer());

        wire.serverWriteSomeMessage(msg);
        assertEquals(msg, wire.deserializeSourcesAnswer());
    }

    @Test
    public void testStat() throws IOException {
        Stat msg = new Stat(new FileId(1144));
        wire.serializeStat(msg);
        assertEquals(msg, wire.deserializeStat());

        wire.serializeStat(msg);
        assertTrue(Stat.class.isInstance(wire.clientReadSomeMessage()));
    }

    @Test
    public void testStatAnswer() throws IOException {
        StatAnswer msg = new StatAnswer(Collections.emptyList());
        wire.serializeStatAnswer(msg);
        assertEquals(msg, wire.deserializeStatAnswer());
    }

    @Test
    public void testUpdate() throws IOException {
        Update msg = new Update((short) 0, Collections.emptyList());
        wire.serializeUpdate(msg);
        assertEquals(msg, wire.deserializeUpdate());

        wire.serializeUpdate(msg);
        assertTrue(Update.class.isInstance(wire.serverReadSomeMessage()));
    }

    @Test
    public void testUpdateAnswer() throws IOException {
        UpdateAnswer msg = new UpdateAnswer(true);
        wire.serializeUpdateAnswer(msg);
        assertEquals(msg, wire.deserializeUpdateAnswer());

        wire.serverWriteSomeMessage(msg);
        assertEquals(msg, wire.deserializeUpdateAnswer());
    }


    @Test
    public void testUpload() throws IOException {
        Upload msg = new Upload("polka", 19843);
        wire.serializeUpload(msg);
        assertEquals(msg, wire.deserializeUpload());

        wire.serializeUpload(msg);
        assertTrue(Upload.class.isInstance(wire.serverReadSomeMessage()));
    }

    @Test
    public void testUploadAnswer() throws IOException {
        UploadAnswer msg = new UploadAnswer(new FileId(543));
        wire.serializeUploadAnswer(msg);
        assertEquals(msg, wire.deserializeUploadAnswer());

        wire.serverWriteSomeMessage(msg);
        assertEquals(msg, wire.deserializeUploadAnswer());
    }
}

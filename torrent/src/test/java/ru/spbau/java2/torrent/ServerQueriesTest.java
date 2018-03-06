package ru.spbau.java2.torrent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import ru.spbau.java2.torrent.main_entities.ServerQueryExecutor;
import ru.spbau.java2.torrent.messages.List;
import ru.spbau.java2.torrent.messages.Update;
import ru.spbau.java2.torrent.messages.Upload;
import ru.spbau.java2.torrent.model.ClientDescriptor;
import ru.spbau.java2.torrent.state.ServerState;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class ServerQueriesTest {
    @Mock
    private ServerState state;

    private ServerQueryExecutor executor;

    @Before
    public void setUpServer() throws UnknownHostException {
        executor = new ServerQueryExecutor(
                new ClientDescriptor(InetAddress.getLocalHost(), (short) 12345), state);
    }

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();


    @Test
    public void testUploadCallsState() {
        executor.executeUpload(new Upload("ййй", 123456));
        verify(state).uploadFile(any(), eq("ййй"), eq((long)123456));
    }

    @Test
    public void testUpdatePerformsUpdate() {
        Update msg = new Update((short) 123, Collections.emptyList());
        executor.executeUpdate(msg);
        verify(state).performUpdate(eq(msg), any());
    }

    @Test
    public void testListGetsAllFiles() {
        executor.executeList(new List());
        verify(state).getAllFiles();
    }
}

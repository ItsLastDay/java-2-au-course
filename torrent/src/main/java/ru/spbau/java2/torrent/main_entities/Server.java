package ru.spbau.java2.torrent.main_entities;

import ru.spbau.java2.torrent.messages.ListAnswer;
import ru.spbau.java2.torrent.messages.SourcesAnswer;
import ru.spbau.java2.torrent.messages.UpdateAnswer;
import ru.spbau.java2.torrent.messages.UploadAnswer;

public interface Server {
    public void startServer();
    void updateLiveClients();
    void shutdownServer();

    ListAnswer answerList();
    UploadAnswer answerUpload();
    SourcesAnswer answerSources();
    UpdateAnswer answerUpdate();
}

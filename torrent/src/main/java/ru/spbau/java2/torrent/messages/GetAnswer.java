package ru.spbau.java2.torrent.messages;


import static ru.spbau.java2.torrent.model.Constants.normalize;

public class GetAnswer implements Message {
    private final byte[] content;

    public GetAnswer(byte[] content) {
        this.content = normalize(content);
    }


    public byte[] getContent() {
        return content;
    }
}

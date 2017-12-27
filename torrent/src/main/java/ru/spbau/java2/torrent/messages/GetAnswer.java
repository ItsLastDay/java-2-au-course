package ru.spbau.java2.torrent.messages;


import static ru.spbau.java2.torrent.model.Constants.normalizeBytesToFilepart;

public class GetAnswer implements Message {
    private final byte[] content;

    public GetAnswer(byte[] content) {
        this.content = normalizeBytesToFilepart(content);
    }


    public byte[] getContent() {
        return content;
    }
}

package ru.spbau.java2.torrent.messages;


import java.util.Arrays;

import static ru.spbau.java2.torrent.model.Constants.normalizeBytesToFilepart;

public class GetAnswer implements Message {
    private final byte[] content;

    public GetAnswer(byte[] content) {
        this.content = normalizeBytesToFilepart(content);
    }


    public byte[] getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetAnswer getAnswer = (GetAnswer) o;
        return Arrays.equals(getContent(), getAnswer.getContent());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getContent());
    }
}

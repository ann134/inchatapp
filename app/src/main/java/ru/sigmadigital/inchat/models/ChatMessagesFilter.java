package ru.sigmadigital.inchat.models;

import java.io.Serializable;

public class ChatMessagesFilter extends JsonParser implements Serializable {

    long lastMessage;
    int count;
    boolean previous;

    public ChatMessagesFilter(long lastMessage, int count, boolean previous) {
        this.lastMessage = lastMessage;
        this.count = count;
        this.previous = previous;
    }
}

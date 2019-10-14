package ru.sigmadigital.inchat.models;

import java.io.Serializable;

public class ChatRequest extends JsonParser implements Serializable {

    String name;
    int[] users;

    public ChatRequest(String name, int[] users) {
        this.name = name;
        this.users = users;
    }
}

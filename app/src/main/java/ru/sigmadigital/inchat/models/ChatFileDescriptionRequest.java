package ru.sigmadigital.inchat.models;

import java.io.Serializable;

public class ChatFileDescriptionRequest extends JsonParser implements Serializable {

    String name;
    int type;

    public ChatFileDescriptionRequest(String name, int type) {
        this.name = name;
        this.type = type;
    }
}

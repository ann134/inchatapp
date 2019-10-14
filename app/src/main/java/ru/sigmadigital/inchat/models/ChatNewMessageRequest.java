package ru.sigmadigital.inchat.models;

import java.io.Serializable;
import java.util.List;

public class ChatNewMessageRequest extends JsonParser implements Serializable {

    String message;
    List<Long> files;

    public ChatNewMessageRequest(String message, List<Long> files) {
        this.message = message;
        this.files = files;
    }

}

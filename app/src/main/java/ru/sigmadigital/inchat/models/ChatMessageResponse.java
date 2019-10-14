package ru.sigmadigital.inchat.models;

import java.io.Serializable;
import java.util.List;

public class ChatMessageResponse extends JsonParser implements Serializable {

    long id;
    String message;
    UserResponse from;
    List<FileResponse> files;
    long createdAt;
    boolean readed;


    public long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public UserResponse getFrom() {
        return from;
    }

    public List<FileResponse> getFiles() {
        return files;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public boolean isReaded() {
        return readed;
    }
}

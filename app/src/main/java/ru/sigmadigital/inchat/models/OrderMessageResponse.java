package ru.sigmadigital.inchat.models;

import java.io.Serializable;

public class OrderMessageResponse extends JsonParser implements Serializable {

    int id;
    String message;
    UserResponse creator;
    String createdAt;
    boolean readed;


    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public UserResponse getCreator() {
        return creator;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public boolean isReaded() {
        return readed;
    }
}

package ru.sigmadigital.inchat.models;

import java.io.Serializable;
import java.util.List;

public class ChatResponse extends  JsonParser implements Serializable {

    long id;
    String name;
    ChatMessageResponse lastMessage;
    List<UserResponse> users;
    String createdAt;
    UserResponse creator;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ChatMessageResponse getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(ChatMessageResponse lastMessage) {
        this.lastMessage = lastMessage;
    }

    public List<UserResponse> getUsers() {
        return users;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public UserResponse getCreator() {
        return creator;
    }
}

package ru.sigmadigital.inchat.models;

import java.io.Serializable;
import java.util.List;

public class OrderAcceptResponse extends JsonParser implements Serializable {

    int id;
    String message;
    UserResponse creator;
    String createdAt;
    int price;
    int days;
    List<OrderMessageResponse> messages;
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

    public int getPrice() {
        return price;
    }

    public int getDays() {
        return days;
    }

    public List<OrderMessageResponse> getMessages() {
        return messages;
    }

    public boolean isReaded() {
        return readed;
    }
}

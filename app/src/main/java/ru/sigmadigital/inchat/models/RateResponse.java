package ru.sigmadigital.inchat.models;

import java.io.Serializable;

public class RateResponse extends JsonParser implements Serializable {

    int id;
    String message;
    UserResponse creator;
    OrderResponse project;
    int rate;
    String createdAt;
    int type;


    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public UserResponse getCreator() {
        return creator;
    }

    public OrderResponse getProject() {
        return project;
    }

    public int getRate() {
        return rate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public int getType() {
        return type;
    }



    public static class RateType {
        public static int positive = 1;
        public static int negative = -1;
        public static int neutral = 0;
    }

}

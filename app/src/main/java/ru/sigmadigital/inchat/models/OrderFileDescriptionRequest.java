package ru.sigmadigital.inchat.models;

import java.io.Serializable;

public class OrderFileDescriptionRequest extends JsonParser implements Serializable {

    String name;

    public OrderFileDescriptionRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

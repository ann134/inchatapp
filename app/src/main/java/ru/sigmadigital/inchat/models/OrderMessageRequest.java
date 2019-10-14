package ru.sigmadigital.inchat.models;

import java.io.Serializable;

public class OrderMessageRequest extends JsonParser implements Serializable {

    String text;

    public OrderMessageRequest(String text) {
        this.text = text;
    }
}

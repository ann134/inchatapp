package ru.sigmadigital.inchat.models;

import java.io.Serializable;

public class OrderAcceptRequest extends JsonParser implements Serializable {

    String text;
    int price;
    int days;

    public OrderAcceptRequest(String text, int price, int days) {
        this.text = text;
        this.price = price;
        this.days = days;
    }
}

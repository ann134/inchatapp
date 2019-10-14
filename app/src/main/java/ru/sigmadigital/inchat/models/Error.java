package ru.sigmadigital.inchat.models;

import java.io.Serializable;

public class Error extends JsonParser implements Serializable {

    int code;
    String description;

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

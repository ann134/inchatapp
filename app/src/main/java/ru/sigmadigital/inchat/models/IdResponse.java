package ru.sigmadigital.inchat.models;

import java.io.Serializable;

public class IdResponse extends JsonParser implements Serializable {

    long id;

    public long getId() {
        return id;
    }
}

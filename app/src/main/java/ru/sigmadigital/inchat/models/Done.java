package ru.sigmadigital.inchat.models;

import java.io.Serializable;

public class Done extends JsonParser implements Serializable {

    boolean done;

    public boolean isDone() {
        return done;
    }
}

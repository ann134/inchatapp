package ru.sigmadigital.inchat.models;

import java.io.Serializable;

public class RestorePassword extends JsonParser implements Serializable {

    String email;

    public RestorePassword(String email) {
        this.email = email;
    }
}

package ru.sigmadigital.inchat.models;

import java.io.Serializable;

public class AuthGoogle extends JsonParser implements Serializable {

    String token;

    public AuthGoogle(String token) {
        this.token = token;
    }
}

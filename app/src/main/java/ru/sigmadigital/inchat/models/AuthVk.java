package ru.sigmadigital.inchat.models;

import java.io.Serializable;

public class AuthVk extends JsonParser implements Serializable {

    String id;
    String token;

    public AuthVk(String id, String token) {
        this.id = id;
        this.token = token;
    }
}

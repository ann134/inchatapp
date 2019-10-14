package ru.sigmadigital.inchat.models;

import java.io.Serializable;

public class AuthEmail extends JsonParser implements Serializable {

    String email;
    String password;

    public AuthEmail(String email, String password) {
        this.email = email;
        this.password = password;
    }
}

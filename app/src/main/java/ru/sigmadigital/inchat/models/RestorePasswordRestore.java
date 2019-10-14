package ru.sigmadigital.inchat.models;

import java.io.Serializable;

public class RestorePasswordRestore extends JsonParser implements Serializable {

    String code;
    String password;
    String email;

    public RestorePasswordRestore(String code, String password, String email) {
        this.code = code;
        this.password = password;
        this.email = email;
    }
}

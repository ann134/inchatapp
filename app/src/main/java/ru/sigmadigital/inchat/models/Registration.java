package ru.sigmadigital.inchat.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Registration extends JsonParser implements Serializable {

    String name;
    String university;
    String email;

    AuthEmail authEmail;
    AuthGoogle authGoogle;
    AuthVk authVk;

    public Registration(String name, String university, String email, AuthEmail authEmail) {
        this.name = name;
        this.university = university;
        this.email = email;
        this.authEmail = authEmail;
    }

    public Registration(String name, String email, AuthEmail authEmail) {
        this.name = name;
        this.email = email;
        this.authEmail = authEmail;
    }

    public Registration(String email, AuthGoogle authGoogle) {
        this.email = email;
        this.authGoogle = authGoogle;
    }

    public Registration( String email, AuthVk authVk) {
        this.email = email;
        this.authVk = authVk;
    }

}

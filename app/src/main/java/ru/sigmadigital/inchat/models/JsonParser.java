package ru.sigmadigital.inchat.models;


import com.google.gson.Gson;

import java.io.Serializable;

public abstract class JsonParser implements Serializable {

    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return new Gson().fromJson(json, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

}





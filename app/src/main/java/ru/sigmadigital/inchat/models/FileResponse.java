package ru.sigmadigital.inchat.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FileResponse extends JsonParser implements Serializable {
    @SerializedName("id")
    int id;

    @SerializedName("name")
    String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

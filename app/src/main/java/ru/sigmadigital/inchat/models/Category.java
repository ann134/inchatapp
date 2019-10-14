package ru.sigmadigital.inchat.models;

import java.io.Serializable;

public class Category extends JsonParser implements Serializable {

    long id;
    String name;
    long parent;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getParent() {
        return parent;
    }
}

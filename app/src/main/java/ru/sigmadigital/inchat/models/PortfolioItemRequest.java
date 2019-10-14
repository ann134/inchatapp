package ru.sigmadigital.inchat.models;

import java.io.Serializable;

public class PortfolioItemRequest extends JsonParser implements Serializable {

    String description;
    String title;
    int type;

    public PortfolioItemRequest(String description, String title, int type) {
        this.description = description;
        this.title = title;
        this.type = type;
    }
}


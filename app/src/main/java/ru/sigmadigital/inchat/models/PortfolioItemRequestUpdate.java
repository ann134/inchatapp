package ru.sigmadigital.inchat.models;

import java.io.Serializable;

public class PortfolioItemRequestUpdate extends JsonParser  implements Serializable {

    String description;
    String title;

    public PortfolioItemRequestUpdate(String description, String title) {
        this.description = description;
        this.title = title;
    }
}

package ru.sigmadigital.inchat.models;

import java.io.Serializable;

public class OrderRequest extends JsonParser implements Serializable {

    private int orderType;
    private int priceType;
    private int price;
    private long category;
    private String title;
    private String description;
    private long actuality;

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public void setPriceType(int priceType) {
        this.priceType = priceType;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setCategory(long category) {
        this.category = category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setActuality(long actuality) {
        this.actuality = actuality;
    }
}

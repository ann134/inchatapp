package ru.sigmadigital.inchat.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class OrderResponse extends JsonParser implements Serializable {

    private long id;
    private String title;
    private String description;
    private int orderType;
    private int priceType;
    private int price;
    private Category category;
    private long actuality;

    @SerializedName("files")
    @Expose
    List<FileResponse> files;

    private UserResponse creator;
    private long createdAt;
    private String updatedAt;
    private int responsesCount;
    private int viewsCount;


    public static class TypeOrders{
        public static int quick_task = 1; //Проектная работа - быстрая задача;
        public static int long_work = 2; //Подработка (работа с оплатой по временному промежутку);
    }

    public static class TypePayment{
        public static int fix = 1;
        public static int free = 2;
        public static int treaty = 3;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getOrderType() {
        return orderType;
    }

    public int getPriceType() {
        return priceType;
    }

    public int getPrice() {
        return price;
    }

    public Category getCategory() {
        return category;
    }

    public long getActuality() {
        return actuality;
    }

    public List<FileResponse> getFiles() {
        return files;
    }

    public UserResponse getCreator() {
        return creator;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public int getResponsesCount() {
        return responsesCount;
    }

    public int getViewsCount() {
        return viewsCount;
    }
}

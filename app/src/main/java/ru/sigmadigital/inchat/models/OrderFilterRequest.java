package ru.sigmadigital.inchat.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderFilterRequest extends JsonParser implements Serializable {
    public static final int SORT_MY_ORDERS = 1;
    public static final int SORT_NEW = 2;
    public static final int SORT_INCREASE_PRICE = 3;
    public static final int SORT_REDUCTION_PRICE = 4;

    private List<Long> categories = new ArrayList<>();
    private int page = 0;
    private int priceType = -1;
    private int orderType = -1;
    private int sortType = -1;
    private int minPrice = -1;
    private int maxPrice = -1;
    private String searchSequence = "-1";
    private boolean attachedFiles = false;

    public OrderFilterRequest() {
    }

    public List<Long> getCategories() {
        return categories;
    }

    public void setCategories(List<Long> categories) {
        this.categories = categories;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPriceType() {
        return priceType;
    }

    public void setPriceType(int priceType) {
        this.priceType = priceType;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public boolean isAttachedFiles() {
        return attachedFiles;
    }

    public void setAttachedFiles(boolean attachedFiles) {
        this.attachedFiles = attachedFiles;
    }

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    public String getSearchSequence() {
        return searchSequence;
    }

    public void setSearchSequence(String searchSequence) {
        this.searchSequence = searchSequence;
    }
}

package ru.sigmadigital.inchat.models;

import java.io.Serializable;

public class PortfolioItemResponse extends JsonParser implements Serializable {

    int id;
    String description;
    String title;
    int type;

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public int getType() {
        return type;
    }

    public static class FilesType {
        public static int FILE = 0;
        public static int IMAGE = 1;
        public static int SOUND = 0;
        public static int VIDEO = 1;

    }


}

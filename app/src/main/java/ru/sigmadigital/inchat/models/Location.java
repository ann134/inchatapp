package ru.sigmadigital.inchat.models;

import java.io.Serializable;

public class Location extends JsonParser implements Serializable {

    double lat;
    double lon;

    public Location(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }
}

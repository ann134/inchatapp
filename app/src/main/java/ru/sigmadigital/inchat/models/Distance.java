package ru.sigmadigital.inchat.models;

import java.io.Serializable;

public class Distance extends JsonParser implements Serializable {
    double lat;
    double lon;
    long radius;

    public Distance(double lat, double lon, long radius) {
        this.lat = lat;
        this.lon = lon;
        this.radius = radius;
    }
}

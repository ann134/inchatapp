package ru.sigmadigital.inchat.models;

import java.io.Serializable;

public class RateRequest extends JsonParser  implements Serializable {

    int rate;
    String review;

    public RateRequest(int rate, String review) {
        this.rate = rate;
        this.review = review;
    }


}

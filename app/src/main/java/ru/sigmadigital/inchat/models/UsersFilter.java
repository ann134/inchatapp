package ru.sigmadigital.inchat.models;

import java.io.Serializable;

public class UsersFilter extends JsonParser implements Serializable {

    int profiMinCreator = -1;
    int profiMaxCreator = -1;
    int profiMin = -1;
    int profiMax = -1;
    int karmaMin = -1;
    int karmaMax = -1;
    int[] categories;
    String university;
    Distance distance;
    int profileType = -1;
    String name;

    public void setProfiMinCreator(int profiMinCreator) {
        this.profiMinCreator = profiMinCreator;
    }

    public void setProfiMaxCreator(int profiMaxCreator) {
        this.profiMaxCreator = profiMaxCreator;
    }

    public void setProfiMin(int profiMin) {
        this.profiMin = profiMin;
    }

    public void setProfiMax(int profiMax) {
        this.profiMax = profiMax;
    }

    public void setKarmaMin(int karmaMin) {
        this.karmaMin = karmaMin;
    }

    public void setKarmaMax(int karmaMax) {
        this.karmaMax = karmaMax;
    }

    public void setCategories(int[] categories) {
        this.categories = categories;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public void setProfileType(int profileType) {
        this.profileType = profileType;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package ru.sigmadigital.inchat.models;

import java.io.Serializable;
import java.util.Date;

public class UserRequest extends JsonParser  implements Serializable {

    Date dateOfBirth;
    String mail;
    String vk;
    String telegram;
    String skype;
    String whatsapp;
    String facebook;

    String city;
    String country;
    String university;

    int[] categories;


    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setVk(String vk) {
        this.vk = vk;
    }

    public void setTelegram(String telegram) {
        this.telegram = telegram;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public void setCategories(int[] categories) {
        this.categories = categories;
    }


    public void setMail(String mail) {
        this.mail = mail;
    }
}

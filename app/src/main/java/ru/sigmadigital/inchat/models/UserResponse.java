package ru.sigmadigital.inchat.models;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class UserResponse extends JsonParser implements Serializable {

    long id;
    String name;
    String university;
    String mail;
    Date dateOfBirth;

    String vk;
    String telegram;
    String skype;
    String whatsapp;
    String facebook;

    @SerializedName("executorRateResponse")
    ExecutorRate executorRate;
    @SerializedName("customerRateResponse")
    CustomerRate customerRate;

    String city;
    String country;

    List<Category> categories;


    int userType;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMail() {
        return mail;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getUniversity() {
        return university;
    }

    public String getVk() {
        return vk;
    }

    public String getTelegram() {
        return telegram;
    }

    public String getSkype() {
        return skype;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public String getFacebook() {
        return facebook;
    }

    public ExecutorRate getExecutorRate() {
        return executorRate;
    }

    public CustomerRate getCustomerRate() {
        return customerRate;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public int getUserType() {
        return userType;
    }

    public List<Category> getCategories() {
        return categories;
    }



    public class ExecutorRate  implements Serializable{

        int profi;
        int karma;

        public int getProfi() {
            return profi;
        }

        public int getKarma() {
            return karma;
        }
    }

    public class CustomerRate implements Serializable{

        int profi;

        public int getProfi() {
            return profi;
        }
    }


    public static class UsersType {
        public static int PUBLIC = 0;
        public static int PRIVATE = 1;
    }

}

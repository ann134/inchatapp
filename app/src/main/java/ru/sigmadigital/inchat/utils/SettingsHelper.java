package ru.sigmadigital.inchat.utils;

import android.content.Context;

import ru.sigmadigital.inchat.App;

import ru.sigmadigital.inchat.models.Registration;
import ru.sigmadigital.inchat.models.Token;
import ru.sigmadigital.inchat.models.UserResponse;


public class SettingsHelper {

    public static String NAME = "settings";
    public static String FIELD_TOKEN = "token";
    public static String FIELD_Registration = "Registration";
    public static String USER = "user";

    public static Token getToken() {
        if (App.getAppContext() != null) {
            String json = App.getAppContext().getSharedPreferences(NAME, Context.MODE_PRIVATE)
                    .getString(FIELD_TOKEN, null);
            return Token.fromJson(json, Token.class);
        } else {
            return null;
        }
    }

    public static void setToken(Token token) {
        if (App.getAppContext() != null) {
            App.getAppContext().getSharedPreferences(NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putString(FIELD_TOKEN, token != null ? token.toJson() : null)
                    .apply();
        }
    }

    public static Registration getRegistration() {
        if (App.getAppContext() != null) {
            String json = App.getAppContext().getSharedPreferences(NAME, Context.MODE_PRIVATE)
                    .getString(FIELD_Registration, null);
            return UserResponse.fromJson(json, Registration.class);
        } else {
            return null;
        }
    }

    public static void setRegistration(Registration user) {
        if (App.getAppContext() != null) {
            App.getAppContext().getSharedPreferences(NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putString(FIELD_Registration, user != null ? user.toJson() : null)
                    .apply();
        }
    }


    public static void clear() {
        if (App.getAppContext() != null) {
            App.getAppContext().getSharedPreferences(NAME, Context.MODE_PRIVATE).edit()
                    .clear()
                    .apply();
        }
    }

    public static void setUserId(long userId){
        if(App.getAppContext() != null){
            App.getAppContext().getSharedPreferences(NAME, Context.MODE_PRIVATE).edit()
                    .putLong(USER,userId)
                    .apply();
        }
    }

    public static long getUser(){
        if(App.getAppContext() != null){
            return App.getAppContext().getSharedPreferences(NAME, Context.MODE_PRIVATE)
                    .getLong(USER, -1);
        } else {
            return  -1;
        }
    }



}

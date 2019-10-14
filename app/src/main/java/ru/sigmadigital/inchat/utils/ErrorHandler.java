package ru.sigmadigital.inchat.utils;

import android.app.Activity;
import android.util.Log;

import ru.sigmadigital.inchat.models.Error;

public class ErrorHandler {

    public static void catchError(Error error, Activity activity) {

        if (error.getCode() == 19){
            ValidateToken.refresh(activity, new ValidateToken.IValid() {
                @Override
                public void OnValid() {
                    Log.e("token was refresh!", "retry the request!");
                }
            });
        }
    }
}

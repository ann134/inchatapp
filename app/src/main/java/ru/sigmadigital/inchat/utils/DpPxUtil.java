package ru.sigmadigital.inchat.utils;

import ru.sigmadigital.inchat.App;

public class DpPxUtil {

    public static int getPixelsFromDp(int dp){
        return (int)(dp * App.getAppContext().getResources().getDisplayMetrics().density);
    }
    public static int getPixelsFromSp(int sp){
        return (int)(sp * App.getAppContext().getResources().getDisplayMetrics().scaledDensity);
    }

    public static int getDpFromPixels(int pixels){
        return (int)( pixels / App.getAppContext().getResources().getDisplayMetrics().density);
    }
}

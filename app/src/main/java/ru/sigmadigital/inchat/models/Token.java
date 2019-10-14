package ru.sigmadigital.inchat.models;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Token extends JsonParser  implements Serializable {

    private String refresh;
    private String access;
    private Date accessExp;
    private Date refreshExp;

    public String getRefresh() {
        return refresh;
    }

    public String getAccess() {
        return access;
    }

    public Date getAccessExp() {
        return accessExp;
    }

    public Date getRefreshExp() {
        return refreshExp;
    }

    /* public Date getAccessExp() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            return simpleDateFormat.parse(accessExp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
        return accessExp;
    }

    public String getRefreshExp() {
        return refreshExp;
    }*/
}

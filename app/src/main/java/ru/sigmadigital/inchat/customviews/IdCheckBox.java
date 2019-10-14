package ru.sigmadigital.inchat.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import ru.sigmadigital.inchat.R;

public class IdCheckBox extends CheckBox {
    private long serverId;

    public IdCheckBox(Context context) {
        super(context);
    }

    public IdCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IdCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public IdCheckBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

}

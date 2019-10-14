package ru.sigmadigital.inchat.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewMargins  extends RecyclerView.ItemDecoration {

    private int margin;


    public RecyclerViewMargins(@IntRange(from=0)int margin) {
        this.margin = margin;


    }


    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        outRect.bottom = margin;

        outRect.top = margin;

    }
}

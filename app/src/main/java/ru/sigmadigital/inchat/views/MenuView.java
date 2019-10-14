package ru.sigmadigital.inchat.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import ru.sigmadigital.inchat.R;

public class MenuView extends ConstraintLayout {

    private TextView title;
    private ImageView icon;

    public MenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        String service = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(service);
        ConstraintLayout layout = (ConstraintLayout) li.inflate(R.layout.view_menu, this, true);

        this.icon = layout.findViewById(R.id.icon);
        this.title = layout.findViewById(R.id.title);

    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setIcon(int icon) {
        this.icon.setImageResource(icon);
    }

}

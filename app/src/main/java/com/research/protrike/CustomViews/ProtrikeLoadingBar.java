package com.research.protrike.CustomViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.research.protrike.R;

public class ProtrikeLoadingBar extends FrameLayout {

    Integer icons = 10;
    Integer max = 100;
    List<ImageView> iconsList;
    Context context;

    public ProtrikeLoadingBar(@NonNull Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public ProtrikeLoadingBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }

    public ProtrikeLoadingBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context);
    }

    public ProtrikeLoadingBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.customviews_protrike_loading_bar, this, true);
        iconsList = new ArrayList<>();
        iconsList.addAll(Arrays.asList(
                findViewById(R.id.icon0),
                findViewById(R.id.icon1),
                findViewById(R.id.icon2),
                findViewById(R.id.icon3),
                findViewById(R.id.icon4),
                findViewById(R.id.icon5),
                findViewById(R.id.icon6),
                findViewById(R.id.icon7),
                findViewById(R.id.icon8),
                findViewById(R.id.icon9)
        ));
    }

    public void setProgress(int progress) {
        int fill = (int) (icons * ((float) progress / (float) max));
        for (int icon_number = 0; icon_number < fill; icon_number++){
            if (icon_number == 0 || icon_number == 9){
                iconsList.get(icon_number).setImageResource(R.drawable.filled_map_marker);
            } else {
                iconsList.get(icon_number).setImageResource(R.drawable.filled_circle);
            }
        }
    }

    public void setMax(int max){
        this.max = max;
    }
}

package com.research.protrike.CustomViews;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.research.protrike.R;

public class PetLoadingBar extends FrameLayout {

    Integer paws = 9;
    Integer max = 100;
    List<ImageView> pawsList;
    Context context;

    public PetLoadingBar(@NonNull Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public PetLoadingBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }

    public PetLoadingBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context);
    }

    public PetLoadingBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.customviews_petloadingbar, this, true);
        pawsList = new ArrayList<>();
        pawsList.addAll(Arrays.asList(
                findViewById(R.id.paw1),
                findViewById(R.id.paw2),
                findViewById(R.id.paw3),
                findViewById(R.id.paw4),
                findViewById(R.id.paw5),
                findViewById(R.id.paw6),
                findViewById(R.id.paw7),
                findViewById(R.id.paw8),
                findViewById(R.id.paw9)
        ));
        ColorStateList stateList = ThemedColor.getColorStateList(context, R.attr.backgroundOverlay);
        for (ImageView paw : pawsList) paw.setImageTintList(stateList);
    }

    public void setProgress(int progress) {
        int fill = (int) (paws * ((float) progress / (float) max));
        Log.d("SET_PROGRESS", "Progress: "+progress);
        Log.d("SET_PROGRESS", "Max: "+max);
        Log.d("SET_PROGRESS", "Percentage: "+(float) progress / (float) max);
        Log.d("SET_PROGRESS", "Fill: "+fill);
        ColorStateList stateList = ThemedColor.getColorStateList(context, R.attr.emphasis);
        for (int paw_number = 0; paw_number < fill; paw_number++){
            pawsList.get(paw_number).setImageTintList(stateList);
        }
    }

    public void setMax(int max){
        this.max = max;
    }
}

package com.gobbledygook.theawless.eventlock.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gobbledygook.theawless.eventlock.R;

public class PresetPreference extends Preference {
    private int image_res;

    public PresetPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PresetPreference, 0, 0);
        try {
            image_res = typedArray.getResourceId(R.styleable.PresetPreference_preset_image, 0);
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);
        LinearLayout linearLayout = new LinearLayout(parent.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(view);
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setAdjustViewBounds(true);
        imageView.setPadding(0, 0, 0, 20);
        imageView.setImageResource(image_res);
        linearLayout.addView(imageView);
        return linearLayout;
    }
}

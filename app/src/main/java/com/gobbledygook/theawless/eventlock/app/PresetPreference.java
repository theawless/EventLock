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

class PresetPreference extends Preference {
    private int image_res;

    public PresetPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PresetPreference);
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
        linearLayout.addView(imageView);
        return linearLayout;
    }

    @Override
    protected void onBindView(View view) {
        ((ImageView) ((LinearLayout) view).getChildAt(1)).setImageResource(image_res);
        super.onBindView(view);
    }
}

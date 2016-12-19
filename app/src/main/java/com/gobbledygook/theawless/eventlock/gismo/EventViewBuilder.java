package com.gobbledygook.theawless.eventlock.gismo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gobbledygook.theawless.eventlock.helper.Enums;

class EventViewBuilder {
    private final Context gismoContext;
    RelativeLayout fullContainerRelativeLayout;
    private LinearLayout textContainerLinearLayout;

    EventViewBuilder(Context gismoContext) {
        this.gismoContext = gismoContext;
    }

    void setUpFullContainerRelativeLayout(int eventViewWidth) {
        fullContainerRelativeLayout = new RelativeLayout(gismoContext);
        fullContainerRelativeLayout.setLayoutParams(new GridLayoutManager.LayoutParams(eventViewWidth, GridLayout.LayoutParams.WRAP_CONTENT));
    }

    void setUpTextContainerRelativeLayout(String position) {
        textContainerLinearLayout = new LinearLayout(gismoContext);
        textContainerLinearLayout.setOrientation(LinearLayout.VERTICAL);
        fullContainerRelativeLayout.addView(textContainerLinearLayout);
        textContainerLinearLayout.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        textContainerLinearLayout.getLayoutParams().width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        ((RelativeLayout.LayoutParams) textContainerLinearLayout.getLayoutParams()).addRule(RelativeLayout.CENTER_VERTICAL);
        switch (position) {
            case "left": {
                ((RelativeLayout.LayoutParams) textContainerLinearLayout.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                break;
            }
            case "center": {
                ((RelativeLayout.LayoutParams) textContainerLinearLayout.getLayoutParams()).addRule(RelativeLayout.CENTER_HORIZONTAL);
                break;
            }
            case "right": {
                ((RelativeLayout.LayoutParams) textContainerLinearLayout.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                break;
            }
        }
    }

    void setUpTitleTextView(int left, int above, int right, int below, int size, String alignment) {
        TextView titleTextView = new TextView(gismoContext);
        textContainerLinearLayout.addView(titleTextView);
        setUpCommonTextView(titleTextView, left, above, right, below, size, alignment);
        titleTextView.setTag(Enums.ItemTag.Title);
    }

    void setUpTimeTextView(int left, int above, int right, int below, int size, String alignment) {
        TextView timeTextView = new TextView(gismoContext);
        textContainerLinearLayout.addView(timeTextView);
        setUpCommonTextView(timeTextView, left, above, right, below, size, alignment);
        timeTextView.setTag(Enums.ItemTag.Time);
    }

    private void setUpCommonTextView(TextView textView, int left, int above, int right, int below, int size, String alignment) {
        textView.setMaxLines(1);
        textView.setHorizontallyScrolling(true);
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setMarqueeRepeatLimit(-1);
        textView.setSelected(true);
        textView.setTextSize(size);
        textView.setPadding(left, above, right, below);
        textView.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
        textView.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
        switch (alignment) {
            case "left": {
                textView.setGravity(Gravity.LEFT);
                break;
            }
            case "right": {
                textView.setGravity(Gravity.RIGHT);
                break;
            }
        }
    }

    void setUpColorImageView(int left, int above, int right, int below, String type, int height, int width, String alignment, boolean stick) {
        ImageView colorImageView = new ImageView(gismoContext);
        fullContainerRelativeLayout.addView(colorImageView);
        colorImageView.setTag(Enums.ItemTag.Image);
        GradientDrawable outlineDrawable = new GradientDrawable();
        ShapeDrawable shapeDrawable = new ShapeDrawable();
        if (type.equals("oval")) {
            shapeDrawable.setShape(new OvalShape());
            outlineDrawable.setShape(GradientDrawable.OVAL);
        } else {
            shapeDrawable.setShape(new RectShape());
            outlineDrawable.setShape(GradientDrawable.RECTANGLE);
        }
        shapeDrawable.setIntrinsicHeight(height);
        shapeDrawable.setIntrinsicWidth(width);
        colorImageView.setImageDrawable(new LayerDrawable(new Drawable[]{shapeDrawable, outlineDrawable}));
        colorImageView.getLayoutParams().height = FrameLayout.LayoutParams.WRAP_CONTENT;
        colorImageView.getLayoutParams().width = FrameLayout.LayoutParams.WRAP_CONTENT;
        colorImageView.setPadding(left, above, right, below);
        ((RelativeLayout.LayoutParams) colorImageView.getLayoutParams()).addRule(RelativeLayout.CENTER_VERTICAL);
        if (!stick) {
            switch (alignment) {
                case "left": {
                    ((RelativeLayout.LayoutParams) colorImageView.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    break;
                }
                case "right": {
                    ((RelativeLayout.LayoutParams) colorImageView.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    break;
                }
            }
        } else {
            ((RelativeLayout.LayoutParams) textContainerLinearLayout.getLayoutParams()).removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
            ((RelativeLayout.LayoutParams) textContainerLinearLayout.getLayoutParams()).removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            colorImageView.setId(View.generateViewId());
            switch (alignment) {
                case "left": {
                    ((RelativeLayout.LayoutParams) colorImageView.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    ((RelativeLayout.LayoutParams) textContainerLinearLayout.getLayoutParams()).addRule(RelativeLayout.RIGHT_OF, colorImageView.getId());
                    break;
                }
                case "right": {
                    ((RelativeLayout.LayoutParams) colorImageView.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    ((RelativeLayout.LayoutParams) textContainerLinearLayout.getLayoutParams()).addRule(RelativeLayout.LEFT_OF, colorImageView.getId());
                    break;
                }
            }
        }
    }
}

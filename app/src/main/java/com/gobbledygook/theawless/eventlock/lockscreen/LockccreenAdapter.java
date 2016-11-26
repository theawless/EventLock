package com.gobbledygook.theawless.eventlock.lockscreen;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

class LockscreenAdapter extends RecyclerView.Adapter<LockscreenAdapter.ViewHolder> {
    JSONArray titles;
    JSONArray times;
    JSONArray colors;
    JSONArray descriptions;

    private static int dpToPixel(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    @Override
    public LockscreenAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        TextView titleTextView = new TextView(context);
        TextView timeTextView = new TextView(context);
        ImageView colorImageView = new ImageView(context);

        ShapeDrawable oval = new ShapeDrawable(new OvalShape());
        oval.setIntrinsicHeight(dpToPixel(context, 10));
        oval.setIntrinsicWidth(dpToPixel(context, 10));
        oval.getPaint().setColor(Color.TRANSPARENT);
        colorImageView.setBackground(oval);

        RelativeLayout relativeLayout = new RelativeLayout(context);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        relativeLayout.setPadding(dpToPixel(context, 24), dpToPixel(context, 24), dpToPixel(context, 24), 0);
        relativeLayout.addView(timeTextView);
        relativeLayout.addView(titleTextView);
        relativeLayout.addView(colorImageView);

        titleTextView.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        titleTextView.getLayoutParams().width = RelativeLayout.LayoutParams.MATCH_PARENT;
        titleTextView.setId(View.generateViewId());

        timeTextView.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        timeTextView.getLayoutParams().width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        ((RelativeLayout.LayoutParams) timeTextView.getLayoutParams()).addRule(RelativeLayout.BELOW, titleTextView.getId());
        timeTextView.setId(View.generateViewId());

        colorImageView.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        colorImageView.getLayoutParams().width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        ((RelativeLayout.LayoutParams) colorImageView.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        ((RelativeLayout.LayoutParams) colorImageView.getLayoutParams()).addRule(RelativeLayout.LEFT_OF, timeTextView.getId());

        return new ViewHolder(relativeLayout, titleTextView, timeTextView, colorImageView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            holder.titleTextView.setText(titles.getString(position));
            holder.timeTextView.setText(times.getString(position));

            ((ShapeDrawable) holder.colorImageView.getBackground()).getPaint().setColor(Integer.parseInt(colors.getString(position)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return titles.length();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relativeLayout;
        TextView titleTextView;
        TextView timeTextView;
        ImageView colorImageView;

        ViewHolder(RelativeLayout relativeLayout, TextView titleTextView, TextView timeTextView, ImageView colorImageView) {
            super(relativeLayout);
            this.relativeLayout = relativeLayout;
            this.titleTextView = titleTextView;
            this.timeTextView = timeTextView;
            this.colorImageView = colorImageView;
        }
    }
}
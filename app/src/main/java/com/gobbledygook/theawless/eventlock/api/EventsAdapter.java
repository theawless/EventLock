package com.gobbledygook.theawless.eventlock.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
    public ArrayList<Event> events;
    public int currentEventIndex;
    private Context appContext;
    private SharedPreferences preferences;


    public EventsAdapter(Context appContext, SharedPreferences preferences) {
        this.appContext = appContext;
        this.preferences = preferences;
    }

    private static int dpToPixel(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context gismoContext = parent.getContext();
        TextView titleTextView = new TextView(gismoContext);
        TextView timeTextView = new TextView(gismoContext);
        ImageView colorImageView = new ImageView(gismoContext);

        ShapeDrawable oval = new ShapeDrawable(new OvalShape());
        oval.setIntrinsicHeight(dpToPixel(gismoContext, 10));
        oval.setIntrinsicWidth(dpToPixel(gismoContext, 10));
        oval.getPaint().setColor(Color.TRANSPARENT);
        colorImageView.setBackground(oval);

        RelativeLayout relativeLayout = new RelativeLayout(gismoContext);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        relativeLayout.setPadding(dpToPixel(gismoContext, 24), dpToPixel(gismoContext, 24), dpToPixel(gismoContext, 24), 0);
        relativeLayout.addView(timeTextView);
        relativeLayout.addView(titleTextView);
        relativeLayout.addView(colorImageView);

        titleTextView.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        titleTextView.getLayoutParams().width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        titleTextView.setId(View.generateViewId());

        timeTextView.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        timeTextView.getLayoutParams().width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        ((RelativeLayout.LayoutParams) timeTextView.getLayoutParams()).addRule(RelativeLayout.BELOW, titleTextView.getId());
        timeTextView.setId(View.generateViewId());

        colorImageView.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        colorImageView.getLayoutParams().width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        ((RelativeLayout.LayoutParams) colorImageView.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        ((RelativeLayout.LayoutParams) colorImageView.getLayoutParams()).addRule(RelativeLayout.CENTER_VERTICAL);

        return new ViewHolder(relativeLayout, titleTextView, timeTextView, colorImageView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.titleTextView.setText(events.get(position).getFormattedTitle(appContext));
        holder.timeTextView.setText(events.get(position).getFormattedTime(appContext));
        ((ShapeDrawable) holder.colorImageView.getBackground()).getPaint().setColor(Integer.parseInt(events.get(position).color));
    }

    @Override
    public int getItemCount() {
        return events.size();
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
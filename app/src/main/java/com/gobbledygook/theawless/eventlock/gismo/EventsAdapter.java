package com.gobbledygook.theawless.eventlock.gismo;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gobbledygook.theawless.eventlock.helper.Enums;

import java.util.ArrayList;


class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
    private final SharedPreferences preferences;
    ArrayList<String>[] events;
    int currentEventIndex;
    boolean currentHighlight[];
    private int[][] innerDimensions = new int[][]{};

    EventsAdapter(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    void setupInnerDimensions(int[][] innerDimensions) {
        this.innerDimensions = innerDimensions;
    }

    boolean inRange() {
        return 0 <= currentEventIndex && currentEventIndex < getItemCount();
    }

    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EventsAdapter.ViewHolder(new EventViewBuildDirector(parent.getContext(), preferences).setBuilder(new EventViewBuilder(parent.getContext())).setInnerDimensions(innerDimensions).getEventView());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder.getColorImageView() != null) {
            ((ShapeDrawable) ((LayerDrawable) holder.getColorImageView().getDrawable()).getDrawable(0)).getPaint().setColor(Integer.parseInt(events[Enums.EventInfo.Color.ordinal()].get(position)));
        }
        if (position == currentEventIndex) {
            if (currentHighlight[Enums.EventInfo.Title.ordinal()])
                holder.getTitleTextView().setTypeface(holder.getTitleTextView().getTypeface(), Typeface.BOLD);
            if (currentHighlight[Enums.EventInfo.Time.ordinal()])
                holder.getTimeTextView().setTypeface(holder.getTimeTextView().getTypeface(), Typeface.BOLD);
            if (currentHighlight[Enums.EventInfo.Color.ordinal()] && holder.getColorImageView() != null && Integer.parseInt(events[Enums.EventInfo.Color.ordinal()].get(position)) != Color.TRANSPARENT) {
                ((GradientDrawable) ((LayerDrawable) holder.getColorImageView().getDrawable()).getDrawable(1)).setStroke(2, Color.WHITE);
            }
        }
        holder.getTitleTextView().setText(events[Enums.EventInfo.Title.ordinal()].get(position));
        holder.getTimeTextView().setText(events[Enums.EventInfo.Time.ordinal()].get(position));
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        if (currentHighlight[Enums.EventInfo.Title.ordinal()])
            holder.getTitleTextView().setTypeface(Typeface.create(holder.getTitleTextView().getTypeface(), Typeface.NORMAL));
        if (currentHighlight[Enums.EventInfo.Time.ordinal()])
            holder.getTimeTextView().setTypeface(Typeface.create(holder.getTimeTextView().getTypeface(), Typeface.NORMAL));
        if (currentHighlight[Enums.EventInfo.Color.ordinal()] && holder.getColorImageView() != null) {
            ((GradientDrawable) ((LayerDrawable) holder.getColorImageView().getDrawable()).getDrawable(1)).setStroke(2, Color.TRANSPARENT);
        }
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        if (events == null) {
            return 0;
        }
        return events[Enums.EventInfo.Title.ordinal()].size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View view) {
            super(view);
        }

        TextView getTitleTextView() {
            return (TextView) itemView.findViewWithTag(Enums.ItemTag.Title);
        }

        TextView getTimeTextView() {
            return (TextView) itemView.findViewWithTag(Enums.ItemTag.Time);
        }

        ImageView getColorImageView() {
            return (ImageView) itemView.findViewWithTag(Enums.ItemTag.Image);
        }
    }
}

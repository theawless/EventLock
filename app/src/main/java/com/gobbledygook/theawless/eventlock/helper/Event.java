package com.gobbledygook.theawless.eventlock.helper;

import android.content.Context;

import com.gobbledygook.theawless.eventlock.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Event {
    public int allDay;
    public int day_diff;
    public long beginTime;
    public long endTime;
    public String title;
    public String description;
    public String location;
    public String color;

    public Event(String title, String description, String location, long beginTime, long endTime, int allDay, String color) {
        this.title = title;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.allDay = allDay;
        this.location = location;
        this.description = description;
        this.color = color;
        this.day_diff = calculateDayDiff();
    }

    private int calculateDayDiff() {
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DATE);
        calendar.setTimeInMillis(beginTime);
        int other_day = calendar.get(Calendar.DATE);
        return other_day - today;
    }

    public String getFormattedTitle(Context context) {
        if (!location.isEmpty()) {
            return title + " " + context.getString(R.string.at) + " " + location;
        }
        return title;
    }

    public String getFormattedTime(Context context) {
        DateFormat formatter = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);
        String time = formatter.format(beginTime) + " - " + formatter.format(endTime);
        if (allDay == 1) {
            time = context.getString(R.string.all_day);
        }
        switch (day_diff) {
            case 0: {
                break;
            }
            case 1: {
                time = time + context.getString(R.string.comma) + " " + context.getString(R.string.tomorrow);
                break;
            }
            case 2: {
                time = time + context.getString(R.string.comma) + " " + context.getString(R.string.day_after_tomorrow);
                break;
            }
            default: {
                time = time + context.getString(R.string.comma) + " " + context.getString(R.string.after) + " " + (day_diff - 1) + " " + context.getString(R.string.days);
            }
        }
        return time;
    }
}

package com.gobbledygook.theawless.eventlock.background;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.gobbledygook.theawless.eventlock.R;

import org.json.JSONArray;
import org.json.JSONException;

public class CurrentEventUpdaterService extends IntentService {
    private static final String TAG = CurrentEventUpdaterService.class.getSimpleName();
    private CurrentEventUpdaterAlarm currentEventUpdaterAlarm = new CurrentEventUpdaterAlarm();

    public CurrentEventUpdaterService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(TAG, "onHandleIntent");
        handleIntent();
        CalendarLoaderAlarm.completeWakefulIntent(intent);
    }

    private void handleIntent() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);
        int oldCurrentEventIndex = preferences.getInt(getString(R.string.event_to_display_key), Integer.parseInt(getString(R.string.event_to_display_default)));
        int newCurrentEventIndex = oldCurrentEventIndex + 1;
        String jsonLongTimesString = preferences.getString(getString(R.string.event_long_end_times_key), "");
        JSONArray longTimes;
        long oldCurrentEventEndTime, newCurrentEventEndTime;
        try {
            longTimes = new JSONArray(jsonLongTimesString);
            oldCurrentEventEndTime = longTimes.getLong(oldCurrentEventIndex);
            newCurrentEventEndTime = longTimes.getLong(newCurrentEventIndex);
        } catch (JSONException e) {
            Log.wtf(TAG, e.getMessage());
            return;
        }
        if (oldCurrentEventEndTime != -1) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(getString(R.string.event_to_display_key), newCurrentEventIndex);
            editor.apply();
            currentEventUpdaterAlarm.setAlarm(this, newCurrentEventEndTime);
        }
    }
}
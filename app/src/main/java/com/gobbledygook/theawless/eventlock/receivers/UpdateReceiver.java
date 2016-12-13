package com.gobbledygook.theawless.eventlock.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.gobbledygook.theawless.eventlock.events.EventsGismo;


public abstract class UpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        EventsGismo eventsGismo = getGismo(action);
        if (eventsGismo == null) {
            return;
        }
        Bundle bundle = intent.getExtras();
        switch (action) {
            case "EventUpdate":
                eventsGismo.deliverNewEvents(
                        bundle.getStringArrayList("formattedTitles"),
                        bundle.getStringArrayList("formattedTimes"),
                        bundle.getStringArrayList("colors")
                );
                break;
            case "CurrentEventUpdate":
                eventsGismo.deliverNewCurrentEvent(bundle.getInt("currentEvent"));
                break;
            case "LooksUpdate":
                eventsGismo.notifyUpdatedPreferences();
                break;
            case Intent.ACTION_SCREEN_OFF: {
                eventsGismo.scrollToCurrentEvent();
                break;
            }
        }
    }

    protected abstract EventsGismo getGismo(String action);
}
package com.gobbledygook.theawless.eventlock.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.gobbledygook.theawless.eventlock.gismo.EventsGismo;
import com.gobbledygook.theawless.eventlock.helper.Constants;


public abstract class UpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        EventsGismo eventsGismo = getGismo(action);
        if (action == null || eventsGismo == null || !eventsGismo.isReady()) {
            return;
        }
        Bundle bundle = intent.getExtras();
        switch (action) {
            case Constants.events_update:
                eventsGismo.deliverNewEvents(
                        bundle.getStringArrayList(Constants.formatted_titles),
                        bundle.getStringArrayList(Constants.formatted_times),
                        bundle.getStringArrayList(Constants.colors)
                );
                break;
            case Constants.current_event_update:
                eventsGismo.deliverNewCurrentEvent(bundle.getInt(Constants.current_event));
                break;
            case Constants.looks_update:
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
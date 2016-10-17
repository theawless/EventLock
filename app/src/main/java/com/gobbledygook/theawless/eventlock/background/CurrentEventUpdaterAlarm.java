package com.gobbledygook.theawless.eventlock.background;

public class CurrentEventUpdaterAlarm extends AlarmWakefulBroadcastReceiver {
    public CurrentEventUpdaterAlarm() {
        super(CurrentEventUpdaterService.class);
    }
}

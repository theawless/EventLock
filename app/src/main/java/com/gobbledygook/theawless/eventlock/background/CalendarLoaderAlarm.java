package com.gobbledygook.theawless.eventlock.background;

public class CalendarLoaderAlarm extends AlarmWakefulBroadcastReceiver {
    public CalendarLoaderAlarm() {
        super(CalendarLoaderService.class);
    }

}

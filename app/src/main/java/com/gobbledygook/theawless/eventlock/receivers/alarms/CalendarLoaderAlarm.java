package com.gobbledygook.theawless.eventlock.receivers.alarms;

import com.gobbledygook.theawless.eventlock.services.CalendarLoaderService;

public class CalendarLoaderAlarm extends Alarm {
    @Override
    protected Class getServiceClass() {
        return CalendarLoaderService.class;
    }
}

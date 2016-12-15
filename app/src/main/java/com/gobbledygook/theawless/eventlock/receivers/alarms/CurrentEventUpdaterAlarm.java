package com.gobbledygook.theawless.eventlock.receivers.alarms;

import com.gobbledygook.theawless.eventlock.services.CurrentEventUpdaterService;

public class CurrentEventUpdaterAlarm extends Alarm {
    @Override
    protected Class getServiceClass() {
        return CurrentEventUpdaterService.class;
    }
}

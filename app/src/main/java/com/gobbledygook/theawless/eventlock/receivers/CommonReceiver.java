package com.gobbledygook.theawless.eventlock.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gobbledygook.theawless.eventlock.services.CalendarLoaderService;

public class CommonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && (action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals(Intent.ACTION_PROVIDER_CHANGED))) {
            context.startService(new Intent(context, CalendarLoaderService.class));
        }
    }
}

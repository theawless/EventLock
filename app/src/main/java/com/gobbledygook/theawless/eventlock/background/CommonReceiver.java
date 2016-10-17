package com.gobbledygook.theawless.eventlock.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CommonReceiver extends BroadcastReceiver {
    private static final String TAG = CommonReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, "on receive action:" + action);
        if (action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals(Intent.ACTION_PROVIDER_CHANGED)) {
            context.startService(new Intent(context, CalendarLoaderService.class));
        }
    }
}
package com.gobbledygook.theawless.eventlock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CommonReceiver extends BroadcastReceiver {
    private static final String TAG = CommonReceiver.class.getSimpleName();
    SchedulingService schedulingService = new SchedulingService();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, "on receive action:" + action);
        if (action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals(Intent.ACTION_PROVIDER_CHANGED)) {
            Log.v(TAG, "calling handleIntent in service");
            schedulingService.handleIntent(context);
        }
    }
}
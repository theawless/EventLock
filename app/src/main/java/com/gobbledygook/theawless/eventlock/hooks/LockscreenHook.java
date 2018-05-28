package com.gobbledygook.theawless.eventlock.hooks;

import android.content.Intent;
import android.content.IntentFilter;
import android.widget.GridLayout;

import com.gobbledygook.theawless.eventlock.BuildConfig;
import com.gobbledygook.theawless.eventlock.gismo.EventsGismo;
import com.gobbledygook.theawless.eventlock.helper.Constants;
import com.gobbledygook.theawless.eventlock.receivers.UpdateReceiver;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class LockscreenHook implements IXposedHookLoadPackage {
    private EventsGismo eventsGismo = null;
    private final UpdateReceiver updateReceiver = new UpdateReceiver() {
        @Override
        protected EventsGismo getGismo(String action) {
            log("got action " + action);
            return eventsGismo;
        }
    };

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("com.android.systemui")) {
            XposedHelpers.findAndHookMethod("com.android.keyguard.KeyguardStatusView", lpparam.classLoader, "onFinishInflate", new KeyGuardHook());
        }
    }

    private void log(String message) {
        XposedBridge.log("EventLock: " + message);
    }

    private class KeyGuardHook extends XC_MethodHook {
        @Override
        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
            GridLayout gridLayout = (GridLayout) param.thisObject;
            eventsGismo = new EventsGismo(gridLayout, new XSharedPreferences(BuildConfig.APPLICATION_ID)) {
                @Override
                public void notifyUpdatedPreferences() {
                    ((XSharedPreferences) preferences).reload();
                    super.notifyUpdatedPreferences();
                }
            };
            eventsGismo.addRecyclerView();
            log("Added view to lockscreen");
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Constants.events_update);
            intentFilter.addAction(Constants.current_event_update);
            intentFilter.addAction(Constants.looks_update);
            intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
            gridLayout.getContext().registerReceiver(updateReceiver, intentFilter);
        }
    }
}

package com.gobbledygook.theawless.eventlock.hooks;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.GridLayout;

import com.gobbledygook.theawless.eventlock.BuildConfig;
import com.gobbledygook.theawless.eventlock.events.EventsGismo;
import com.gobbledygook.theawless.eventlock.receivers.UpdateReceiver;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class LockscreenHook implements IXposedHookLoadPackage {
    private final EventsGismo eventsGismo = new EventsGismo(new XSharedPreferences(BuildConfig.APPLICATION_ID)) {
        @Override
        public void notifyUpdatedPreferences() {
            ((XSharedPreferences) preferences).reload();
            super.notifyUpdatedPreferences();
        }
    };
    private final UpdateReceiver updateReceiver = new UpdateReceiver() {
        @Override
        protected EventsGismo getGismo(String action) {
            log(action);
            return eventsGismo;
        }
    };

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
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
            Context context = ((GridLayout) param.thisObject).getContext();
            ((GridLayout) param.thisObject).addView(eventsGismo.getRecyclerView(context));
            log("Added view to lockscreen");
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("EventUpdate");
            intentFilter.addAction("CurrentEventUpdate");
            intentFilter.addAction("LooksUpdate");
            intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
            context.registerReceiver(updateReceiver, intentFilter);
        }
    }
}
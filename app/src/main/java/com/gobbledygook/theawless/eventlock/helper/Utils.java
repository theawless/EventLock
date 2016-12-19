package com.gobbledygook.theawless.eventlock.helper;

import android.content.Context;

public class Utils {
    private Utils() {
    }

    public static int dpToPixel(Context context, String dp) {
        return (int) (Integer.parseInt(dp) * context.getResources().getDisplayMetrics().density);
    }
}

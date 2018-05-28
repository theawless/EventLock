package com.gobbledygook.theawless.eventlock.helper;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class Utils {
    private Utils() {
    }

    public static <T extends Comparable<T>> ArrayList<Integer> indexOfAll(T object, ArrayList<T> list) {
        ArrayList<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < list.size(); ++i) {
            if (object.equals(list.get(i))) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    public static int dpToPixel(Context context, String dp) {
        return (int) (Integer.parseInt(dp) * context.getResources().getDisplayMetrics().density);
    }

    public static void preventParentTouchTheft(View view) {
        view.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        view.getParent().requestDisallowInterceptTouchEvent(true);
                        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                            case MotionEvent.ACTION_UP:
                                view.getParent().requestDisallowInterceptTouchEvent(false);
                                view.performClick();
                                break;
                        }
                        return false;
                    }
                }
        );
    }
}

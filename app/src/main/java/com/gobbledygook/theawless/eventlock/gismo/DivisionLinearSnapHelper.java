package com.gobbledygook.theawless.eventlock.gismo;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

class DivisionLinearSnapHelper extends LinearSnapHelper {
    private int divisionFactor;
    private int targetIndex;

    void setDivisionFactor(int divisionFactor) {
        this.divisionFactor = divisionFactor;
    }

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        return targetIndex = super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
    }

    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        int distance[] = super.calculateDistanceToFinalSnap(layoutManager, targetView);
        if (divisionFactor % 2 == 0 && distance != null) {
            if (((GridLayoutManager) layoutManager).getOrientation() == GridLayoutManager.VERTICAL) {
                distance[1] += targetIndex == 0 ? -targetView.getHeight() / 2 : targetView.getHeight() / 2;
            } else {
                distance[0] += targetIndex == 0 ? -targetView.getWidth() / 2 : targetView.getWidth() / 2;
            }
        }
        return distance;
    }
}

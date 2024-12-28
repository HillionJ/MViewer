package fr.red.mviewer.utils;

import android.view.GestureDetector;
import android.view.MotionEvent;

import fr.red.mviewer.FlowActivity;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {

    private IHM ihm = IHM.getIHM();

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (Math.abs(velocityX) > Math.abs(velocityY)) {
            if (velocityX > 0) {
                ((FlowActivity)ihm.getActivite(FlowActivity.class)).scrollToPrevious();
            } else {
                ((FlowActivity)ihm.getActivite(FlowActivity.class)).scrollToNext();
            }
            return true;
        }
        return false;
    }
}

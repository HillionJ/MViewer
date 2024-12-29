package fr.red.mviewer.utils;

import android.view.GestureDetector;
import android.view.MotionEvent;

import fr.red.mviewer.FlowActivity;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    // Gérer les swipes entre les plaquettes
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (Math.abs(velocityX) > Math.abs(velocityY)) {
            if (velocityX > 0) {
                // Gérer le swipe vers la gauche
                ((FlowActivity)IHM.getIHM().getActivite(FlowActivity.class)).scrollToPrevious();
            } else {
                // Gérer le swipe vers la droite
                ((FlowActivity)IHM.getIHM().getActivite(FlowActivity.class)).scrollToNext();
            }
            return true;
        }
        return false;
    }
}

class GestureListener
!!!134914.java!!!	onScroll(inout e1 : MotionEvent, inout e2 : MotionEvent, in distanceX : float, in distanceY : float) : boolean
                return false;
!!!135042.java!!!	onFling(inout e1 : MotionEvent, inout e2 : MotionEvent, in velocityX : float, in velocityY : float) : boolean
                if (Math.abs(velocityX) > Math.abs(velocityY)) {
                    if (velocityX > 0) {
                        scrollToPrevious();
                    } else {
                        scrollToNext();
                    }
                    return true;
                }
                return false;

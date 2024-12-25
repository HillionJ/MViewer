class GestureListener
!!!137858.java!!!	onScroll(inout e1 : MotionEvent, inout e2 : MotionEvent, in distanceX : float, in distanceY : float) : boolean
                return false;
!!!137986.java!!!	onFling(inout e1 : MotionEvent, inout e2 : MotionEvent, in velocityX : float, in velocityY : float) : boolean
                if (Math.abs(velocityX) > Math.abs(velocityY)) {
                    if (velocityX > 0) {
                        scrollToPrevious();
                    } else {
                        scrollToNext();
                    }
                    return true;
                }
                return false;

package io.rong.photoview.gestures;

import android.view.MotionEvent;

public abstract interface GestureDetector
{
  public abstract boolean onTouchEvent(MotionEvent paramMotionEvent);

  public abstract boolean isScaling();

  public abstract boolean isDragging();

  public abstract void setOnGestureListener(OnGestureListener paramOnGestureListener);
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.photoview.gestures.GestureDetector
 * JD-Core Version:    0.6.0
 */
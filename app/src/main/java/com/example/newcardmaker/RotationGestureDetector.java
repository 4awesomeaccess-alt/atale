package com.example.newcardmaker;

import android.view.MotionEvent;

public class RotationGestureDetector {
    public interface OnRotationGestureListener {
        void onRotation(float angle); // delta angle (change)
    }

    private float prevAngle = 0f;
    private boolean isRotating = false;
    private final OnRotationGestureListener listener;

    public RotationGestureDetector(OnRotationGestureListener listener) {
        this.listener = listener;
    }

    public void onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_POINTER_DOWN:
                // 2 આંગળી touch — શરૂઆતનો angle
                if (event.getPointerCount() == 2) {
                    prevAngle = getAngle(event);
                    isRotating = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                // 2 આંગળી move — angle change calculate
                if (isRotating && event.getPointerCount() == 2) {
                    float newAngle = getAngle(event);
                    float delta = newAngle - prevAngle;

                    // -180 થી +180 range માં રાખો
                    if (delta > 180f) delta -= 360f;
                    if (delta < -180f) delta += 360f;

                    listener.onRotation(delta);
                    prevAngle = newAngle;
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                isRotating = false;
                break;
        }
    }

    // 2 આંગળી વચ્ચેનો angle ગણો
    private float getAngle(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        return (float) Math.toDegrees(Math.atan2(dy, dx));
    }
}
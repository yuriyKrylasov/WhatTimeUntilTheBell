package com.whatTimeUntilTheBell;

public class TouchPoint {
    private float mX;
    private float mY;

    public TouchPoint(float x, float y) {
        mX = x;
        mY = y;
    }

    public float getX() {
        return mX;
    }

    public float getY() {
        return mY;
    }

    public void reset() {
        mX = 0.0f;
        mY = 0.0f;
    }

    public boolean isInsideCircle(float centerPointX, float centerPointY, float radius) {
        double xValue = Math.pow(mX - centerPointX, 2);
        double yValue = Math.pow(mY - centerPointY, 2);
        double radiusValue = Math.pow(radius, 2);
        return xValue + yValue <= radiusValue;
    }
}

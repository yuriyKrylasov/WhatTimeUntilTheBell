package com.whatTimeUntilTheBell;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;

class RippleEffectDrawer {
    private final ActionButton mActionButton;
    private static final int RADIUS_INCREMENT = 5;
    private static final long POST_INVALIDATION_DELAY = 100;
    private int currentRadius;

    RippleEffectDrawer(ActionButton actionButton) {
        mActionButton = actionButton;
    }

    boolean isDrawingInProgress() {
        return currentRadius > 0 && !isDrawingFinished();
    }

    boolean isDrawingFinished() {
        return currentRadius >= getEndRippleRadius();
    }

    private int getEndRippleRadius() {
        return (int) (mActionButton.calculateCircleRadius() * 2);
    }

    private void updateRadius() {
        if (mActionButton.isPressed()) {
            if (currentRadius <= getEndRippleRadius()) {
                currentRadius += RADIUS_INCREMENT;
            }
        } else {
            if (isDrawingInProgress()) {
                currentRadius = getEndRippleRadius();
            } else if (isDrawingFinished()) {
                currentRadius = 0;
            }
        }
    }

    void draw(Canvas canvas) {
        updateRadius();
        drawRipple(canvas);
        ViewInvalidator invalidator = mActionButton.getInvalidator();
        if (isDrawingInProgress()) {
            invalidator.requireInvalidation();
        } else if (isDrawingFinished() && !mActionButton.isPressed()) {
            invalidator.requireDelayedInvalidation();
            invalidator.setInvalidationDelay(POST_INVALIDATION_DELAY);
        }
    }

    private void drawRipple(Canvas canvas) {
        canvas.save();
        canvas.clipPath(getCircleClipPath(), Region.Op.INTERSECT);
        TouchPoint point = mActionButton.getTouchPoint();
        canvas.drawCircle(point.getX(), point.getY(), currentRadius, getPreparedPaint());
        canvas.restore();
    }

    private Path getCircleClipPath() {
        Path path = new Path();
        path.addCircle(mActionButton.calculateCenterX(), mActionButton.calculateCenterY(),
                mActionButton.calculateCircleRadius(), Path.Direction.CW);
        return path;
    }

    private Paint getPreparedPaint() {
        mActionButton.resetPaint();
        Paint paint = mActionButton.getPaint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(mActionButton.getButtonColorRipple());
        return paint;
    }
}

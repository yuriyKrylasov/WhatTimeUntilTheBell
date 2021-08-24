package com.whatTimeUntilTheBell;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class ActionButton extends View {
    private final float mSize = dpToPx(56.0f);
    private boolean mIsPressed = false;

    private int mButtonColor = 0xFF673AEE;
    private int mButtonColorPressed = 0xFF311BB1;
    private int mButtonColorRipple = darkenButtonColorPressed();

    private final float mShadowRadius = dpToPx(8.0f);
    private final float mShadowXOffset = 0.0f;
    private final float mShadowYOffset = mShadowRadius;
    private int mShadowColor = 0x42000000;

    private final Drawable mImage = getResources().getDrawable(R.drawable.ic_add);
    private final float mImageSize = dpToPx(24.0f);

    private final Animation mShowAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fab_jump_from_down);
    private final Animation mHideAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fab_jump_to_down);

    private PointF mPoint = new PointF(0.0f, 0.0f);
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected final RippleEffectDrawer rippleEffectDrawer = new RippleEffectDrawer(this);
    protected final ShadowResponsiveDrawer shadowResponsiveDrawer = new ShadowResponsiveDrawer(this);

    boolean invalidationRequired;
    boolean invalidationDelayedRequired;
    long invalidationDelay;

    public ActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initializes the layer type needed for shadows drawing
        setLayerType(LAYER_TYPE_SOFTWARE, mPaint);
    }

    void show() {
        if (getVisibility() == INVISIBLE) {
            startAnimation(mShowAnimation);
            setVisibility(VISIBLE);
        }
    }

    void hide() {
        if (getVisibility() == VISIBLE) {
            startAnimation(mHideAnimation);
            setVisibility(INVISIBLE);
        }
    }

    public boolean isPressed() {
        return mIsPressed;
    }

    void setIsPressed(boolean isPressed) {
        mIsPressed = isPressed;
        invalidate();
    }

    int getButtonColor() {
        return mButtonColor;
    }

    void setButtonColor(int buttonColor) {
        mButtonColor = buttonColor;
        invalidate();
    }

    int getButtonColorPressed() {
        return mButtonColorPressed;
    }

    void setButtonColorPressed(int buttonColorPressed) {
        mButtonColorPressed = buttonColorPressed;
        mButtonColorRipple = darkenButtonColorPressed();
    }

    private int darkenButtonColorPressed() {
        float[] hsv = new float[3];
        Color.colorToHSV(mButtonColorPressed, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

    int getButtonColorRipple() {
        return mButtonColorRipple;
    }

    private boolean hasShadow() {
        return !hasElevation() && mShadowRadius > 0.0f;
    }

    float getShadowRadius() {
        return mShadowRadius;
    }

    float getShadowXOffset() {
        return mShadowXOffset;
    }

    float getShadowYOffset() {
        return mShadowYOffset;
    }

    int getShadowColor() {
        return mShadowColor;
    }

    void setShadowColor(int shadowColor) {
        mShadowColor = shadowColor;
        invalidate();
    }

    PointF getPoint() {
        return mPoint;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        PointF point = new PointF(event.getX(), event.getY());
        boolean touchPointInsideCircle = Math.pow(point.x - calculateCenterX(), 2) +
                Math.pow(point.y - calculateCenterY(), 2) <= Math.pow(calculateCircleRadius(), 2);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (touchPointInsideCircle) {
                    setIsPressed(true);
                    mPoint = point;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (touchPointInsideCircle) {
                    setIsPressed(false);
                    mPoint.set(0, 0);
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!touchPointInsideCircle && mIsPressed) {
                    setIsPressed(false);
                    mPoint.set(0, 0);
                    return true;
                }
        }
        return false;
    }

    @Override
    public void startAnimation(Animation animation) {
        if (animation != null &&
                (getAnimation() == null || getAnimation().hasEnded())) {
            super.startAnimation(animation);
        }
    }

    protected Paint getPaint() {
        return mPaint;
    }

    protected final void resetPaint() {
        mPaint.reset();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas);
        rippleEffectDrawer.draw(canvas);
        if (hasElevation()) {
            drawElevation();
        }
        drawImage(canvas);

        if (invalidationRequired) {
            postInvalidate();
        }
        if (invalidationDelayedRequired) {
            postInvalidateDelayed(invalidationDelay);
        }
        invalidationRequired = false;
        invalidationDelayedRequired = false;
        invalidationDelay = 0L;
    }

    protected void drawCircle(Canvas canvas) {
        resetPaint();
        if (hasShadow()) {
            shadowResponsiveDrawer.draw();
        }
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mIsPressed || rippleEffectDrawer.isDrawingInProgress() ?
                mButtonColorPressed : mButtonColor);
        canvas.drawCircle(calculateCenterX(), calculateCenterY(), calculateCircleRadius(), mPaint);
    }

    protected float calculateCenterX() {
        return getMeasuredWidth() / 2.0f;
    }

    protected float calculateCenterY() {
        return getMeasuredHeight() / 2.0f;
    }

    protected final float calculateCircleRadius() {
        return mSize / 2;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void drawElevation() {
        final float halfSize = mSize / 2;
        final int left     = (int) (calculateCenterX() - halfSize);
        final int top      = (int) (calculateCenterY() - halfSize);
        final int right    = (int) (calculateCenterX() + halfSize);
        final int bottom   = (int) (calculateCenterY() + halfSize);
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(left, top, right, bottom);
            }
        });
    }

    private boolean hasElevation() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getElevation() > 0.0f;
    }

    private void drawImage(Canvas canvas) {
        final int startPointX = (int) (calculateCenterX() - mImageSize / 2);
        final int startPointY = (int) (calculateCenterY() - mImageSize / 2);
        mImage.setBounds(startPointX, startPointY, (int) (startPointX + mImageSize), (int) (startPointY + mImageSize));
        mImage.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (!hasShadow()) {
            setMeasuredDimension(0, 0);
        }
        else {
            final float msr = shadowResponsiveDrawer.getMaxShadowRadius();
            setMeasuredDimension((int) (mSize + ((int) (msr + Math.abs(mShadowXOffset)) * 2)),
                    (int) (mSize + (int) ((msr + Math.abs(mShadowYOffset)) * 2)));
        }
    }

    private float dpToPx(float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }
}

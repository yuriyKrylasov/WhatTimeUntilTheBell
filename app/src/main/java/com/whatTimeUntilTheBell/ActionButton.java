package com.whatTimeUntilTheBell;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
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

    private float mShadowRadius = dpToPx(8.0f);
    private float mShadowXOffset = 0.0f;
    private float mShadowYOffset = mShadowRadius;
    private int mShadowColor = 0x42000000;
    private final boolean mShadowResponsiveEffectEnabled = true;

    private final Drawable mImage = getResources().getDrawable(R.drawable.ic_add);
    private float mImageSize = dpToPx(24.0f);

    private final Animation mShowAnimation = loadAnimation(getContext(), R.anim.fab_jump_from_down);
    private final Animation mHideAnimation = loadAnimation(getContext(), R.anim.fab_jump_to_down);

    private TouchPoint mTouchPoint = new TouchPoint(0.0f, 0.0f);
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final ViewInvalidator mInvalidator = new ViewInvalidator(this);
    protected final RippleEffectDrawer rippleEffectDrawer = new RippleEffectDrawer(this);
    protected final ShadowResponsiveDrawer shadowResponsiveDrawer = new ShadowResponsiveDrawer(this);

    public ActionButton(Context context) {
        super(context);
        initLayerType();
    }

    public ActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayerType();
        initActionButtonAttrs(context, attrs, 0);
    }

    public ActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayerType();
        initActionButtonAttrs(context, attrs, defStyleAttr);
    }

    private void initActionButtonAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ActionButton,
                defStyleAttr, 0);
        initButtonColor(attributes);
        initButtonColorPressed(attributes);
        initButtonColorRipple(attributes);
        initShadowRadius(attributes);
        initShadowXOffset(attributes);
        initShadowYOffset(attributes);
        initShadowColor(attributes);
        initImageSize(attributes);
        attributes.recycle();
    }

    /**
     * Initializes the layer type needed for shadows drawing
     */
    private void initLayerType() {
        setLayerType(LAYER_TYPE_SOFTWARE, mPaint);
    }

    private void initButtonColor(TypedArray attrs) {
        int index = R.styleable.ActionButton_button_color;
        if (attrs.hasValue(index)) {
            mButtonColor = attrs.getColor(index, mButtonColor);
        }
    }

    private void initButtonColorPressed(TypedArray attrs) {
        int index = R.styleable.ActionButton_button_colorPressed;
        if (attrs.hasValue(index)) {
            mButtonColorPressed = attrs.getColor(index, mButtonColorPressed);
            mButtonColorRipple = darkenButtonColorPressed();
        }
    }

    private void initButtonColorRipple(TypedArray attrs) {
        int index = R.styleable.ActionButton_button_colorRipple;
        if (attrs.hasValue(index)) {
            mButtonColorRipple = attrs.getColor(index, mButtonColorRipple);
        }
    }

    private void initShadowRadius(TypedArray attrs) {
        int index = R.styleable.ActionButton_shadow_radius;
        if (attrs.hasValue(index)) {
            mShadowRadius = attrs.getDimension(index, mShadowRadius);
        }
    }

    private void initShadowXOffset(TypedArray attrs) {
        int index = R.styleable.ActionButton_shadow_xOffset;
        if (attrs.hasValue(index)) {
            mShadowXOffset = attrs.getDimension(index, mShadowXOffset);
        }
    }

    private void initShadowYOffset(TypedArray attrs) {
        int index = R.styleable.ActionButton_shadow_yOffset;
        if (attrs.hasValue(index)) {
            mShadowYOffset = attrs.getDimension(index, mShadowYOffset);
        }
    }

    private void initShadowColor(TypedArray attrs) {
        int index = R.styleable.ActionButton_shadow_color;
        if (attrs.hasValue(index)) {
            mShadowColor = attrs.getColor(index, mShadowColor);
        }
    }

    private void initImageSize(TypedArray attrs) {
        int index = R.styleable.ActionButton_image_size;
        if (attrs.hasValue(index)) {
            mImageSize = attrs.getDimension(index, mImageSize);
        }
    }

    public void playShowAnimation() {
        startAnimation(mShowAnimation);
    }

    public void playHideAnimation() {
        startAnimation(mHideAnimation);
    }

    public void show() {
        if (isHidden()) {
            playShowAnimation();
            setVisibility(VISIBLE);
        }
    }

    public void hide() {
        if (!isHidden()) {
            playHideAnimation();
            setVisibility(INVISIBLE);
        }
    }

    public boolean isHidden() {
        return getVisibility() == INVISIBLE;
    }

    public boolean isPressed() {
        return mIsPressed;
    }

    public void setIsPressed(boolean isPressed) {
        mIsPressed = isPressed;
        invalidate();
    }

    public int getButtonColor() {
        return mButtonColor;
    }

    public void setButtonColor(int buttonColor) {
        mButtonColor = buttonColor;
        invalidate();
    }

    public int getButtonColorPressed() {
        return mButtonColorPressed;
    }

    public void setButtonColorPressed(int buttonColorPressed) {
        mButtonColorPressed = buttonColorPressed;
        mButtonColorRipple = darkenButtonColorPressed();
    }

    private int darkenButtonColorPressed() {
        var hsv = new float[3];
        Color.colorToHSV(mButtonColorPressed, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

    public int getButtonColorRipple() {
        return mButtonColorRipple;
    }

    private boolean hasShadow() {
        return !hasElevation() && mShadowRadius > 0.0f;
    }

    public float getShadowRadius() {
        return mShadowRadius;
    }

    public float getShadowXOffset() {
        return mShadowXOffset;
    }

    public float getShadowYOffset() {
        return mShadowYOffset;
    }

    public int getShadowColor() {
        return mShadowColor;
    }

    public void setShadowColor(int shadowColor) {
        mShadowColor = shadowColor;
        invalidate();
    }

    public TouchPoint getTouchPoint() {
        return mTouchPoint;
    }

    protected void setTouchPoint(TouchPoint point) {
        mTouchPoint = point;
    }

    /**
     * Adds additional actions on motion events:
     * 1. Changes the <b>Action Button</b> {@link #mState} to {@link State#PRESSED}
     *    on {@link android.view.MotionEvent#ACTION_DOWN}
     * 2. Changes the <b>Action Button</b> {@link #mState} to {@link State#NORMAL}
     *    on {@link android.view.MotionEvent#ACTION_UP}
     * 3. Changes the <b>Action Button</b> {@link #mState} to {@link State#NORMAL}
     *    on {@link android.view.MotionEvent#ACTION_MOVE} in case when touch point
     *    leaves the main circle
     *
     * @param event motion event
     * @return true if event was handled, otherwise false
     */
    @SuppressWarnings("all")
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        TouchPoint point = new TouchPoint(event.getX(), event.getY());
        boolean touchPointInsideCircle = point.isInsideCircle(calculateCenterX(), calculateCenterY(),
                calculateCircleRadius());
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (touchPointInsideCircle) {
                    setIsPressed(true);
                    mTouchPoint = point;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (touchPointInsideCircle) {
                    setIsPressed(false);
                    mTouchPoint.reset();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!touchPointInsideCircle && mIsPressed) {
                    setIsPressed(false);
                    mTouchPoint.reset();
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

    protected ViewInvalidator getInvalidator() {
        return mInvalidator;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas);
        drawRipple(canvas);
        if (hasElevation()) {
            drawElevation();
        }
        if (mImage != null) {
            drawImage(canvas);
        }
        mInvalidator.invalidate();
    }

    protected void drawCircle(Canvas canvas) {
        resetPaint();
        if (hasShadow()) {
            if (mShadowResponsiveEffectEnabled) {
                shadowResponsiveDrawer.draw();
            } else {
                drawShadow();
            }
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

    protected void drawShadow() {
        mPaint.setShadowLayer(mShadowRadius, mShadowXOffset, mShadowYOffset, mShadowColor);
    }

    protected void drawRipple(Canvas canvas) {
        rippleEffectDrawer.draw(canvas);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void drawElevation() {
        float halfSize = mSize / 2;
        final int left = (int) (calculateCenterX() - halfSize);
        final int top = (int) (calculateCenterY() - halfSize);
        final int right = (int) (calculateCenterX() + halfSize);
        final int bottom = (int) (calculateCenterY() + halfSize);
        ViewOutlineProvider provider = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(left, top, right, bottom);
            }
        };
        setOutlineProvider(provider);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean hasElevation() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getElevation() > 0.0f;
    }

    private void drawImage(Canvas canvas) {
        int startPointX = (int) (calculateCenterX() - mImageSize / 2);
        int startPointY = (int) (calculateCenterY() - mImageSize / 2);
        int endPointX = (int) (startPointX + mImageSize);
        int endPointY = (int) (startPointY + mImageSize);
        mImage.setBounds(startPointX, startPointY, endPointX, endPointY);
        mImage.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(calculateMeasuredWidth(), calculateMeasuredHeight());
    }

    private int calculateMeasuredWidth() {
        return (int) (mSize + calculateShadowWidth());
    }

    private int calculateMeasuredHeight() {
        return (int) (mSize + calculateShadowHeight());
    }

    private int calculateShadowWidth() {
        float shadowRadius = mShadowResponsiveEffectEnabled ?
                shadowResponsiveDrawer.getMaxShadowRadius() : mShadowRadius;
        return hasShadow() ? (int) ((shadowRadius + Math.abs(mShadowXOffset)) * 2) : 0;
    }

    private int calculateShadowHeight() {
        float shadowRadius = mShadowResponsiveEffectEnabled ?
                shadowResponsiveDrawer.getMaxShadowRadius() : mShadowRadius;
        return hasShadow() ? (int) ((shadowRadius + Math.abs(mShadowYOffset)) * 2) : 0;
    }

    private float dpToPx(float dp) {
        return DensityConverter.dpToPx(getContext(), dp);
    }

    private static Animation loadAnimation(Context context, int animResId) {
        return AnimationUtils.loadAnimation(context, animResId);
    }
}

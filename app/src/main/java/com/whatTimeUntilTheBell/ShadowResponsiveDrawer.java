package com.whatTimeUntilTheBell;

class ShadowResponsiveDrawer {
    private final ActionButton mActionButton;
    private static final float SHADOW_RESPONSE_FACTOR = 1.75f;
    private static final float SHADOW_DRAWING_STEP = 0.5f;

    private float mCurrentShadowRadius;

    ShadowResponsiveDrawer(ActionButton actionButton) {
        mActionButton = actionButton;
        mCurrentShadowRadius = actionButton.getShadowRadius();
    }

    void draw() {
        updateRadius();
        mActionButton.getPaint().setShadowLayer(mCurrentShadowRadius, mActionButton.getShadowXOffset(),
                mActionButton.getShadowYOffset(), mActionButton.getShadowColor());
    }

    void updateRadius() {
        if (mActionButton.isPressed() && mCurrentShadowRadius < getMaxShadowRadius()) {
            mCurrentShadowRadius += SHADOW_DRAWING_STEP;
            mActionButton.getInvalidator().requireInvalidation();
        } else if (!mActionButton.isPressed() && mCurrentShadowRadius > getMinShadowRadius()) {
            mCurrentShadowRadius -= SHADOW_DRAWING_STEP;
            mActionButton.getInvalidator().requireInvalidation();
        } else if (!mActionButton.isPressed()){
            mCurrentShadowRadius = mActionButton.getShadowRadius();
        }
    }

    void setCurrentShadowRadius(float currentShadowRadius) {
        mCurrentShadowRadius = currentShadowRadius;
    }

    float getMinShadowRadius() {
        return mActionButton.getShadowRadius();
    }

    float getMaxShadowRadius() {
        return getMinShadowRadius() * SHADOW_RESPONSE_FACTOR;
    }

}

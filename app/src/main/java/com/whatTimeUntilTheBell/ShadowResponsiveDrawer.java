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
            mActionButton.invalidationRequired = true;
        } else if (!mActionButton.isPressed() && mCurrentShadowRadius > mActionButton.getShadowRadius()) {
            mCurrentShadowRadius -= SHADOW_DRAWING_STEP;
            mActionButton.invalidationRequired = true;
        } else if (!mActionButton.isPressed()){
            mCurrentShadowRadius = mActionButton.getShadowRadius();
        }
    }

    float getMaxShadowRadius() {
        return mActionButton.getShadowRadius() * SHADOW_RESPONSE_FACTOR;
    }
}

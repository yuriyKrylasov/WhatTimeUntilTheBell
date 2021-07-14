package com.whatTimeUntilTheBell;

import android.view.View;

class ViewInvalidator {
    private boolean invalidationRequired;
    private boolean invalidationDelayedRequired;
    private long mInvalidationDelay;
    private final View mView;

    ViewInvalidator(View view) {
        mView = view;
    }

    boolean isInvalidationRequired() {
        return invalidationRequired;
    }

    void requireInvalidation() {
        invalidationRequired = true;
    }

    boolean isInvalidationDelayedRequired() {
        return invalidationDelayedRequired;
    }

    void requireDelayedInvalidation() {
        invalidationDelayedRequired = true;
    }

    long getInvalidationDelay() {
        return mInvalidationDelay;
    }

    void setInvalidationDelay(long invalidationDelay) {
        mInvalidationDelay = invalidationDelay;
    }

    void invalidate() {
        if (isInvalidationRequired()) {
            mView.postInvalidate();
        }
        if (isInvalidationDelayedRequired()) {
            mView.postInvalidateDelayed(getInvalidationDelay());
        }
        reset();
    }

    private void reset() {
        invalidationRequired = false;
        invalidationDelayedRequired = false;
        setInvalidationDelay(0L);
    }
}

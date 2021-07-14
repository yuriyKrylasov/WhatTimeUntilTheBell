package com.whatTimeUntilTheBell;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class Preference extends FrameLayout {
    private final TextView mTitle;
    private final TextView mSummary;

    private final int mEnabledColor;
    private final int mDisabledColor;

    public void setSummary(String summary) {
        mSummary.setText(summary);
        if (summary.length() != 0) {
            mSummary.setVisibility(View.VISIBLE);
        }
    }

    public Preference(Context context, AttributeSet attrs) {
        super(context, attrs, 0);

        String androidNs = "http://schemas.android.com/apk/res/android";
        inflate(context, R.layout.preference, this);

        // for animation on click
        setOnClickListener(v -> {});

        mTitle = findViewById(android.R.id.title);
        mTitle.setText(getAttr(attrs, androidNs, "title"));
        mSummary = findViewById(android.R.id.summary);
        mSummary.setText(getAttr(attrs, androidNs, "summary"));
        if (mSummary.getText().length() == 0) {
            mSummary.setVisibility(View.GONE);
        }

        mEnabledColor = mTitle.getTextColors().getDefaultColor();
        mDisabledColor = 0xFF9D9FA2;
    }

    private void setViewsColor(int color) {
        mTitle.setTextColor(color);
        mSummary.setTextColor(color);
    }

    protected void enable() {
        setViewsColor(mEnabledColor);
    }

    protected void disable() {
        setViewsColor(mDisabledColor);
    }

    protected void onDependencyStateChanged(boolean newState) {
        if (newState) {
            enable();
        }
        else {
            disable();
        }
        setEnabled(newState);
    }

    private String getAttr(AttributeSet attrs, String namespace, String name) {
        String s = attrs.getAttributeValue(namespace, name);
        if (s == null) {
            return "";
        }
        if (s.charAt(0) == '@') {
            s = getResources().getString(attrs.getAttributeResourceValue(namespace, name, 0));
        }
        return s;
    }
}

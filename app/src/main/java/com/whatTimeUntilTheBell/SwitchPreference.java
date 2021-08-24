package com.whatTimeUntilTheBell;

import static android.graphics.Color.TRANSPARENT;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.Switch;

public class SwitchPreference extends Preference {
    private final Switch mSwitch;
    private Preference mDependencyByThis;

    interface OnStateChangedListener {
        void onStateChanged(boolean newState);
    }

    public OnStateChangedListener onStateChanged;

    public void setDependencyByThis(Preference dependencyByThis) {
        mDependencyByThis = dependencyByThis;
        dependencyByThis.onDependencyStateChanged(mSwitch.isChecked());
    }

    public SwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        mSwitch = new Switch(getContext());
        mSwitch.setBackgroundColor(TRANSPARENT);
        mSwitch.setClickable(false);

        ((LinearLayout) findViewById(android.R.id.widget_frame)).addView(mSwitch);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(v -> {
            boolean newState = !mSwitch.isChecked();
            if (onStateChanged != null) {
                onStateChanged.onStateChanged(newState);
            }
            if (mDependencyByThis != null) {
                mDependencyByThis.onDependencyStateChanged(newState);
            }
            mSwitch.setChecked(newState);
            l.onClick(v);
        });
    }

    @Override
    protected void disable() {
        super.disable();
        setSwitched(false);
    }

    public void setSwitched(boolean checked) {
        if (mSwitch.isChecked() != checked && isEnabled()) {
            mSwitch.setChecked(checked);
            if (onStateChanged != null) {
                onStateChanged.onStateChanged(checked);
            }
            if (mDependencyByThis != null) {
                mDependencyByThis.onDependencyStateChanged(mSwitch.isChecked());
            }
        }
    }
}

package com.whatTimeUntilTheBell;

import android.content.SharedPreferences;

class SettingSharedPref {
    private final SharedPreferences mPref;
    private final String mPrefName;
    private final Boolean mDefValue;

    public SettingSharedPref(SharedPreferences pref, String prefName, Boolean defValue) {
        mPref = pref;
        mPrefName = prefName;
        mDefValue = defValue;
    }

    public boolean get() {
        return mPref.getBoolean(mPrefName, mDefValue);
    }

    public void set(boolean value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean(mPrefName, value);
        editor.apply();
    }
}

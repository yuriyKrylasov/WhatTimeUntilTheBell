package com.whatTimeUntilTheBell;

import android.content.SharedPreferences;

class SettingsSharedPref {
    public final SettingSharedPref isDarkTheme;
    public final SettingSharedPref isNeedCreateBeautifulXml;
    public final SettingSharedPref isNeedShowNotification;
    public final SettingSharedPref isNeedShowNotificationWhenLessonsOver;

    public SettingsSharedPref(SharedPreferences pref, boolean isDeviceDarkTheme) {
        isDarkTheme                           = new SettingSharedPref(pref, "isDarkTheme", isDeviceDarkTheme);
        isNeedCreateBeautifulXml              = new SettingSharedPref(pref, "isNeedCreateBeautifulXml", false);
        isNeedShowNotification                = new SettingSharedPref(pref, "isNeedShowNotification", true);
        isNeedShowNotificationWhenLessonsOver = new SettingSharedPref(pref, "isNeedShowNotificationWhenLessonsOver", false);
    }
}

package com.whatTimeUntilTheBell;

import android.content.SharedPreferences;

class SettingsSharedPref {
    public final SettingSharedPref isDarkTheme;
    public final SettingSharedPref isNeedAddXmlProlog;
    public final SettingSharedPref isNeedAddNewLineToEndOfXml;
    public final SettingSharedPref useLongXmlTagEnd;
    public final SettingSharedPref isNeedCreateBeautifulXml;
    public final SettingSharedPref isNeedShowNotification;
    public final SettingSharedPref isNeedShowNotificationWhenLessonsOver;

    public SettingsSharedPref(SharedPreferences pref, boolean isDeviceDarkTheme) {
        isDarkTheme                           = new SettingSharedPref(pref, "isDarkTheme", isDeviceDarkTheme);
        isNeedAddXmlProlog                    = new SettingSharedPref(pref, "addXmlProlog");
        isNeedAddNewLineToEndOfXml            = new SettingSharedPref(pref, "addNewLineToEndOfXml");
        useLongXmlTagEnd                      = new SettingSharedPref(pref, "useLongXmlTagEnd");
        isNeedCreateBeautifulXml              = new SettingSharedPref(pref, "isNeedCreateBeautifulXml");
        isNeedShowNotification                = new SettingSharedPref(pref, "isNeedShowNotification", true);
        isNeedShowNotificationWhenLessonsOver = new SettingSharedPref(pref, "isNeedShowNotificationWhenLessonsOver");
    }
}

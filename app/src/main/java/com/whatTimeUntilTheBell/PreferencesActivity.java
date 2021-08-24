package com.whatTimeUntilTheBell;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class PreferencesActivity extends AppCompatActivity {
    private SwitchPreference mDarkThemePref;
    private SwitchPreference mCreateBeautifulXmlPref;
    private SwitchPreference mShowNotificationPref;
    private SwitchPreference mShowNotificationWhenLessonsOverPref;
    private MyApplication mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mApp = (MyApplication) getApplication();
        AppCompatDelegate.setDefaultNightMode(mApp.isDarkTheme() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        mDarkThemePref                       = findViewById(R.id.dark_theme_pref);
        mCreateBeautifulXmlPref              = findViewById(R.id.create_beautiful_xml_pref);
        mShowNotificationPref                = findViewById(R.id.show_notification_pref);
        mShowNotificationWhenLessonsOverPref = findViewById(R.id.show_notification_when_lessons_over_pref);
        Preference mResetSettingsPref        = findViewById(R.id.reset_settings_pref);

        mShowNotificationPref.setDependencyByThis(mShowNotificationWhenLessonsOverPref);

        mDarkThemePref.setSwitched(mApp.isDarkTheme());
        mCreateBeautifulXmlPref.setSwitched(mApp.isNeedCreateBeautifulXml());
        mShowNotificationPref.setSwitched(mApp.isNeedShowNotification());
        mShowNotificationWhenLessonsOverPref.setSwitched(mApp.isNeedShowNotificationWhenLessonsOver());

        mDarkThemePref.onStateChanged = newState -> {
            mApp.setDarkTheme(newState);
            startActivity(new Intent(getApplicationContext(), PreferencesActivity.class));
            finish();
        };
        mCreateBeautifulXmlPref.onStateChanged = newState -> mApp.setNeedCreateBeautifulXml(newState);
        mShowNotificationPref.onStateChanged = newState -> {
            mApp.setNeedShowNotification(newState);
            if (newState) {
                startService(new Intent(this, NotificationService.class));
            }
            else {
                stopService(new Intent(this, NotificationService.class));
            }
        };
        mShowNotificationWhenLessonsOverPref.onStateChanged = newState -> mApp.setNeedShowNotificationWhenLessonsOver(newState);
        mResetSettingsPref.setOnClickListener(v -> {
            mDarkThemePref                      .setSwitched(false);
            mCreateBeautifulXmlPref             .setSwitched(false);
            mShowNotificationPref               .setSwitched(true );
            mShowNotificationWhenLessonsOverPref.setSwitched(false);
        });
    }
}

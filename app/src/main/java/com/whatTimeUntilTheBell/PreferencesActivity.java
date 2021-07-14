package com.whatTimeUntilTheBell;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Random;

public class PreferencesActivity extends AppCompatActivity {
    private byte versionPrefClickedCount = 0;

    private SwitchPreference mDarkThemePref;
    private SwitchPreference mAddXmlPrologPref;
    private SwitchPreference mAddNewLineToEndPref;
    private SwitchPreference mUseLongEndOfTheLessonPref;
    private SwitchPreference mCreateBeautifulXmlPref;
    private TextView mXmlFilePreferenceCategory;
    private SwitchPreference mShowNotificationPref;
    private SwitchPreference mShowNotificationWhenLessonsOverPref;
    private MyApplication mApp;

    private void setXmlFileCategoryVisibility(int visibility) {
        mXmlFilePreferenceCategory.setVisibility(visibility);
        mAddXmlPrologPref         .setVisibility(visibility);
        mAddNewLineToEndPref      .setVisibility(visibility);
        mUseLongEndOfTheLessonPref.setVisibility(visibility);
        mCreateBeautifulXmlPref   .setVisibility(visibility);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mApp = (MyApplication) getApplication();
        AppCompatDelegate.setDefaultNightMode(mApp.isDarkTheme() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        Preference versionPref               = findViewById(R.id.version_pref);
        mDarkThemePref                       = findViewById(R.id.dark_theme_pref);
        mAddXmlPrologPref                    = findViewById(R.id.add_xml_prolog_pref);
        mAddNewLineToEndPref                 = findViewById(R.id.add_new_line_to_end_pref);
        mUseLongEndOfTheLessonPref           = findViewById(R.id.use_long_end_of_the_lesson_pref);
        mCreateBeautifulXmlPref              = findViewById(R.id.create_beautiful_xml_pref);
        mXmlFilePreferenceCategory           = findViewById(R.id.xml_file_category);
        mShowNotificationPref                = findViewById(R.id.show_notification_pref);
        mShowNotificationWhenLessonsOverPref = findViewById(R.id.show_notification_when_lessons_over_pref);
        Preference mResetSettingsPref        = findViewById(R.id.reset_settings_pref);

        setXmlFileCategoryVisibility(View.GONE);
        mShowNotificationPref.setDependencyByThis(mShowNotificationWhenLessonsOverPref);

        mDarkThemePref.setSwitched(mApp.isDarkTheme());
        mAddXmlPrologPref.setSwitched(mApp.isNeedAddXmlProlog());
        mAddNewLineToEndPref.setSwitched(mApp.isNeedAddNewLineToEndOfXml());
        mUseLongEndOfTheLessonPref.setSwitched(mApp.getUseLongXmlTagEnd());
        mCreateBeautifulXmlPref.setSwitched(mApp.isNeedCreateBeautifulXml());
        mShowNotificationPref.setSwitched(mApp.isNeedShowNotification());
        mShowNotificationWhenLessonsOverPref.setSwitched(mApp.isNeedShowNotificationWhenLessonsOver());

        mAddXmlPrologPref.setSummary(getString(R.string.increases_file_size, "40"));
        mAddNewLineToEndPref.setSummary(getString(R.string.increases_file_size_by_one_byte));
        mUseLongEndOfTheLessonPref.setSummary(getString(R.string.increases_lesson_size, "7"));
        mCreateBeautifulXmlPref.setSummary(
                getString(R.string.increases_file_week_and_lesson_size, "3", "10", "8"));

        versionPref.setOnClickListener(v -> {
            if (versionPrefClickedCount == 3) {
                setXmlFileCategoryVisibility(View.VISIBLE);
            }
            else {
                ++versionPrefClickedCount;
            }
        });
        mDarkThemePref.onStateChanged = newState -> {
            mApp.setDarkTheme(newState);
            startActivity(new Intent(getApplicationContext(), PreferencesActivity.class));
            finish();
        };
        mAddXmlPrologPref.onStateChanged = newState -> mApp.setNeedAddXmlProlog(newState);
        mAddNewLineToEndPref.onStateChanged = newState -> mApp.setNeedAddNewLineToEndOfXml(newState);
        mUseLongEndOfTheLessonPref.onStateChanged = newState -> mApp.setUseLongXmlTagEnd(newState);
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
            mAddXmlPrologPref                   .setSwitched(false);
            mAddNewLineToEndPref                .setSwitched(false);
            mUseLongEndOfTheLessonPref          .setSwitched(false);
            mCreateBeautifulXmlPref             .setSwitched(false);
            mShowNotificationPref               .setSwitched(true );
            mShowNotificationWhenLessonsOverPref.setSwitched(false);
        });
    }
}

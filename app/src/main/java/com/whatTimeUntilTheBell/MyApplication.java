package com.whatTimeUntilTheBell;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private byte activityCount = 0;
    private SettingsSharedPref sharedPref;
    private final SharedPreferences[] lessonsPrefs = new SharedPreferences[Day.values().length];
    @SuppressWarnings("unchecked")
    public ArrayList<Lesson>[] lessons = (ArrayList<Lesson>[]) Array.newInstance(ArrayList.class, Day.values().length);

    NotificationService notificationService;
    MainActivity mainActivity;

    private enum LessonKeys {
        begin,
        end,
        title
    }

    boolean isDarkTheme() {
        return sharedPref.isDarkTheme.get();
    }

    void setDarkTheme(boolean value) {
        sharedPref.isDarkTheme.set(value);
    }

    boolean isNeedCreateBeautifulXml() {
        return sharedPref.isNeedCreateBeautifulXml.get();
    }

    void setNeedCreateBeautifulXml(boolean value) {
        sharedPref.isNeedCreateBeautifulXml.set(value);
    }

    boolean isNeedShowNotification() {
        return sharedPref.isNeedShowNotification.get();
    }

    void setNeedShowNotification(boolean value) {
        sharedPref.isNeedShowNotification.set(value);
    }

    boolean isNeedShowNotificationWhenLessonsOver() {
        return sharedPref.isNeedShowNotificationWhenLessonsOver.get();
    }

    void setNeedShowNotificationWhenLessonsOver(boolean value) {
        sharedPref.isNeedShowNotificationWhenLessonsOver.set(value);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        for (int i = 0; i < Day.values().length; ++i) {
            lessons[i] = new ArrayList<>();
            lessonsPrefs[i] = getSharedPreferences(Day.fromInt(i).toString() + "Lessons", MODE_PRIVATE);
        }

        registerActivityLifecycleCallbacks(this);
        sharedPref = new SettingsSharedPref(getSharedPreferences("settings", MODE_PRIVATE),
            (getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES);
    }

    private String getLessonAccessString(int id, LessonKeys key) {
        return "Lesson" + id + key.toString();
    }

    private void setLessonData(SharedPreferences.Editor editor, int id, LessonKeys key,
                               String value) {
        editor.putString(getLessonAccessString(id, key), value);
    }

    private void setLessonData(SharedPreferences.Editor editor, int id, Lesson lesson) {
        setLessonData(editor, id, LessonKeys.begin, lesson.begin.toString());
        setLessonData(editor, id, LessonKeys.end,   lesson.end  .toString());
        setLessonData(editor, id, LessonKeys.title, lesson.title);
    }

    void loadLessonsData() {
        for (Day day : Day.values()) {
            Map<String, ?> pref = lessonsPrefs[day.ordinal()].getAll();
            int count = pref.values().size() / LessonKeys.values().length;

            if (count == 0) {
                continue;
            }

            lessons[day.ordinal()] = new ArrayList<>(count);
            for (int i = 0; i < count; ++i) {
                lessons[day.ordinal()].add(new Lesson(
                        new Time(pref.get(getLessonAccessString(i, LessonKeys.begin)).toString()),
                        new Time(pref.get(getLessonAccessString(i, LessonKeys.end  )).toString()),
                                 pref.get(getLessonAccessString(i, LessonKeys.title)).toString()
                ));
            }
        }
    }

    void saveLessons() {
        for (Day day : Day.values()) {
            ArrayList<Lesson> lessons = this.lessons[day.ordinal()];
            SharedPreferences.Editor editor = lessonsPrefs[day.ordinal()].edit();
            editor.clear();

            for (int i = 0; i < lessons.size(); ++i) {
                setLessonData(editor, i, lessons.get(i));
            }

            editor.apply();
        }
    }

    void saveLesson(int day, int id) {
        SharedPreferences.Editor editor = lessonsPrefs[day].edit();
        setLessonData(editor, id, lessons[day].get(id));
        editor.apply();
    }

    void saveLastLesson(int day) {
        saveLesson(day, lessonsPrefs[day].getAll().size() / LessonKeys.values().length);
    }

    private CharSequence getTimeText(int time, int idIfSingular, int idIfTtf, int idIfPlural) {
        if (time == 1) {
            return getText(idIfSingular);
        }
        if (time >= 2 && time <= 4) {
            return getText(idIfTtf);
        }
        return getText(idIfPlural);
    }

    private String timeToString(Time time) {
        String minutes = time.minutes != 0 ? time.minutes + " " + (
            time.minutes >= 10 && time.minutes <= 19 ? getText(R.string.minutes) :
                getTimeText(time.minutes % 10, R.string.minute, R.string.minutes_ttf, R.string.minutes)
        ) + " " : "";

        String seconds = time.seconds != 0 ? time.seconds + " " + (
            time.seconds >= 10 && time.seconds <= 19 ? getText(R.string.minutes) :
                getTimeText(time.seconds % 10, R.string.second, R.string.seconds_ttf, R.string.seconds)
        ) : "";

        return minutes + seconds;
    }

    public String[] whatsNext() {
        Time currentTime = new Time();

        for (Lesson lesson : lessons[Day.current()]) {
            if (currentTime.compareTo(lesson.begin) < 0) {
                return new String[]{
                        getString(R.string.time_until_the_bell_title, getText(R.string.lesson)),
                        timeToString(lesson.begin.minus(currentTime))
                };
            }
            if (currentTime.compareTo(lesson.end) < 0) {
                return new String[]{
                        getString(R.string.time_until_the_bell_title, getText(R.string._break)),
                        timeToString(lesson.end.minus(currentTime))
                };
            }
        }
        return new String[]{ getString(R.string.lessons_over), "" };
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activityCount == 0 && notificationService != null && !mainActivity.isStartingActivity()) {
            notificationService.stopTimer();
        }
        ++activityCount;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        --activityCount;
        if (activityCount == 0 && notificationService != null && !mainActivity.isStartingActivity()) {
            notificationService.startTimer();
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }
}

package com.whatTimeUntilTheBell;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    public static MyApplication instance;

    boolean isDarkTheme() {
        return sharedPref.isDarkTheme.get();
    }

    void setDarkTheme(boolean value) {
        sharedPref.isDarkTheme.set(value);
    }

    boolean isNeedAddXmlProlog() {
        return sharedPref.isNeedAddXmlProlog.get();
    }

    void setNeedAddXmlProlog(boolean value) {
        sharedPref.isNeedAddXmlProlog.set(value);
    }

    boolean isNeedAddNewLineToEndOfXml() {
        return sharedPref.isNeedAddNewLineToEndOfXml.get();
    }

    void setNeedAddNewLineToEndOfXml(boolean value) {
        sharedPref.isNeedAddNewLineToEndOfXml.set(value);
    }

    boolean getUseLongXmlTagEnd() {
        return sharedPref.useLongXmlTagEnd.get();
    }

    void setUseLongXmlTagEnd(boolean value) {
        sharedPref.useLongXmlTagEnd.set(value);
        Lesson.lessonXmlTagEnd = value ? Lesson.LONG_END : Lesson.SHORT_END;
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

    private enum LessonKeys {
        begin,
        end,
        title
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        long start = System.currentTimeMillis();
        for (int i = 0; i < Day.values().length; ++i) {
            lessons[i] = new ArrayList<>();
            lessonsPrefs[i] = getSharedPreferences(Day.fromInt(i).toString() + "Lessons", MODE_PRIVATE);
        }
        long end = System.currentTimeMillis() - start;
        Log.i("prefLoad", String.valueOf(end));

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
        setLessonData(editor, id, LessonKeys.begin, lesson.getBegin().toString());
        setLessonData(editor, id, LessonKeys.end,   lesson.getEnd()  .toString());
        setLessonData(editor, id, LessonKeys.title, lesson.getTitle());
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
        String minutes = time.getMinutes() != 0 ? time.getMinutes() + " " + (
            time.getMinutes() >= 10 && time.getMinutes() <= 19 ? getText(R.string.minutes) :
                getTimeText(time.getMinutes() % 10, R.string.minute, R.string.minutes_ttf, R.string.minutes)
        ) + " " : "";

        String seconds = time.getSeconds() != 0 ? time.getSeconds() + " " + (
            time.getSeconds() >= 10 && time.getSeconds() <= 19 ? getText(R.string.minutes) :
                getTimeText(time.getSeconds() % 10, R.string.second, R.string.seconds_ttf, R.string.seconds)
        ) : "";

        return minutes + seconds;
    }

    public String[] whatsNext() {
        Time currentTime = new Time();

        for (Lesson lesson : lessons[Day.current()]) {
            if (currentTime.compareTo(lesson.getBegin()) < 0) {
                return new String[]{
                        getString(R.string.until_plh_left, getText(R.string.lesson)),
                        timeToString(lesson.getBegin().minus(currentTime))
                };
            }
            if (currentTime.compareTo(lesson.getEnd()) < 0) {
                return new String[]{
                        getString(R.string.until_plh_left, getText(R.string._break)),
                        timeToString(lesson.getEnd().minus(currentTime))
                };
            }
        }
        return new String[]{ getString(R.string.lessons_over), "" };
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if (activityCount == 0 && notificationService != null && !mainActivity.isStartingActivity()) {
            notificationService.stopTimer();
        }
        ++activityCount;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        --activityCount;
        if (activityCount == 0 && notificationService != null && !mainActivity.isStartingActivity()) {
            notificationService.startTimer();
        }
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
    }
}

package com.whatTimeUntilTheBell;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private Timer timer;
    private MyApplication app;
    private final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), this::setDayText);

    private TextView dayTv;
    private ViewPager vp;
    private ActionButton fab;
    private TextView timeUntilTheTv;
    private TextView timeUntilTheBell;

    boolean mIsStartingActivity = false;

    public boolean isStartingActivity() {
        return mIsStartingActivity;
    }

    public ActionButton getActionButton() {
        return fab;
    }

    interface OnPageScrolledListener {
        void onPageScrolled(int position);
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {
        private final OnPageScrolledListener mOnPageScrolledListener;

        public ViewPagerAdapter(FragmentManager fm, OnPageScrolledListener onPageScrolled) {
            super(fm);
            mOnPageScrolledListener = onPageScrolled;
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public Fragment getItem(int position) {
            return ListViewFragment.newInstance(position);
        }

        @Override
        public int getItemPosition(@NonNull Object obj) {
            return POSITION_NONE;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mOnPageScrolledListener.onPageScrolled(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    private void setDayText(int day) {
        vpItem = day;
        var localizedDays = new String[]{
                getString(R.string.monday),
                getString(R.string.tuesday),
                getString(R.string.wednesday),
                getString(R.string.thursday),
                getString(R.string.friday),
                getString(R.string.saturday),
                getString(R.string.sunday),
        };
        dayTv.setText(localizedDays[day]);
    }

    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                showTime();
            }
        }, 0, 1000);
        showTime();
    }

    private void stopTimer() {
        timer.cancel();
    }

    private void showTime() {
        var array = app.whatsNext();

        runOnUiThread(() -> {
            timeUntilTheTv.setText(array[0]);
            timeUntilTheBell.setText(array[1]);
            if (array[1].isEmpty()) {
                timeUntilTheBell.setVisibility(View.GONE);
            }
            else {
                timeUntilTheBell.setVisibility(View.VISIBLE);
            }
        });
    }

    private String lessonsListToXml(ArrayList<Lesson> list) {
        var s = new StringBuilder();
        if (app.isNeedCreateBeautifulXml()) {
            for (var lesson : list) {
                s.append("        ").append(lesson.toXml()).append("\n");
            }
            s.deleteCharAt(s.length() - 1);
        }
        else {
            for (var lesson : list) {
                s.append(lesson.toXml());
            }
        }
        return s.toString();
    }

    private String getXmlLessons() {
        StringBuilder s = new StringBuilder();

        if (app.isNeedCreateBeautifulXml()) {
            for (var day : Day.values()) {
                var lessons = app.lessons[day.ordinal()];
                if (lessons.isEmpty()) {
                    continue;
                }

                s.append("    <").append(day.toString()).append(">\n")
                        .append(lessonsListToXml(lessons)).append("\n")
                        .append("    </").append(day.toString()).append(">\n");
            }
            s.deleteCharAt(s.length() - 1);
        }
        else {
            for (var day : Day.values()) {
                var lessons = app.lessons[day.ordinal()];
                if (lessons.isEmpty()) {
                    continue;
                }

                s.append("<").append(day).append(">").append(lessonsListToXml(lessons)).append("</").append(day).append(">");
            }
        }
        return s.toString();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (MyApplication) getApplication();
        app.mainActivity = this;
        AppCompatDelegate.setDefaultNightMode(app.isDarkTheme() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_main);

        dayTv = findViewById(R.id.day);
        vp = findViewById(R.id.vp);
        fab = findViewById(R.id.fab);
        timeUntilTheTv = findViewById(R.id.time_until_the_text);
        timeUntilTheBell = findViewById(R.id.time_until_the_bell);

        var lessons = app.lessons[Day.current()];
        if (lessons.size() == 0) {
            app.loadLessonsData();
            // update link
            lessons = app.lessons[Day.current()];

            // first startup
            if (lessons.size() == 0) {
                // save current theme to preferences
                app.setDarkTheme(app.isDarkTheme());
            }
        }

        setDayText(vpItem);
        vp.setAdapter(adapter);
        vp.addOnPageChangeListener(adapter);
        vp.setCurrentItem(vpItem);

        fab.setOnClickListener(v -> {
            int day = vp.getCurrentItem();
            var dialog = new ChangeLessonDialog();
            dialog.setLessonTitle(getString(R.string.lesson_num, String.valueOf(app.lessons[day].size() + 1)));
            dialog.setDeleteLessonButtonVisibility(View.GONE);
            dialog.onApply = lesson ->  {
                app.lessons[day].add(lesson);
                app.saveLastLesson(day);
                adapter.notifyDataSetChanged();
            };

            dialog.show(getFragmentManager(), ChangeLessonDialog.class.getSimpleName());
        });

        if (app.isNeedShowNotification()) {
            startService(new Intent(this, NotificationService.class));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTimer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.preferences) {
            mIsStartingActivity = true;
            startActivity(new Intent(this, PreferencesActivity.class));
            mIsStartingActivity = false;
            return true;
        }
        if (item.getItemId() == R.id.share) {
            var isEmpty = true;
            for (var day : app.lessons) {
                if (!day.isEmpty()) {
                    isEmpty = false;
                    break;
                }
            }
            if (isEmpty) {
                Toast.makeText(getApplicationContext(), getText(R.string.lessons_not_found),
                        Toast.LENGTH_SHORT).show();
                return true;
            }

            var shareIntent = new Intent(Intent.ACTION_SEND)
                    .setType("text/plain")
                    .putExtra(Intent.EXTRA_SUBJECT, "lessons");

            var message = "";
            if (app.isNeedAddXmlProlog()) {
                message += xmlProlog;
                if (app.isNeedCreateBeautifulXml()) {
                    message += "\n";
                }
            }

            message += "<Week>";
            message += app.isNeedCreateBeautifulXml() ? "\n" + getXmlLessons() + "\n" : getXmlLessons();
            message += "</Week>";
            if (app.isNeedAddNewLineToEndOfXml()) {
                message += "\n";
            }

            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(shareIntent, getText(R.string.share)));
            return true;
        }
        if (item.getItemId() == R.id.load) {
            var intent = new Intent(Intent.ACTION_GET_CONTENT).setType("text/plain");
            startActivityForResult(Intent.createChooser(intent, getText(R.string.select_file_manager)), 8777);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showWrongFileToast() {
        Toast.makeText(getApplicationContext(), getText(R.string.wrong_xml_file), Toast.LENGTH_SHORT).show();
    }

    private String readTextFile(InputStream inputStream) throws IOException {
        var outputStream = new ByteArrayOutputStream();
        var buf = new byte[1024];
        int len;
        while ((len = inputStream.read(buf)) != -1) {
            outputStream.write(buf, 0, len);
        }
        outputStream.close();
        inputStream.close();
        return outputStream.toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            loadLessonsFromFile(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLessonsFromFile(Intent data) throws Exception {
        var s = readTextFile(getContentResolver().openInputStream(data.getData()));

        var factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        var xpp = factory.newPullParser();

        xpp.setInput(new StringReader(s));
        xpp.next();
        if (!xpp.getName().equals("Week")) {
            showWrongFileToast();
            return;
        }
        xpp.next();

        ArrayList<Lesson>[] tempLessons = (ArrayList<Lesson>[]) Array.newInstance(ArrayList.class, Day.values().length);
        for (int i = 0; i < Day.values().length; ++i) {
            tempLessons[i] = new ArrayList<>();
        }

        var currentDay = 0;
        while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
            if (xpp.getName().equals("Lesson") && xpp.getEventType() == XmlPullParser.START_TAG) {
                tempLessons[currentDay].add(
                        new Lesson(
                                new Time(xpp.getAttributeValue(null, "begin")),
                                new Time(xpp.getAttributeValue(null, "end")),
                                xpp.getAttributeValue(null, "title")
                        )
                );
            }
            else if (xpp.getName().equals("Week")) {
                xpp.next();
                continue;
            }
            else if (xpp.getEventType() == XmlPullParser.START_TAG) {
                try {
                    currentDay = Day.valueOf(xpp.getName()).ordinal();
                } catch (IllegalArgumentException e) {
                    showWrongFileToast();
                    return;
                }
            }
            xpp.next();
        }

        System.arraycopy(tempLessons, 0, app.lessons, 0, tempLessons.length);
        app.saveLessons();
        adapter.notifyDataSetChanged();
    }

    private static final String xmlProlog = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
    private static int vpItem = Day.current();
}

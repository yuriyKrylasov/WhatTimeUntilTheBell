package com.whatTimeUntilTheBell;

import java.util.Calendar;

public class Time {
    private int mHours;
    private int mMinutes;
    private final int mSeconds;

    public Time() {
        Calendar calendar = Calendar.getInstance();
        mHours   = calendar.get(Calendar.HOUR_OF_DAY);
        mMinutes = calendar.get(Calendar.MINUTE);
        mSeconds = calendar.get(Calendar.SECOND);
    }

    public Time(int hours, int minutes) {
        this(hours, minutes, 0);
    }

    public Time(int hours, int minutes, int seconds) {
        mHours   = hours;
        mMinutes = minutes;
        mSeconds = seconds;
    }

    public Time(String time) {
        int index = time.indexOf(':');
        if (index > 0 && time.length() != index + 1) {
            mHours = Integer.parseInt(time.substring(0, index));
            mMinutes = Integer.parseInt(time.substring(index + 1));
        }
        else {
            mHours = 24;
            mMinutes = 0;
        }
        mSeconds = 0;
    }

    public int getHours() {
        return mHours;
    }

    public void setHours(int hours) {
        mHours = hours;
    }

    public int getMinutes() {
        return mMinutes;
    }

    public void setMinutes(int minutes) {
        mMinutes = minutes;
    }

    public int getSeconds() {
        return mSeconds;
    }

    public int compareTo(Time time) {
        return toSeconds() - time.toSeconds();
    }

    public Time minus(Time time) {
        int difference = compareTo(time);
        return new Time(difference / 3600, difference / 60, difference % 60);
    }

    private int toSeconds() {
        return mHours * 3600 + mMinutes * 60 + mSeconds;
    }

    @Override
    public String toString() {
        return mHours + ":" + (mMinutes < 10 ? "0" + mMinutes : mMinutes);
    }
}

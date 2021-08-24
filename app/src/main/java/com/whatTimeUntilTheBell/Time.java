package com.whatTimeUntilTheBell;

import java.util.Calendar;

public class Time {
    int hours;
    int minutes;
    final int seconds;

    public Time() {
        Calendar calendar = Calendar.getInstance();
        hours   = calendar.get(Calendar.HOUR_OF_DAY);
        minutes = calendar.get(Calendar.MINUTE);
        seconds = calendar.get(Calendar.SECOND);
    }

    public Time(int hours, int minutes) {
        this(hours, minutes, 0);
    }

    public Time(int hours, int minutes, int seconds) {
        this.hours   = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public Time(String time) {
        int index = time.indexOf(':');
        if (index > 0 && time.length() != index + 1) {
            hours = Integer.parseInt(time.substring(0, index));
            minutes = Integer.parseInt(time.substring(index + 1));
        }
        else {
            hours = 24;
            minutes = 0;
        }
        seconds = 0;
    }

    public int compareTo(Time time) {
        return toSeconds() - time.toSeconds();
    }

    public Time minus(Time time) {
        int difference = compareTo(time);
        return new Time(difference / 3600, difference / 60, difference % 60);
    }

    private int toSeconds() {
        return hours * 3600 + minutes * 60 + seconds;
    }

    @Override
    public String toString() {
        return hours + ":" + (minutes < 10 ? "0" + minutes : minutes);
    }
}

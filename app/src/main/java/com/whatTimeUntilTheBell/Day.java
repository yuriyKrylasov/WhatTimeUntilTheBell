package com.whatTimeUntilTheBell;

import java.util.Calendar;

enum Day {
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday,
    Sunday;

    static Day fromInt(int value) {
        for (Day day : values()) {
            if (day.ordinal() == value) {
                return day;
            }
        }
        return Monday;
    }

    static int current() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.SUNDAY) {
            return 6;
        }
        return day - 2;
    }
}

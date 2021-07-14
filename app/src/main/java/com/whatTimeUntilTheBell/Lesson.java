package com.whatTimeUntilTheBell;

import androidx.annotation.NonNull;

public class Lesson {
    private final Time mBegin;
    private final Time mEnd;
    private String mTitle;

    public final static String LONG_END = "></Lesson>";
    public final static String SHORT_END = " />";

    public static String lessonXmlTagEnd = SHORT_END;

    public Lesson(Time begin, Time end, String title) {
        mBegin = begin;
        mEnd = end;
        mTitle = title;
    }

    @NonNull
    @Override
    public String toString() {
        return mBegin + " - " + mEnd;
    }

    @NonNull
    public String toXml() {
        return "<Lesson begin=\"" + mBegin + "\" end=\"" + mEnd + "\" title=\"" + mTitle + "\"" + lessonXmlTagEnd;
    }

    public Time getBegin() {
        return mBegin;
    }

    public Time getEnd() {
        return mEnd;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}

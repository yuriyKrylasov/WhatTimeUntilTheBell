package com.whatTimeUntilTheBell;

class Lesson {
    final Time begin;
    final Time end;
    String title;

    Lesson(Time begin, Time end, String title) {
        this.begin = begin;
        this.end   = end;
        this.title = title;
    }

    @Override
    public String toString() {
        return begin + " - " + end;
    }

    String toXml() {
        return "<Lesson begin=\"" + begin + "\" end=\"" + end + "\" title=\"" + title + "\"/>";
    }
}

package com.whatTimeUntilTheBell;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

public class NotificationBuilder {
    private final Notification.Builder mBuilder;

    public NotificationBuilder(Context context, String channelId) {
        if (Build.VERSION.SDK_INT >= 26) {
            mBuilder = new Notification.Builder(context, channelId);
        }
        else {
            mBuilder = new Notification.Builder(context);
        }
    }

    public NotificationBuilder setShowWhen(boolean show) {
        if (Build.VERSION.SDK_INT >= 17) {
            mBuilder.setShowWhen(show);
        }
        return this;
    }

    public NotificationBuilder setAutoCancel(boolean autoCancel) {
        mBuilder.setAutoCancel(autoCancel);
        return this;
    }

    public NotificationBuilder setOngoing(boolean ongoing) {
        mBuilder.setOngoing(ongoing);
        return this;
    }

    public NotificationBuilder setSound(Uri sound) {
        mBuilder.setSound(sound);
        return this;
    }

    public NotificationBuilder setSmallIcon(int icon) {
        mBuilder.setSmallIcon(icon);
        return this;
    }

    public NotificationBuilder setContentIntent(PendingIntent intent) {
        mBuilder.setContentIntent(intent);
        return this;
    }

    public NotificationBuilder setContentTitle(CharSequence title) {
        mBuilder.setContentTitle(title);
        return this;
    }

    public NotificationBuilder setContentText(CharSequence text) {
        if (text.length() != 0) {
            mBuilder.setContentText(text);
        }
        return this;
    }

    public NotificationBuilder addAction(int icon, CharSequence title, PendingIntent intent) {
        if (Build.VERSION.SDK_INT >= 16) {
            mBuilder.addAction(icon, title, intent);
        }
        return this;
    }

    public NotificationBuilder setPriority(int pri) {
        if (Build.VERSION.SDK_INT >= 16) {
            mBuilder.setPriority(pri);
        }
        return this;
    }

    public Notification build() {
        if (Build.VERSION.SDK_INT >= 16) {
            return mBuilder.build();
        }
        else {
            return mBuilder.getNotification();
        }
    }
}
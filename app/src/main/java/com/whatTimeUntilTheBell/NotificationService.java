package com.whatTimeUntilTheBell;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {
    private static final int mNotificationId = 1;
    private String mChannelId;
    private Timer mTimer;
    private MyApplication mApp;
    private int mImmutableFlag = 0;

    private void sendNotification(String title, String text) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT | mImmutableFlag);

        Intent actionIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingActionIntent = PendingIntent.getBroadcast(this, 0,
                actionIntent, mImmutableFlag);

        NotificationCompat.Builder notificationBuilder =
            new NotificationCompat.Builder(getApplicationContext(), mChannelId)
                .setShowWhen(false)
                .setAutoCancel(true)
                .setOngoing(true)
                .setSound(null)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .addAction(0, getResources().getString(R.string.stop), pendingActionIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        if (text.isEmpty()) {
            notificationBuilder.setContentText(text);
        }

        startForeground(mNotificationId, notificationBuilder.build());
    }

    public void updateNotification() {
        if (mApp.lessons[Day.current()].size() == 0)
            mApp.loadLessonsData();

        String[] array = mApp.whatsNext();
        if (!mApp.isNeedShowNotificationWhenLessonsOver() && array[1].isEmpty()) {
            stopTimer();
            stopSelf();
        }
        sendNotification(array[0], array[1]);
    }

    public void startTimer() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateNotification();
            }
        }, 0, 1000);
        updateNotification();
    }

    public void stopTimer() {
        mTimer.cancel();
        stopForeground(true);
    }

    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            mImmutableFlag = PendingIntent.FLAG_IMMUTABLE;

        mTimer = new Timer();
        mApp = (MyApplication) getApplication();
        mApp.notificationService = this;

        mChannelId = getResources().getString(R.string.app_name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager)
                    getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(mChannelId, mChannelId,
                    NotificationManager.IMPORTANCE_LOW);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}

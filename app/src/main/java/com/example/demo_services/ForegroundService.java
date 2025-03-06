package com.example.demo_services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class ForegroundService extends Service {
    private static final String TAG = "ForegroundService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Start");
        startForeground(101, updateNotification());
        return START_STICKY;
    }

    private Notification updateNotification() {
        Context context = getApplicationContext();
        PendingIntent action = PendingIntent.getActivity(
                context,
                0,
                new Intent(context, MainActivity.class),
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "123456";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "ForegroundServiceDemo",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Alex channel description");
            manager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(this, channelId);
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        return builder.setContentIntent(action)
                .setContentTitle("ForegroundService")
                .setTicker("ForegroundService")
                .setContentText("ForegroundService Running")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentIntent(action)
                .setOngoing(true)
                .build();
    }
} 
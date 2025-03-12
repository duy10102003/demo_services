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

/**
 * ForegroundService là một loại Service chạy ở foreground, hiển thị notification cho người dùng.
 * Service này có độ ưu tiên cao hơn background service và ít bị hệ thống kill hơn.
 * Thích hợp cho các tác vụ người dùng cần biết đang chạy (VD: nghe nhạc, download file).
 */
public class ForegroundService extends Service {
    private static final String TAG = "ForegroundService";

    /**
     * Được gọi khi service được bind
     * ForegroundService thường không cần bind nên return null
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Được gọi mỗi lần service được start
     * Phải gọi startForeground trong vòng 5 giây sau khi service start
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Start");
        // ID thông báo phải khác 0 và unique trong app
        startForeground(101, updateNotification());
        return START_STICKY; // Service sẽ được restart sau khi bị kill
    }

    /**
     * Tạo và cập nhật notification cho foreground service
     * @return Notification đã được cấu hình
     */
    private Notification updateNotification() {
        Context context = getApplicationContext();

        // Tạo PendingIntent để mở app khi click vào notification
        PendingIntent action = PendingIntent.getActivity(
                context,
                0,
                new Intent(context, MainActivity.class),
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE // FLAG_IMMUTABLE bắt buộc từ Android 12
        );

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;

        // Từ Android 8.0 (API 26) trở lên bắt buộc phải có NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "123456";
            // Tạo notification channel với độ ưu tiên cao
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "ForegroundServiceDemo", // Tên channel hiển thị trong Settings
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Alex channel description");
            // Đăng ký channel với hệ thống
            manager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(this, channelId);
        } else {
            // Với Android 7.1 trở xuống không cần channel
            builder = new NotificationCompat.Builder(context);
        }

        // Cấu hình notification
        return builder.setContentIntent(action)           // Intent khi click vào notification
                .setContentTitle("ForegroundService")     // Tiêu đề notification
                .setTicker("ForegroundService")          // Text hiển thị khi notification mới xuất hiện
                .setContentText("ForegroundService Running") // Nội dung notification
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Icon nhỏ bắt buộc phải có
                .setContentIntent(action)                // Intent khi click
                .setOngoing(true)                       // Không cho phép user swipe to dismiss
                .build();
    }
} 
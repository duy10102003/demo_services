package com.example.demo_services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {
    private static final String TAG = "MyService";
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && "com.example.demo_services.CODE4FUNC".equals(intent.getAction())) {
                Toast.makeText(context, intent.getStringExtra("DEMO_MSG"),
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                Log.d(TAG, "Service is doing background work...");
                Toast.makeText(getApplicationContext(), "Service is working...", Toast.LENGTH_SHORT).show();
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Service created");
        Toast.makeText(this, "Service Created", Toast.LENGTH_SHORT).show();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.demo_services.CODE4FUNC");
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
        }

        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: Service started");
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();

        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Service destroyed");
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
} 
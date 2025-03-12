package com.example.demo_services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class MyIntentService extends IntentService {
    private static final String TAG = "MyIntentService";
    private static final String ACTION_FOO = "com.example.demo_services.action.FOO";
    private static final String ACTION_BAZ = "com.example.demo_services.action.BAZ";

    private static final String EXTRA_PARAM1 = "com.example.demo_services.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.demo_services.extra.PARAM2";

    private Handler handler = new Handler(Looper.getMainLooper());

    public MyIntentService() {
        super("MyIntentService");
    }

    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: IntentService created");
        handler.post(() ->Toast.makeText(this, "IntentService Created", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent: Thread name = " + Thread.currentThread().getName());
        handler.post(() ->Toast.makeText(this, "IntentService is handling work", Toast.LENGTH_SHORT).show());

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            }
        }
    }

    private void handleActionFoo(String param1, String param2) {
        Log.d(TAG, "handleActionFoo: START");
        handler.post(() ->Toast.makeText(this, "IntentService: handleActionFoo started", Toast.LENGTH_SHORT).show());
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "handleActionFoo: DONE");
        handler.post(() ->Toast.makeText(this, "IntentService: handleActionFoo completed", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: IntentService destroyed");
        handler.post(() ->Toast.makeText(this, "IntentService Destroyed", Toast.LENGTH_SHORT).show());
    }
}
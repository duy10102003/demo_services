package com.example.demo_services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Messenger mService = null;
    private boolean bound = false;

    private Button btnStartIntentService;
    private Button btnStartUnBoundService;
    private Button btnStopUnBoundService;
    private Button btnStartBoundService;
    private Button btnStopBoundService;
    private Button btnSendMsg;
    private Button btnStartForeground;

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Toast.makeText(getApplicationContext(), "onServiceConnected", Toast.LENGTH_SHORT).show();
            mService = new Messenger(service);
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            bound = false;
        }
    };

    @RequiresApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        btnStartIntentService = findViewById(R.id.btnStartIntentService);
        btnStartUnBoundService = findViewById(R.id.btnStartUnBoundService);
        btnStopUnBoundService = findViewById(R.id.btnStopUnBoundService);
        btnStartBoundService = findViewById(R.id.btnStartBoundService);
        btnStopBoundService = findViewById(R.id.btnStopBoundService);
        btnSendMsg = findViewById(R.id.btnSendMsg);
        btnStartForeground = findViewById(R.id.btnStartForeground);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void setupClickListeners() {
        btnStartIntentService.setOnClickListener(v -> 
            MyIntentService.startActionFoo(this, "", ""));

        btnStartUnBoundService.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyService.class);
            startService(intent);
        });

        btnStopUnBoundService.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyService.class);
            stopService(intent);
        });

        btnStartBoundService.setOnClickListener(v -> {
            Intent intent = new Intent(this, BoundService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        });

        btnStopBoundService.setOnClickListener(v -> {
            if (bound) {
                unbindService(mConnection);
                bound = false;
            }
        });

        btnSendMsg.setOnClickListener(v -> {
            Message msg = Message.obtain(null, BoundService.MSG_SAY_HELLO, 0, 0);
            try {
                if (mService != null) {
                    mService.send(msg);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        btnStartForeground.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForegroundService.class);
            startForegroundService(intent);
        });
    }
}
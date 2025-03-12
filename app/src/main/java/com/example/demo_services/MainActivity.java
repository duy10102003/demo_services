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
            Toast.makeText(getApplicationContext(), "Service Connected", Toast.LENGTH_SHORT).show();
            mService = new Messenger(service);
            bound = true;
            btnSendMsg.setEnabled(true);
        }


        //call khi ma connection service lost, crash app
        @Override
        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            bound = false;
            btnSendMsg.setEnabled(false);
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

        btnSendMsg.setEnabled(false);
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
       //Activity bind vào service thông qua bindService()
       //Service trả về một IBinder thông qua onBind()
       //Activity nhận được IBinder và có thể gửi message đến Service
        // Service xử lý message thông qua IncomingHandler
        btnStartBoundService.setOnClickListener(v -> {
            Intent intent = new Intent(this, BoundService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        });

        btnStopBoundService.setOnClickListener(v -> {
            if (bound) {
                unbindService(mConnection);
                bound = false;
                btnSendMsg.setEnabled(false);
                Toast.makeText(this, "Service Disconnected", Toast.LENGTH_SHORT).show();
            }
        });

        btnSendMsg.setOnClickListener(v -> {
            if (bound && mService != null) {
                try {
                    Message msg = Message.obtain(null, BoundService.MSG_SAY_HELLO, 0, 0);
                    mService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error sending message", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Service not bound", Toast.LENGTH_SHORT).show();
            }
        });

        //phai chay tren andorid 8.tro len de su dung startForegroundService
        btnStartForeground.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForegroundService.class);
            startForegroundService(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bound) {
            unbindService(mConnection);
            bound = false;
        }
    }
}
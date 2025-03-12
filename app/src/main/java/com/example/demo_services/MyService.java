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
         /*
            -> START_NOT_STICKY.

            Nếu hệ thống kill service khi giá trị này được trả về thì service này không được khởi
             động lại trừ khi có một Intent đang được chờ ở onStartCommand().
             Đây là lựa chọn an toàn nhất để tránh chạy Service khi không cần thiết
             và khi ứng dụng có thể khởi động lại một cách đơn giản các công việc chưa hoàn thành.

            -> START_STICKY.

            Khi giá trị này được trả về trong onStartCommand, nếu service bị hệ thống kill. Nếu onStartCommand không có một intent nào chờ nó nữa thì Service sẽ được hệ thống khởi động lại với một Intent null.

            -> START_REDELEVER_INTENT

            Nếu Service bị kill thì nó sẽ được khởi động lại với một Intent là Intent cuối cùng mà Service được nhận. Điều này thích hợp với các service đang thực hiện công việc muốn tiếp tục ngay tức thì như download fie chẳng hạn. Ngoài 3 giá trị trên thì trong onStartCommand() còn có thêm 2 giá trị trả về nữa là.

            -> START_STICKY_COMPATIBILITY

            Giá trị này cũng giống như START_STICKY nhưng nó không chắc chắn, đảm bảo khởi động lại service.

            -> DEFAULT. Là một sự lựa chọn giữa START_STICKY_COMPATIBILITY hoặc START_STICKY
        */
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
package com.example.demo_services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

/**
 * BoundService là một loại Service cho phép các component khác (như Activity) bind vào và tương tác với nó.
 * Bound khi một thành phần của ứng dụng ràng buộc với nó bởi lời gọi bindService().
 * Một Bound Service cung cấp một giao diện Client - Server cho phép các thành phần tương tác với nó:
 * gửi yêu cầu, nhận kết quả và thậm chí là IPC.
 * Service này sử dụng Messenger để thực hiện IPC (Inter-Process Communication).
 */
public class BoundService extends Service {
    // Định nghĩa các loại message mà service có thể xử lý
    public static final int MSG_SAY_HELLO = 1;

    // Messenger để giao tiếp với clients (Activities)
    private Messenger mMessenger;

    /**
     * Handler để xử lý các message từ client gửi đến
     * Static class để tránh memory leak
     */
    static class IncomingHandler extends Handler {
        // Context được giữ yếu (weak) để tránh memory leak
        private final Context applicationContext;

        IncomingHandler(Context context) {
            // Lưu application context thay vì activity context
            this.applicationContext = context.getApplicationContext();
        }

        @Override
        public void handleMessage(Message msg) {
            // Xử lý các loại message khác nhau dựa vào msg.what
            switch (msg.what) {
                case MSG_SAY_HELLO:
                    // Khi nhận được message hello, hiển thị toast
                    Toast.makeText(applicationContext, "hello!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    // Các message không xác định được xử lý bởi class cha
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Được gọi khi một component (như Activity) muốn bind vào service
     * @param intent Intent được sử dụng để bind service
     * @return IBinder interface để client có thể giao tiếp với service
     */
    @Override
    public IBinder onBind(Intent intent) {
        // Tạo messenger mới với handler để xử lý các incoming message
        mMessenger = new Messenger(new IncomingHandler(this));
        // Trả về binder để client có thể gửi messages
        return mMessenger.getBinder();
    }

    /**
     * Được gọi khi service bị destroy
     * Nơi để cleanup các resources
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "Bound Service Destroy", Toast.LENGTH_SHORT).show();
    }
} 
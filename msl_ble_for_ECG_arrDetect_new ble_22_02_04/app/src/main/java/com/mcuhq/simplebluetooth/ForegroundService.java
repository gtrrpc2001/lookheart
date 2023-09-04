package com.mcuhq.simplebluetooth;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.core.app.NotificationCompat;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ForegroundService extends Service {

    private Executor executor = Executors.newSingleThreadExecutor(); // 백그라운드에서 코드를 실행할 Executor
    private Handler handler = new Handler(Looper.getMainLooper());

    private volatile boolean isRunning = true; // 서비스가 실행 중인지 확인하는 플래그

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startBackgroundTask();

        initializeNotification(); // 포그라운드 생성
        return START_NOT_STICKY;
    }

    private void startBackgroundTask() {
        executor.execute(() -> {
            while (isRunning) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    // Thread interrupted
                }
            }
        });
    }

    public void initializeNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1");
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.bigText("실행중");
        style.setBigContentTitle(null);
        style.setSummaryText("LOOKHEART");
        builder.setContentText(null);
        builder.setContentTitle(null);
        builder.setOngoing(true);
        builder.setStyle(style);
        builder.setWhen(0);
        builder.setShowWhen(false);

        Intent notificationIntent = new Intent(this, HomeFragment.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(new NotificationChannel("1", "forgroundService", NotificationManager.IMPORTANCE_NONE));
        Notification notification = builder.build();

        startForeground(1, notification);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false; // 백그라운드 작업 중지
        stopSelf();
    }
}

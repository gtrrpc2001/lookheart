package com.mcuhq.simplebluetooth.fragment.home;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.fragment.home.HomeFragment;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ForegroundService extends Service {

    private final static String FOREGROUND_SERVICE = "LOOKHEART_FOREGROUND";
    private final static String FOREGROUND_NAME = "LOOKHEART_USER";

    private Executor executor = Executors.newSingleThreadExecutor(); // 백그라운드에서 코드를 실행할 Executor

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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, FOREGROUND_SERVICE);
        builder.setSmallIcon(R.mipmap.ic_msl_user_round);
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.bigText(getResources().getString(R.string.serviceRunning));
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
        manager.createNotificationChannel(new NotificationChannel(FOREGROUND_SERVICE, FOREGROUND_NAME, NotificationManager.IMPORTANCE_NONE));
        Notification notification = builder.build();

        startForeground(2017, notification);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
//            startForeground(2017, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION);
//        else
//            startForeground(2017, notification);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false; // 백그라운드 작업 중지
        stopSelf();
    }

}

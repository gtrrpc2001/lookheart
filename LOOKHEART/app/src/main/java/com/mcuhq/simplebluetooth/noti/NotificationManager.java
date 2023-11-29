package com.mcuhq.simplebluetooth.noti;


import android.app.NotificationChannel;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;

import com.mcuhq.simplebluetooth.R;

public class NotificationManager {
    private static final String PRIMARY_CHANNEL_ID = "basicNotification";
    private static final String CHANNEL_NAME = "LOOK_HEART_CHANNEL";
    private static final String PRIMARY_ARR_CHANNEL_ID = "arrNotification";
    private static final String ARR = "arr";
    private static final String BRADYCARDIA = "bradycardia";
    private static final String TACHYCARDIA = "tachycardia";
    private static final String ATRIAL_FIBRILLATION = "atrialFibrillation";
    private static final String MYO = "myo";
    private static final String NON_CONTACT = "nonContact";
    private static final String ARR_50 = "50";
    private static final String ARR_100 = "100";
    private static final String ARR_200 = "200";
    private static final String ARR_300 = "300";

    private final FragmentActivity fragmentActivity;
    private static NotificationManager instance;
    int basicSound = R.raw.basicsound;
    int arrSound = R.raw.arrsound;
    private android.app.NotificationManager notificationManager;
    private boolean notificationsPermissionCheck = false;

    private NotificationManager(FragmentActivity setFragmentActivity){
        fragmentActivity = setFragmentActivity;

        notificationsPermissionCheck = true;
        initBasicNotificationChannel();
        initArrNotificationChannel();
    }

    public static synchronized NotificationManager getInstance(FragmentActivity getFragmentActivity) {
        if (instance == null) {
            instance = new NotificationManager(getFragmentActivity);
        }
        return instance;
    }

    private void createNotificationChannel(String channelId, Uri soundUri) {
        // notification manager 생성
        if (notificationManager == null) {
            notificationManager = (android.app.NotificationManager) fragmentActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        NotificationChannel notificationChannel = new NotificationChannel(channelId, CHANNEL_NAME, notificationManager.IMPORTANCE_HIGH);

        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        notificationChannel.setDescription("Notification");
        notificationChannel.setSound(soundUri, new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build());
        notificationManager.createNotificationChannel(notificationChannel);
    }

    private void initBasicNotificationChannel() {
        createNotificationChannel(PRIMARY_CHANNEL_ID, Uri.parse("android.resource://" + fragmentActivity.getPackageName() + "/" + basicSound));
    }

    private void initArrNotificationChannel() {
        createNotificationChannel(PRIMARY_ARR_CHANNEL_ID, Uri.parse("android.resource://" + fragmentActivity.getPackageName() + "/" + arrSound));
    }

    public void sendNotification(String noti, int notificationId, String getCurrentTime){
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder(noti, getCurrentTime);
        notificationManager.notify(notificationId, notifyBuilder.build());
    }

    public void sendArrNotification(String noti, int notificationId, String getCurrentTime){
        NotificationCompat.Builder notifyBuilder = getArrNotificationBuilder(noti, getCurrentTime);
        notificationManager.notify(notificationId, notifyBuilder.build());
    }

    private NotificationCompat.Builder getNotificationBuilder(String noti, String currentTime) {
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(fragmentActivity, PRIMARY_CHANNEL_ID);

        switch (noti){
            case MYO:
                notifyBuilder.setContentTitle(fragmentActivity.getResources().getString(R.string.notiTypeMyo));
                break;
            case NON_CONTACT:
                notifyBuilder.setContentTitle(fragmentActivity.getResources().getString(R.string.notiTypeNonContact));
                break;
            default:
                return notifyBuilder;
        }

        notifyBuilder.setContentText(fragmentActivity.getResources().getString(R.string.notiTime) + currentTime);
        notifyBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        return notifyBuilder;
    }

    private NotificationCompat.Builder getArrNotificationBuilder(String noti, String currentTime) {
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(fragmentActivity, PRIMARY_ARR_CHANNEL_ID);

        switch (noti){
            case ARR:
                notifyBuilder.setContentTitle(fragmentActivity.getResources().getString(R.string.notiTypeArr));
                break;
            case BRADYCARDIA:
                notifyBuilder.setContentTitle(fragmentActivity.getResources().getString(R.string.notiTypeSlowArr));
                break;
            case TACHYCARDIA:
                notifyBuilder.setContentTitle(fragmentActivity.getResources().getString(R.string.notiTypeFastArr));
                break;
            case ATRIAL_FIBRILLATION:
                notifyBuilder.setContentTitle(fragmentActivity.getResources().getString(R.string.notiTypeHeavyArr));
                break;
            case ARR_50:
                notifyBuilder.setContentTitle(fragmentActivity.getResources().getString(R.string.arrCnt50));
                notifyBuilder.setContentText(fragmentActivity.getResources().getString(R.string.arrCnt50Text));
                notifyBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
                return notifyBuilder;
            case ARR_100:
                notifyBuilder.setContentTitle(fragmentActivity.getResources().getString(R.string.arrCnt100));
                notifyBuilder.setContentText(fragmentActivity.getResources().getString(R.string.arrCnt100Text));
                notifyBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
                return notifyBuilder;
            case ARR_200:
                notifyBuilder.setContentTitle(fragmentActivity.getResources().getString(R.string.arrCnt200));
                notifyBuilder.setContentText(fragmentActivity.getResources().getString(R.string.arrCnt200Text));
                notifyBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
                return notifyBuilder;
            case ARR_300:
                notifyBuilder.setContentTitle(fragmentActivity.getResources().getString(R.string.arrCnt300));
                notifyBuilder.setContentText(fragmentActivity.getResources().getString(R.string.arrCnt300Text));
                notifyBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
                return notifyBuilder;
            default:
                return notifyBuilder;
        }

        notifyBuilder.setContentText(fragmentActivity.getResources().getString(R.string.notiTime) + currentTime);
        notifyBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        return notifyBuilder;
    }

    public boolean getNotiPermissionCheck(){
        return notificationsPermissionCheck;
    }
}

package com.mcuhq.simplebluetooth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;

public class BatteryOptimizationUtil {

    public static boolean isIgnoringBatteryOptimizations(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (powerManager == null) {
            return false; // PowerManager를 가져오지 못함
        }

        return powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
    }

    private static void checkBatteryOptimizations(Context context) {
        PowerManager pm = (PowerManager) context
                .getApplicationContext()
                .getSystemService(Context.POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(context.getPackageName())) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
    }

    public static void showBatteryOptimizationDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.setBatTitle));
        builder.setMessage(context.getResources().getString(R.string.setBatMessage));

        builder.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkBatteryOptimizations(context);
            }
        });

        builder.setNegativeButton(context.getResources().getString(R.string.rejectLogout), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 사용자가 취소를 선택한 경우의 처리
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}

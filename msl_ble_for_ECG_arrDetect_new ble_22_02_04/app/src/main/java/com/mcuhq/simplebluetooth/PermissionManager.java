package com.mcuhq.simplebluetooth;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PermissionManager {

    private final static int PERMISSION_MAX = 2;
    private final static int NOTIFICATIONS_REQUEST_CODE = 3000;
    private final static int ACCESS_BACKGROUND_LOCATION_REQUEST_CODE = 3001;
    private static PermissionManager instance;
    private Fragment fragment = null;
    private Context context;
    private Activity activity = null;
    private List<String> permissions;
    private String[] PERMISSIONS;
    private ActivityResultLauncher<String[]> multiplePermissionLauncher;
    private int permissionRequestCount;
    private PermissionCallback permissionCallback; // 콜백 인스턴스 변수
    private Boolean backgroundLocationFlag = false;

    public interface PermissionCallback {
        void onPermissionGranted();
        void onPermissionDenied(List<String> grantedPermissions, List<String> deniedPermissions);
        void setGuardianFlag();
    }

    public void setPermissionCallback(PermissionCallback permissionCallback) {
        this.permissionCallback = permissionCallback;
    }


    public static PermissionManager getInstance(Fragment fragment, Activity myActivity, PermissionCallback permissionCallback, Context context, String email) {
        if (instance == null) {
            instance = new PermissionManager(fragment, myActivity, permissionCallback, context, email);
        }
        return instance;
    }

    // fragment
    public PermissionManager(Fragment myFragment, Activity myActivity, PermissionCallback permissionCallback, Context myContext, String email) {
        this.activity = myActivity;
        this.fragment = myFragment;
        this.context = myContext;
        this.permissions = new ArrayList<>();
        this.permissionRequestCount = SharedPrefManager.getInstance(context, email).permissionCheck();

        setPermissionCallback(permissionCallback);
        initializePermissions();
        initializePermissionLauncher();
    }

    // activity
    public PermissionManager(Activity activity) {
        this.activity = activity;

        requestPostNotificationsPermission();
    }

    private void initializePermissions() {
        // 필요 권한 추가
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN);
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
        }

        if (Build.VERSION.SDK_INT >= 33) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS);
        }

        PERMISSIONS = permissions.toArray(new String[0]);
    }

    private void initializePermissionLauncher() {

        // 사용자 권한 응답에 대한 처리
        multiplePermissionLauncher = fragment.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
            List<String> grantedPermissions = new ArrayList<>();
            List<String> deniedPermissions = new ArrayList<>();

            if (isGranted.containsValue(false)) {
                // 하나 이상의 권한 거부
                Toast.makeText(fragment.getContext(), context.getResources().getString(R.string.permissionToast), Toast.LENGTH_SHORT).show();

                for (Map.Entry<String, Boolean> entry : isGranted.entrySet()) {
                    if (!entry.getValue()) { // 거부된 권한
                        deniedPermissions.add(entry.getKey());
                        Log.d("PERMISSIONS", "Permission denied: " + entry.getKey());
                    }else { // 승인된 권한
                        grantedPermissions.add(entry.getKey());
                        Log.d("PERMISSIONS", "Permission granted: " + entry.getKey());
                    }
                }
            } else {
                // 모든 권한이 승인
                Log.d("PERMISSIONS", "Permission All granted");
                for (Map.Entry<String, Boolean> entry : isGranted.entrySet()) {
                    if (entry.getValue()) {
                        Log.d("PERMISSIONS", "Permission granted: " + entry.getKey());
                    }
                }
                showPermissionDialog();
            }

            if (!deniedPermissions.isEmpty()) { // Callback
                if (permissionCallback != null) { // 하나 이상의 권한 거부
                    permissionCallback.onPermissionDenied(grantedPermissions, deniedPermissions);
                }
            } else {
                if (permissionCallback != null) { // 모든 권한 승인
                    permissionCallback.onPermissionGranted();
                }
            }

        });
    }

    public Boolean checkAndRequestPermissions(String email) {

        if (!hasPermissions(PERMISSIONS)) {
        // 일부 권한 또는 모든 권한 거부
            if (permissionRequestCount < PERMISSION_MAX) {
                multiplePermissionLauncher.launch(PERMISSIONS);
                permissionRequestCount++;
                SharedPrefManager.getInstance(context, email).setPermissionCnt(permissionRequestCount);
                return false;
            } else {
                showSettingsAlert();
                return false;
            }
        } else {
            // 모든 권한 승인
            Log.d("RequestPermissions", "true");
            return true;
        }
    }

    private boolean hasPermissions(String[] permissions) {
        if (fragment.isAdded() && fragment.getContext() != null) { // 프래그먼트 상태 확인
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(fragment.getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permission denied", permission);
                    return false;
                } else {
                    Log.d("permission granted", permission);
                }
            }
            return true;
        } else {
            Log.e("PermissionManager", "Fragment not added or context is null.");
            return false; // 프래그먼트가 비활성 상태이거나 Context가 null인 경우
        }
    }

    private void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(fragment.getContext());
        alertDialog.setTitle(context.getResources().getString(R.string.requiredPermission));
        alertDialog.setMessage(context.getResources().getString(R.string.requiredPermissionList));
        alertDialog.setPositiveButton(context.getResources().getString(R.string.goToSettings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", fragment.getActivity().getPackageName(), null);
                intent.setData(uri);
                fragment.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton(context.getResources().getString(R.string.rejectLogout), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void requestPostNotificationsPermission(){
        // Notification 권한 확인
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            // 권한 요청
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATIONS_REQUEST_CODE);
        }
    }

    // Android API 30 and above requires setting the backgroundPermission directly
    private void requestBackgroundPermission() {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                ACCESS_BACKGROUND_LOCATION_REQUEST_CODE);
    }

    // Background permission request dialog
    public void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.locationPermission));
        builder.setMessage(context.getResources().getString(R.string.locationPermissionMessage));

        DialogInterface.OnClickListener listener = (dialog, which) -> {
            backgroundLocationFlag = true;
            if (which == DialogInterface.BUTTON_POSITIVE) {
                requestBackgroundPermission();
            }
        };

        DialogInterface.OnClickListener guardianFlag = (dialog, which) -> {
            permissionCallback.setGuardianFlag();
        };

        builder.setPositiveButton(context.getResources().getString(R.string.ok), listener);
        builder.setNegativeButton(context.getResources().getString(R.string.rejectLogout), guardianFlag);

        builder.show();
    }

    public void setFlag() {
        backgroundLocationFlag = false;
    }
    public Boolean getFlag() {
        return backgroundLocationFlag;
    }

    public static void reset() {
        instance = null; // 싱글턴 인스턴스 해제
    }
}
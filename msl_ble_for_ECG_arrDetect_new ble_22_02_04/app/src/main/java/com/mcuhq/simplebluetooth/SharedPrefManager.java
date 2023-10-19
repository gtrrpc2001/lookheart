package com.mcuhq.simplebluetooth;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String IS_FIRST_TIME = "permission_check";

    private SharedPreferences sharedPreferences;

    // 싱글턴 인스턴스
    private static SharedPrefManager instance;

    // Context를 전달받아 SharedPreferences 인스턴스를 얻는 private 생성자
    private SharedPrefManager(Context context, String sharedPrefName) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
    }

    // 클래스의 싱글턴 인스턴스를 반환하는 public 메서드
    public static synchronized SharedPrefManager getInstance(Context context, String sharedPrefName) {
        if (instance == null) {
            instance = new SharedPrefManager(context, sharedPrefName);
        }
        return instance;
    }

    public void setPermissionCnt(int count) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(IS_FIRST_TIME, count);
        editor.apply();
    }

    public int permissionCheck() {
        return sharedPreferences.getInt(IS_FIRST_TIME, 0);
    }

}
package com.mcuhq.simplebluetooth;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServerManager {

    public interface ServerTaskCallback {
        void onSuccess(Boolean result);
        void onFailure(Exception e);
    }

    public void signupTask(String email, String pw, String name, String gender, String height, String weight, String age, String birthday, ServerTaskCallback callback) {

        new Thread(() -> {
            try {

                Map<String, Object> mapParam = new HashMap<>();
                mapParam.put("kind", "checkReg");
                mapParam.put("아이디", email);
                mapParam.put("패스워드키", pw);
                mapParam.put("성명", name);
                mapParam.put("이메일", email);
                mapParam.put("핸드폰", "01012345678");
                mapParam.put("성별", gender);
                mapParam.put("신장", height);
                mapParam.put("몸무게", weight);
                mapParam.put("나이", age);
                mapParam.put("생년월일", birthday);
                mapParam.put("설정_수면시작", "23");
                mapParam.put("설정_수면종료", "7");
                mapParam.put("설정_활동BPM", "90");
                mapParam.put("설정_일걸음", "3000");
                mapParam.put("설정_일거리", "5"); // km
                mapParam.put("설정_일활동칼로리", "500");
                mapParam.put("설정_일칼로리", "3000");
                mapParam.put("알림_sms", "0");
                mapParam.put("시간차이", "0");

                // API 호출
                getAPIMsg(mapParam, callback);

            } catch (Exception e) {
            }
        }).start();
    }

    public void loginTask(String email, String pw, ServerTaskCallback callback) {

        new Thread(() -> {
            try {

                Map<String, Object> mapParam = new HashMap<>();
                mapParam.put("kind", "checkLogin");
                mapParam.put("아이디", email);
                mapParam.put("패스워드키", pw);

                // API 호출
                getAPIMsg(mapParam, callback);

            } catch (Exception e) {
            }
        }).start();
    }

    public void checkIdTask(String email, ServerTaskCallback callback) {

        new Thread(() -> {
            try {

                Map<String, Object> mapParam = new HashMap<>();
                mapParam.put("kind", "checkIDDupe");
                mapParam.put("아이디", email);

                // API 호출
                getAPIMsg(mapParam, callback);

            } catch (Exception e) {
            }
        }).start();
    }


    public void setProfileTask(String email, String pw, String name, String gender, String height, String weight, int age, String birthday, ServerTaskCallback callback) {

        new Thread(() -> {
            try {

                Map<String, Object> mapParam = new HashMap<>();
                mapParam.put("kind", "setProfile");
                mapParam.put("아이디", email);
                mapParam.put("성명", name);
                mapParam.put("성별", gender);
                mapParam.put("신장", height);
                mapParam.put("몸무게", weight);
                mapParam.put("나이", age);
                mapParam.put("생년월일", birthday);

                // API 호출
                getAPIMsg(mapParam, callback);

            } catch (Exception e) {
            }
        }).start();
    }

    public void getAPIMsg(Map<String, Object> mapParam, ServerTaskCallback callback) {
        String sr = "";
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(false)
                    .build();
//            String url = "http://db.medsyslab.co.kr:40080/lunar/msl/api_getdatasetparam.php";
            String url = "http://121.153.127.222:40081/msl/api_getdata";
//            String url = "http://121.153.127.222:40081/mslbpm/api_getdata";

            FormBody.Builder formBuilder = new FormBody.Builder();
            if (mapParam != null && mapParam.isEmpty() == false && mapParam.size() > 0) { // [널 아님]

                // [map 데이터 key, value 확인]
                Set set = mapParam.keySet();
                Iterator iterator = set.iterator();
                while (iterator.hasNext()) {

                    String key = (String) iterator.next();
                    String value = String.valueOf(mapParam.get(key));

                    formBuilder.add(key, String.valueOf(value));
                }
            }

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", "application/json;")
                    .addHeader("Cache-Control", "no-cache")
                    .post(formBuilder.build())
                    .build();

            Response response = null;

            try {
                response = client.newCall(request).execute();
                sr = response.body().string();
                Log.e("result", sr);

                boolean resultCheck = sr.toLowerCase().contains("true");

                callback.onSuccess(resultCheck); // 콜백 호출

            } catch (IOException e) {
                e.printStackTrace();
                callback.onFailure(e);  // 콜백 호출
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
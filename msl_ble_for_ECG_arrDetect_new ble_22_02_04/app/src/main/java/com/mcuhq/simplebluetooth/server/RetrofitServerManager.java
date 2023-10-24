package com.mcuhq.simplebluetooth.server;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitServerManager {

//    private static final String BASE_URL = "http://121.152.22.85:40081/"; // TEST
    private static final String BASE_URL = "http://121.152.22.85:40080/"; // Real Address

    private static RetrofitServerManager instance;
    private static RetrofitService apiService;

    public static synchronized RetrofitServerManager getInstance() {
        if ( instance == null )
            instance = new RetrofitServerManager();
        return instance;
    }

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();

    private void initializeApiService() {
        if (apiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(RetrofitService.class);
        }
    }

    public interface APICallback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    public interface ServerTaskCallback {
        void onSuccess(String result);
        void onFailure(Exception e);
    }

    public interface UserDataCallback {
        void userData(UserProfile userProfile);
        void onFailure(Exception e);
    }


    public void updatePWD(String email, String pw, ServerTaskCallback callback) {

        new Thread(() -> {
            try {

                Map<String, Object> mapParam = new HashMap<>();
                mapParam.put("kind", "updatePWD");
//                mapParam.put("아이디", email);
//                mapParam.put("패스워드키", pw);
                mapParam.put("eq", email);
                mapParam.put("password", pw);
                // API 호출
                updatePWDFromAPI(mapParam, callback);

            } catch (Exception e) {
            }
        }).start();
    }

    public void updatePWDFromAPI(Map<String, Object> data, ServerTaskCallback callback) {
        initializeApiService();

        Call<String> call = apiService.updatePWD(data);
        executeCall(call, new APICallback<String>() {
            @Override
            public void onSuccess(String result) {
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void findID(String name, String phoneNumber, String birthday, ServerTaskCallback callback) {
        try {

            Map<String, Object> mapParam = new HashMap<>();
            mapParam.put("name", name);
            mapParam.put("phoneNumber", phoneNumber);
            mapParam.put("birthday", birthday);

            // API 호출
            findIDFromAPIMap(mapParam, callback);

        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    public void findIDFromAPIMap(Map<String, Object> data, ServerTaskCallback callback) {

        initializeApiService();

        Call<String> call = apiService.findID(data.get("name").toString(), data.get("phoneNumber").toString(), data.get("birthday").toString());
        executeCall(call, new APICallback<String>() {
            @Override
            public void onSuccess(String result) {
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void setGuardian(String email, String timezone, String writetime, ArrayList<String> phone, ServerTaskCallback callback) {

        try {

            Map<String, Object> mapParam = new HashMap<>();
            mapParam.put("eq", email);
            mapParam.put("timezone", timezone);
            mapParam.put("writetime", writetime);
            mapParam.put("phones", phone);

            // API 호출
            setGuardianFromAPI(mapParam, callback);

        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    public void setGuardianFromAPI(Map<String, Object> data, ServerTaskCallback callback) {

        initializeApiService();

        Call<String> call = apiService.setGuardian(data);
        executeCall(call, new APICallback<String>() {
            @Override
            public void onSuccess(String result) {
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }



    public void loginTask(String email, String pw, ServerTaskCallback callback) {

        try {

            Map<String, Object> mapParam = new HashMap<>();
            mapParam.put("eq", email);
            mapParam.put("password", pw);

            // API 호출
            loginTaskFromAPI(mapParam, callback);

        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    public void loginTaskFromAPI(Map<String, Object> loginData, ServerTaskCallback callback) {

        initializeApiService();

        Call<String> call = apiService.checkLogin(loginData.get("eq").toString(), loginData.get("password").toString());

        executeCall(call, new APICallback<String>() {
            @Override
            public void onSuccess(String result) {
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }



    public void checkIdTask(String email, ServerTaskCallback callback) {

        try {
            Map<String, Object> mapParam = new HashMap<>();
            mapParam.put("kind", "checkIDDupe");
            mapParam.put("eq", email);

            // API 호출
            checkIDFromAPI(mapParam, callback);

        } catch (Exception e) {
            callback.onFailure(e);
        }

    }

    public void checkIDFromAPI(Map<String, Object> loginData, ServerTaskCallback callback) {

        initializeApiService();

        Log.e("loginData", loginData.get("eq").toString());
        Call<String> call = apiService.checkID(loginData.get("eq").toString());
        executeCall(call, new APICallback<String>() {
            @Override
            public void onSuccess(String result) {
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }


    public void setSignupData(String email, String pw, String name, String gender, String height, String weight, String age, String birthday, ServerTaskCallback callback) {
        try {
            Map<String, Object> mapParam = new HashMap<>();

            // TEST
//            mapParam.put("kind", "checkReg");
//            mapParam.put("아이디", email);
//            mapParam.put("패스워드키", pw);
//            mapParam.put("성명", name);
//            mapParam.put("이메일", email);
//            mapParam.put("핸드폰", "01012345678");
//            mapParam.put("성별", gender);
//            mapParam.put("신장", height);
//            mapParam.put("몸무게", weight);
//            mapParam.put("나이", age);
//            mapParam.put("생년월일", birthday);
//            mapParam.put("설정_수면시작", "23");
//            mapParam.put("설정_수면종료", "7");
//            mapParam.put("설정_활동BPM", "90");
//            mapParam.put("설정_일걸음", "3000");
//            mapParam.put("설정_일거리", "5");
//            mapParam.put("설정_일활동칼로리", "500");
//            mapParam.put("설정_일칼로리", "3000");
//            mapParam.put("알림_sms", "0");
//            mapParam.put("시간차이", "0");

            mapParam.put("kind", "checkReg");
            mapParam.put("eq", email);
            mapParam.put("password", pw);
            mapParam.put("eqname", name);
            mapParam.put("email", email);
            mapParam.put("phone", "01012345678");
            mapParam.put("sex", gender);
            mapParam.put("height", height);
            mapParam.put("weight", weight);
            mapParam.put("age", age);
            mapParam.put("birth", birthday);
            mapParam.put("sleeptime", "23");
            mapParam.put("uptime", "7");
            mapParam.put("bpm", "90");
            mapParam.put("step", "3000");
            mapParam.put("distanceKM", "5");
            mapParam.put("calexe", "500");
            mapParam.put("cal", "3000");
            mapParam.put("alarm_sms", "0");
            mapParam.put("differtime", "0");

            // API 호출
            setProfileFromAPI(mapParam, callback);

        } catch (Exception e) {
            callback.onFailure(e);
        }
    }
    public void setProfile(String email, String name, String number, String gender, String height, String weight, String age, String birthday, String sleep, String wakeup,
                           String targetBpm, String targetStep, String targetDistance, String targetCal, String targetECal, ServerTaskCallback callback) {
        try {
            Map<String, Object> mapParam = new HashMap<>();
            mapParam.put("kind", "setProfile");

            // TEST
//            mapParam.put("아이디", email);
//            mapParam.put("성명", name);
//            mapParam.put("이메일", email);
//            mapParam.put("핸드폰", number);
//            mapParam.put("성별", gender);
//            mapParam.put("신장", height);
//            mapParam.put("몸무게", weight);
//            mapParam.put("나이", age);
//            mapParam.put("생년월일", birthday);
//            mapParam.put("설정_수면시작", sleep);
//            mapParam.put("설정_수면종료", wakeup);
//            mapParam.put("설정_활동BPM", targetBpm);
//            mapParam.put("설정_일걸음", targetStep);
//            mapParam.put("설정_일거리", targetDistance);
//            mapParam.put("설정_일활동칼로리", targetECal);
//            mapParam.put("설정_일칼로리", targetCal);
//            mapParam.put("알림_sms", "0");
//            mapParam.put("시간차이", "0");

            mapParam.put("eq", email);
            mapParam.put("eqname", name);
            mapParam.put("email", email);
            mapParam.put("phone", "01012345678");
            mapParam.put("sex", gender);
            mapParam.put("height", height);
            mapParam.put("weight", weight);
            mapParam.put("age", age);
            mapParam.put("birth", birthday);
            mapParam.put("sleeptime", "23");
            mapParam.put("uptime", "7");
            mapParam.put("bpm", "90");
            mapParam.put("step", "3000");
            mapParam.put("distanceKM", "5");
            mapParam.put("calexe", "500");
            mapParam.put("cal", "3000");
            mapParam.put("alarm_sms", "0");
            mapParam.put("differtime", "0");

            // API 호출
            setProfileFromAPI(mapParam, callback);

        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    public void setProfileFromAPI(Map<String, Object> userData, ServerTaskCallback callback) {

        initializeApiService();

        Call<String> call = apiService.setProfile(userData);

        executeCall(call, new APICallback<String>() {
            @Override
            public void onSuccess(String result) {
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void getProfile(String email, UserDataCallback callback) {

        try {
            Map<String, Object> mapParam = new HashMap<>();
            mapParam.put("eq", email);

            // API 호출
            getProfileFromAPI(mapParam, callback);

        } catch (Exception e) {
            callback.onFailure(e);
        }
    }


    public void getProfileFromAPI(Map<String, Object> mapParam, UserDataCallback callback) {

        initializeApiService();

        Call<List<UserProfile>> call = apiService.getProfileData(mapParam.get("eq").toString());

        executeCall(call, new APICallback<List<UserProfile>>() {
            @Override
            public void onSuccess(List<UserProfile> result) {
                try {
                    if (!result.isEmpty()) {
                        UserProfileManager.getInstance().setUserProfile(result.get(0));
                        callback.userData(UserProfileManager.getInstance().getUserProfile());  // 콜백 호출
                    }
                }catch (Exception ignored) {
                    callback.onFailure(ignored);
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }


    public void sendTenSecondData(String email, String timezone, String writeTime, int bpm, double temp, int hrv, int step, double distance, double tCal, double eCal, int arrCnt, ServerTaskCallback callback) {
        try {

            Map<String, Object> mapParam = new HashMap<>();
            mapParam.put("kind", "BpmDataInsert");
            mapParam.put("eq", email);
            mapParam.put("timezone", timezone);
            mapParam.put("writetime", writeTime);
            mapParam.put("bpm", bpm);
            mapParam.put("hrv", hrv);
            mapParam.put("cal", tCal);
            mapParam.put("calexe", eCal);
            mapParam.put("step", step);
            mapParam.put("distanceKM", distance);
            mapParam.put("arrcnt", arrCnt);
            mapParam.put("temp",temp);

            sendTenSecondDataToServer(mapParam, callback); // API 호출

        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    public void sendTenSecondDataToServer(Map<String, Object> data, ServerTaskCallback callback) {

        initializeApiService();

        Call<String> call = apiService.sendTenSecondData(data);
        executeCall(call, new APICallback<String>() {
            @Override
            public void onSuccess(String result) {
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void sendHourlyData(String email, String dataYear, String dataMonth, String dataDay, String dataHour, String timezone, String step, String distance, String cal, String eCal, String arrCnt, ServerTaskCallback callback) {

        try {
            Map<String, Object> mapParam = new HashMap<>();
            mapParam.put("kind", "calandInsert");
            mapParam.put("eq", email);
            mapParam.put("datayear", dataYear);
            mapParam.put("datamonth", dataMonth);
            mapParam.put("dataday", dataDay);
            mapParam.put("datahour", dataHour);
            mapParam.put("ecgtimezone", timezone);
            mapParam.put("step", step);
            mapParam.put("distanceKM", distance);
            mapParam.put("cal", cal);
            mapParam.put("calexe", eCal);
            mapParam.put("arrcnt", arrCnt);

            sendHourlyDataToServer(mapParam, callback); // API 호출

        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    public void sendHourlyDataToServer(Map<String, Object> data, ServerTaskCallback callback) {

        initializeApiService();

        Call<String> call = apiService.sendHourlyData(data);
        executeCall(call, new APICallback<String>() {
            @Override
            public void onSuccess(String result) {
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }



    public void sendEcgData(String email, String writeTime, String timezone, int bpm, StringBuilder ecgList, ServerTaskCallback callback) {

        String stringEcgList = ecgList.toString();

        try {

            Map<String, Object> mapParam = new HashMap<>();
            mapParam.put("kind", "ecgdataInsert");
            mapParam.put("eq", email);
            mapParam.put("writetime", writeTime);
            mapParam.put("ecgtimezone", timezone);
            mapParam.put("bpm", bpm);
            mapParam.put("ecgPacket", stringEcgList);

            // API 호출
            sendEcgDataToServer(mapParam, callback);

        } catch (Exception e) {
        }

    }

    public void sendEcgDataToServer(Map<String, Object> data, ServerTaskCallback callback) {

        initializeApiService();

        Call<String> call = apiService.sendEcgData(data);
        executeCall(call, new APICallback<String>() {
            @Override
            public void onSuccess(String result) {
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }




    public void sendArrData(String email, String timezone, String writeTime, String arrData, String arrStatus, ServerTaskCallback callback) {

        try {

            Map<String, Object> mapParam = new HashMap<>();
            mapParam.put("kind", "arrEcgInsert");
            mapParam.put("eq", email);
            mapParam.put("timezone", timezone);
            mapParam.put("writetime", writeTime);
            mapParam.put("ecgPacket", arrData);
            mapParam.put("arrStatus", arrStatus);

            // API 호출
            sendArrDataToServer(mapParam, callback);

        } catch (Exception e) {
            callback.onFailure(e);
        }
    }


    public void sendArrDataToServer(Map<String, Object> data, ServerTaskCallback callback) {

        initializeApiService();

        Call<String> call = apiService.sendArrData(data);
        executeCall(call, new APICallback<String>() {
            @Override
            public void onSuccess(String result) {
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }



    public void sendEmergencyData(String email, String timezone, String writeTime, String address, ServerTaskCallback callback) {

        try {

            Map<String, Object> mapParam = new HashMap<>();
            mapParam.put("kind", "arrEcgInsert");
            mapParam.put("eq", email);
            mapParam.put("timezone", timezone);
            mapParam.put("writetime", writeTime);
            mapParam.put("ecgPacket", "");
            mapParam.put("arrStatus", "");
            mapParam.put("bodystate", "1");
            mapParam.put("address", address);

            // API 호출
            sendEmergencyDataToServer(mapParam, callback);

        } catch (Exception e) {
            callback.onFailure(e);
        }
    }


    public void sendEmergencyDataToServer(Map<String, Object> data, ServerTaskCallback callback) {

        initializeApiService();

        Call<String> call = apiService.sendArrData(data);
        executeCall(call, new APICallback<String>() {
            @Override
            public void onSuccess(String result) {
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }


    private <T> void executeCall(Call<T> call, APICallback<T> callback) {
        call.enqueue(new retrofit2.Callback<T>() {
            @Override
            public void onResponse(Call<T> call, retrofit2.Response<T> response) {
                if (response.isSuccessful() && response.body() != null) {
//                    Gson gson = new Gson();
//                    String jsonBody = gson.toJson(response.body());
//                    Log.e("callback", jsonBody);

                    callback.onSuccess(response.body());
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.e("errorBody", errorBody);
                    callback.onFailure(new Exception("API call not successful"));
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                callback.onFailure(new Exception(t));
            }
        });
    }
}
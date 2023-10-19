package com.mcuhq.simplebluetooth.server;

import com.google.gson.annotations.SerializedName;

public class UserProfile {
    // Gson 응답 파싱
    @SerializedName("아이디")
    private String id;

    @SerializedName("성명")
    private String name;

    @SerializedName("이메일")
    private String email;

    @SerializedName("핸드폰")
    private String phone;

    @SerializedName("성별")
    private String gender;

    @SerializedName("신장")
    private String height;

    @SerializedName("몸무게")
    private String weight;

    @SerializedName("나이")
    private String age;

    @SerializedName("생년월일")
    private String birthday;

    @SerializedName("가입일")
    private String joinDate;

    @SerializedName("설정_수면시작")
    private String sleepStart;

    @SerializedName("설정_수면종료")
    private String sleepEnd;

    @SerializedName("설정_활동BPM")
    private String activityBPM;

    @SerializedName("설정_일걸음")
    private String dailyStep;

    @SerializedName("설정_일거리")
    private String dailyDistance;

    @SerializedName("설정_일활동칼로리")
    private String dailyActivityCalorie;

    @SerializedName("설정_일칼로리")
    private String dailyCalorie;

    @SerializedName("알림_sms")
    private String smsNotification;

    @SerializedName("시간차이")
    private String timeDifference;

    @SerializedName("phone")
    private String guardian;

//    @SerializedName("eq")
//    private String id;
//
//    @SerializedName("eqname")
//    private String name;
//
//    @SerializedName("email")
//    private String email;
//
//    @SerializedName("phone")
//    private String phone;
//
//    @SerializedName("sex")
//    private String gender;
//
//    @SerializedName("height")
//    private String height;
//
//    @SerializedName("weight")
//    private String weight;
//
//    @SerializedName("age")
//    private String age;
//
//    @SerializedName("birth")
//    private String birthday;
//
//    @SerializedName("signupdate")
//    private String joinDate;
//
//    @SerializedName("sleeptime")
//    private String sleepStart;
//
//    @SerializedName("uptime")
//    private String sleepEnd;
//
//    @SerializedName("bpm")
//    private String activityBPM;
//
//    @SerializedName("step")
//    private String dailyStep;
//
//    @SerializedName("distanceKM")
//    private String dailyDistance;
//
//    @SerializedName("calexe")
//    private String dailyActivityCalorie;
//
//    @SerializedName("cal")
//    private String dailyCalorie;
//
//    @SerializedName("alarm_sms")
//    private String smsNotification;
//
//    @SerializedName("differtime")
//    private String timeDifference;

    // getters

    public String getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public String getPhone(){
        return phone;
    }

    public String getGender(){
        return gender;
    }

    public String getHeight(){
        return height;
    }

    public String getWeight(){
        return weight;
    }

    public String getAge(){
        return age;
    }

    public String getBirthday(){
        return birthday;
    }

    public String getJoinDate(){
        return joinDate;
    }

    public String getSleepStart(){
        return sleepStart;
    }

    public String getSleepEnd(){
        return sleepEnd;
    }
    public String getActivityBPM(){
        return activityBPM;
    }
    public String getDailyStep(){
        return dailyStep;
    }
    public String getDailyDistance(){
        return dailyDistance;
    }
    public String getDailyCalorie(){
        return dailyActivityCalorie;
    }
    public String getDailyActivityCalorie(){
        return dailyCalorie;
    }
    public String getGuardian(){
        return guardian;
    }

    public void setGuardian(String phone){
        guardian = phone;
    }
}

package com.mcuhq.simplebluetooth.server;

import com.google.gson.annotations.SerializedName;

public class UserProfile {
    // Gson 응답 파싱
    @SerializedName("eq")
    private String id;

    @SerializedName("eqname")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("userphone")
    private String phone;

    @SerializedName("sex")
    private String gender;

    @SerializedName("height")
    private String height;

    @SerializedName("weight")
    private String weight;

    @SerializedName("age")
    private String age;

    @SerializedName("birth")
    private String birthday;

    @SerializedName("signupdate")
    private String joinDate;

    @SerializedName("sleeptime")
    private String sleepStart;

    @SerializedName("uptime")
    private String sleepEnd;

    @SerializedName("bpm")
    private String activityBPM;

    @SerializedName("step")
    private String dailyStep;

    @SerializedName("distanceKM")
    private String dailyDistance;

    @SerializedName("calexe")
    private String dailyActivityCalorie;

    @SerializedName("cal")
    private String dailyCalorie;

    @SerializedName("alarm_sms")
    private String smsNotification;

    @SerializedName("differtime")
    private String timeDifference;

    @SerializedName("phone")
    private String guardian;

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
}

package com.mcuhq.simplebluetooth.server;

public class UserProfileManager {

    private static UserProfileManager instance;

    private UserProfile userProfile;

    public static UserProfileManager getInstance() {
        if (instance == null) {
            instance = new UserProfileManager();
        }
        return instance;
    }

    public UserProfile getUserProfile(){
        return userProfile;
    }

    public void setUserProfile(UserProfile myUserProfile) {
        userProfile = myUserProfile;
    }
}

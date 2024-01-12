package com.mcuhq.simplebluetooth.profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mcuhq.simplebluetooth.auth.Find_Pw;
import com.mcuhq.simplebluetooth.fragment.ProfileFragment;
import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.server.RetrofitServerManager;
import com.library.lookheartLibrary.viewmodel.SharedViewModel;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class Profile_3 extends Fragment {

    private SharedViewModel viewModel;
    View view;

    private SharedPreferences userDetailsSharedPref;
    private SharedPreferences.Editor userDetailsEditor;

    // -------------------------- Button TAG--------------------------
    // region
    private final static int EMERGENCY_ON = 1, EMERGENCY_OFF = 2;
    private final static int NON_CONTACT_ON = 3, NON_CONTACT_OFF = 4;
    private final static int MYO_ON = 5, MYO_OFF = 6;
    private final static int ARR_ON = 7, ARR_OFF = 8;
    private final static int FAST_ARR_ON = 9, FAST_ARR_OFF = 10;
    private final static int SLOW_ARR_ON = 11, SLOW_ARR_OFF = 12;
    private final static int HEAVY_ARR_ON = 13, HEAVY_ARR_OFF = 14;
    private final static int HOURLY_ARR_ON = 15, HOURLY_ARR_OFF = 16;
    private final static int TOTAL_ARR_ON = 17, TOTAL_ARR_OFF = 18;
    // endRegion

    // -------------------------- Notification Flag--------------------------
    // region
    private boolean emergencyFlag = false;
    private boolean arrFlag = false;
    private boolean myoFlag = false;
    private boolean nonContactFlag = false;
    private boolean fastArrFlag = false;
    private boolean slowArrFlag = false;
    private boolean heavyArrFlag = false;
    private boolean hourlyArrFlag = false;
    private boolean totalArrFlag = false;
    // endRegion

    // -------------------------- UI var --------------------------
    // region
    private int buttonNormalColor;
    private int buttonPressColor;

    private Button emergency_off;
    private Button emergency_on;

    private Button arr_off;
    private Button arr_on;

    private Button hourlyArr_off;
    private Button hourlyArr_on;

    private Button totalArr_off;
    private Button totalArr_on;

    private Button fastArr_off;
    private Button fastArr_on;

    private Button slowArr_off;
    private Button slowArr_on;

    private Button heavyArr_off;
    private Button heavyArr_on;

    private Button myo_off;
    private Button myo_on;

    private Button nonContact_off;
    private Button nonContact_on;

    private Button logoutButton, changePasswordButton;
    // endRegion

    public Profile_3() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_profile3, container, false);

        init();

        addFindViewById();

        setButtonsColor();

        setButtonEvent();

        return view;
    }


    private void addFindViewById() {
        emergency_off = view.findViewById(R.id.profile3_emergency_off);
        emergency_on = view.findViewById(R.id.profile3_emergency_on);

        arr_off = view.findViewById(R.id.profile3_arr_off);
        arr_on = view.findViewById(R.id.profile3_arr_on);

        hourlyArr_off = view.findViewById(R.id.hourlyArr_off);
        hourlyArr_on = view.findViewById(R.id.hourlyArr_on);

        totalArr_off = view.findViewById(R.id.totalArr_off);
        totalArr_on = view.findViewById(R.id.totalArr_on);

        fastArr_off = view.findViewById(R.id.profile3_fastarr_off);
        fastArr_on = view.findViewById(R.id.profile3_fastarr_on);

        slowArr_off = view.findViewById(R.id.profile3_slowarr_off);
        slowArr_on = view.findViewById(R.id.profile3_slowarr_on);

        heavyArr_off = view.findViewById(R.id.profile3_irregular_off);
        heavyArr_on = view.findViewById(R.id.profile3_irregular_on);

        myo_off = view.findViewById(R.id.profile3_muscle_off);
        myo_on = view.findViewById(R.id.profile3_muscle_on);

        nonContact_off = view.findViewById(R.id.profile3_drop_off);
        nonContact_on = view.findViewById(R.id.profile3_drop_on);

        changePasswordButton = view.findViewById(R.id.profile_changepw);
        logoutButton = view.findViewById(R.id.profile_logout_btn);

    }

    private void init() {
        // Email
        SharedPreferences emailSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("User", Context.MODE_PRIVATE);
        String email = emailSharedPreferences.getString("email", "null");

        // SharedPreferences
        userDetailsSharedPref = getActivity().getSharedPreferences(email, Context.MODE_PRIVATE);
        userDetailsEditor = userDetailsSharedPref.edit();

        // ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // button Color
        buttonNormalColor = ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.buttonNormalColor);
        buttonPressColor = R.drawable.profile_button_press;

        // Notification Flag
        emergencyFlag = userDetailsSharedPref.getBoolean("HeartAttackFlag", true);
        nonContactFlag = userDetailsSharedPref.getBoolean("NonContactFlag", true);
        myoFlag = userDetailsSharedPref.getBoolean("MyoFlag", false);
        arrFlag = userDetailsSharedPref.getBoolean("ArrFlag", false);
        fastArrFlag = userDetailsSharedPref.getBoolean("FastArrFlag", false);
        slowArrFlag = userDetailsSharedPref.getBoolean("SlowArrFlag", false);
        heavyArrFlag = userDetailsSharedPref.getBoolean("HeavyArrFlag", false);
        hourlyArrFlag = userDetailsSharedPref.getBoolean("HourlyArrFlag", false);
        totalArrFlag = userDetailsSharedPref.getBoolean("TotalArrFlag", false);

    }
    private void setButtonEvent(){
        setOnClickListener(emergency_off, emergency_on, EMERGENCY_ON);
        setOnClickListener(nonContact_off, nonContact_on, NON_CONTACT_ON);
        setOnClickListener(myo_off, myo_on, MYO_ON);
        setOnClickListener(arr_off, arr_on, ARR_ON);
        setOnClickListener(fastArr_off, fastArr_on, FAST_ARR_ON);
        setOnClickListener(slowArr_off, slowArr_on, SLOW_ARR_ON);
        setOnClickListener(heavyArr_off, heavyArr_on, HEAVY_ARR_ON);
        setOnClickListener(hourlyArr_off, hourlyArr_on, HOURLY_ARR_ON);
        setOnClickListener(totalArr_off, totalArr_on, TOTAL_ARR_ON);

        // change Password
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Find_Pw.class);
                // 액티비티로 이동
                startActivity(intent);
            }
        });

        // Logout
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutAlert();
            }
        });

    }

    private void showLogoutAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.logout))
                .setMessage(getResources().getString(R.string.logoutHelp))
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences autoLoginSP = getActivity().getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = autoLoginSP.edit();

                        Boolean autoLoginCheck = autoLoginSP.getBoolean("autologin", false);
                        if (autoLoginCheck) {
                            editor = autoLoginSP.edit();
                            editor.putBoolean("autologin", false);
                            editor.apply();
                        }

                        getActivity().finish();

                    }
                })
                .setNegativeButton(getResources().getString(R.string.rejectLogout), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        Dialog dialog = builder.create();
        dialog.show();
    }

    private void setOnClickListener(Button offButton, Button onButton, int tag) {
        onButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonColor(offButton, onButton, true);
                saveSetting(tag);
            }
        });

        offButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonColor(offButton, onButton, false);
                saveSetting(tag + 1);
            }
        });
    }

    private void saveSetting(int tag) {
        switch (tag) {
            case EMERGENCY_ON:
            case EMERGENCY_OFF:
                emergencyFlag = tag == EMERGENCY_ON;
                userDetailsEditor.putBoolean("HeartAttackFlag", emergencyFlag);
                viewModel.setEmergency(emergencyFlag);
                break;

            case NON_CONTACT_ON:
            case NON_CONTACT_OFF:
                nonContactFlag = tag == NON_CONTACT_ON;
                userDetailsEditor.putBoolean("NonContactFlag", nonContactFlag);
                viewModel.setNonContact(nonContactFlag);
                break;

            case MYO_ON:
            case MYO_OFF:
                myoFlag = tag == MYO_ON;
                userDetailsEditor.putBoolean("MyoFlag", myoFlag);
                viewModel.setMyo(myoFlag);
                break;

            case ARR_ON:
            case ARR_OFF:
                arrFlag = tag == ARR_ON;
                userDetailsEditor.putBoolean("ArrFlag", arrFlag);
                viewModel.setArr(arrFlag);
                break;

            case FAST_ARR_ON:
            case FAST_ARR_OFF:
                fastArrFlag = tag == FAST_ARR_ON;
                userDetailsEditor.putBoolean("FastArrFlag", fastArrFlag);
                viewModel.setFastarr(fastArrFlag);
                break;

            case SLOW_ARR_ON:
            case SLOW_ARR_OFF:
                slowArrFlag = tag == SLOW_ARR_ON;
                userDetailsEditor.putBoolean("SlowArrFlag", slowArrFlag);
                viewModel.setSlowarr(slowArrFlag);
                break;

            case HEAVY_ARR_ON:
            case HEAVY_ARR_OFF:
                heavyArrFlag = tag == HEAVY_ARR_ON;
                userDetailsEditor.putBoolean("HeavyArrFlag", heavyArrFlag);
                viewModel.setIrregular(heavyArrFlag);
                break;

            case HOURLY_ARR_ON:
            case HOURLY_ARR_OFF:
                hourlyArrFlag = tag == HOURLY_ARR_ON;
                userDetailsEditor.putBoolean("HourlyArrFlag", hourlyArrFlag);
                viewModel.setHourlyArr(hourlyArrFlag);
                break;

            case TOTAL_ARR_ON:
            case TOTAL_ARR_OFF:
                totalArrFlag = tag == TOTAL_ARR_ON;
                userDetailsEditor.putBoolean("TotalArrFlag", totalArrFlag);
                viewModel.setTotalArr(totalArrFlag);
                break;

            default:
                break;
        }

        userDetailsEditor.apply();

    }
    private void setButtonsColor() {
        setButtonColor(emergency_off, emergency_on, emergencyFlag);
        setButtonColor(arr_off, arr_on, arrFlag);
        setButtonColor(hourlyArr_off, hourlyArr_on, hourlyArrFlag);
        setButtonColor(totalArr_off, totalArr_on, totalArrFlag);
        setButtonColor(fastArr_off, fastArr_on, fastArrFlag);
        setButtonColor(slowArr_off, slowArr_on, slowArrFlag);
        setButtonColor(heavyArr_off, heavyArr_on, heavyArrFlag);
        setButtonColor(myo_off, myo_on, myoFlag);
        setButtonColor(nonContact_off, nonContact_on, nonContactFlag);
    }

    private void setButtonColor(Button offButton, Button onButton, Boolean flag) {
        if (flag) {
            onButton.setBackgroundResource(buttonPressColor);
            onButton.setTextColor(Color.WHITE);
            offButton.setBackgroundColor(buttonNormalColor);
            offButton.setTextColor(Color.BLACK);
        } else {
            offButton.setBackgroundResource(buttonPressColor);
            offButton.setTextColor(Color.WHITE);
            onButton.setBackgroundColor(buttonNormalColor);
            onButton.setTextColor(Color.BLACK);
        }
    }
}

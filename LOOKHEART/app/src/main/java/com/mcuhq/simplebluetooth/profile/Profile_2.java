package com.mcuhq.simplebluetooth.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.server.RetrofitServerManager;
import com.library.lookheartLibrary.viewmodel.SharedViewModel;
import com.library.lookheartLibrary.server.UserProfileManager;
import com.library.lookheartLibrary.server.UserProfile;
public class Profile_2 extends Fragment {

    private final static int BPM_PLUS = 0, BPM_MINUS = 1;
    private final static int STEP_PLUS = 2, STEP_MINUS = 3;
    private final static int DISTANCE_PLUS = 4, DISTANCE_MINUS = 5;
    private final static int ACTIVITY_CAL_PLUS = 6, ACTIVITY_CAL_MINUS = 7;
    private final static int TOTAL_CAL_PLUS = 8, TOTAL_CAL_MINUS = 9;

    private View view;
    private SharedViewModel viewModel;
    private RetrofitServerManager retrofitServerManager;
    private UserProfile userProfile;
    private SharedPreferences userDetailsSharedPref;
    private SharedPreferences.Editor userDetailsEditor;

    private Button profile2_bpm_m,profile2_bpm_p,profile2_step_m,profile2_step_p,
            profile2_distance_m,profile2_distance_p,profile2_ecal_m,profile2_ecal_p,
            profile2_cal_m,profile2_cal_p,profile2_save;

    private EditText profile2_bpm,profile2_step,profile2_distance,profile2_ecal,profile2_cal;

    private String bpm = "90", step = "2000", distance = "5", eCal = "500", cal = "3000";

    private ScrollView sv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_profile2, container, false);

        init();

        findViewById();

        setUI();

        setButton();

        //입력할때 키보드에 대한 높이조절
        profile2_bpm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                KeyboardUp(100);
            }

        });
        profile2_step.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                KeyboardUp(300);
            }

        });
        profile2_distance.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                KeyboardUp(300);
            }

        });
        profile2_ecal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                KeyboardUp(500);
            }

        });
        profile2_cal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                KeyboardUp(500);
            }

        });


        return view;
    }

    private void setButton() {
        setOnClickListener(profile2_bpm_p, profile2_bpm_m, BPM_PLUS);
        setOnClickListener(profile2_step_p, profile2_step_m, STEP_PLUS);
        setOnClickListener(profile2_distance_p, profile2_distance_m, DISTANCE_PLUS);
        setOnClickListener(profile2_ecal_p, profile2_ecal_m, ACTIVITY_CAL_PLUS);
        setOnClickListener(profile2_cal_p, profile2_cal_m, TOTAL_CAL_PLUS);

        // save button
        profile2_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bpm = profile2_bpm.getText().toString();
                step = profile2_step.getText().toString();
                distance = profile2_distance.getText().toString();
                eCal = profile2_ecal.getText().toString();
                cal = profile2_cal.getText().toString();

                saveProfileData();
                setViewModel();

            }
        });
    }

    private void setOnClickListener(Button plusButton, Button minusButton, int tag) {
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonEvent(tag);
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonEvent(tag + 1);
            }
        });
    }

    private void buttonEvent(int tag) {
        switch (tag) {
            case BPM_PLUS:
            case BPM_MINUS:
                bpm = tag == BPM_PLUS ? String.valueOf(Integer.parseInt(bpm) + 1) : String.valueOf(Integer.parseInt(bpm) - 1);
                profile2_bpm.setText(bpm);
                break;

            case STEP_PLUS:
            case STEP_MINUS:
                step = tag == STEP_PLUS ? String.valueOf(Integer.parseInt(step) + 1) : String.valueOf(Integer.parseInt(step) - 1);
                profile2_step.setText(step);
                break;

            case DISTANCE_PLUS:
            case DISTANCE_MINUS:
                distance = tag == DISTANCE_PLUS ? String.valueOf(Integer.parseInt(distance) + 1) : String.valueOf(Integer.parseInt(distance) - 1);
                profile2_distance.setText(distance);
                break;

            case ACTIVITY_CAL_PLUS:
            case ACTIVITY_CAL_MINUS:
                eCal = tag == ACTIVITY_CAL_PLUS ? String.valueOf(Integer.parseInt(eCal) + 1) : String.valueOf(Integer.parseInt(eCal) - 1);
                profile2_ecal.setText(eCal);
                break;

            case TOTAL_CAL_PLUS:
            case TOTAL_CAL_MINUS:
                cal = tag == TOTAL_CAL_PLUS ? String.valueOf(Integer.parseInt(cal) + 1) : String.valueOf(Integer.parseInt(cal) - 1);
                profile2_cal.setText(cal);
                break;

            default:
                break;
        }
    }

    private void init() {
        retrofitServerManager = RetrofitServerManager.getInstance();
        userProfile = UserProfileManager.getInstance().getUserProfile();

        String email = userProfile.getEmail();
        userDetailsSharedPref = getActivity().getSharedPreferences(email, Context.MODE_PRIVATE);
        userDetailsEditor = userDetailsSharedPref.edit();

        bpm = userProfile.getActivityBPM();
        step = userProfile.getDailyStep();
        distance = userProfile.getDailyDistance();
        eCal = userProfile.getDailyActivityCalorie();
        cal = userProfile.getDailyCalorie();
    }

    private void setUI() {

        profile2_bpm.setText(bpm);
        profile2_step.setText(step);
        profile2_distance.setText(distance);
        profile2_ecal.setText(eCal);
        profile2_cal.setText(cal);

    }

    private void saveProfileData(){

        userProfile.setActivityBPM(bpm);
        userProfile.setDailyStep(step);
        userProfile.setDailyDistance(distance);
        userProfile.setDailyActivityCalorie(eCal);
        userProfile.setDailyCalorie(cal);

        try {
            retrofitServerManager.setProfile(userProfile, new RetrofitServerManager.ServerTaskCallback() {
                        @Override
                        public void onSuccess(String result) {
                            Log.i("save",result);
                            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), getResources().getString(R.string.saveData), Toast.LENGTH_SHORT).show());

                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("setProfile","setProfile");
                            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), getResources().getString(R.string.failSaveData), Toast.LENGTH_SHORT).show());
                            e.printStackTrace();
                        }
                    });

        }catch (Exception e){

        }
    }

    private void setViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.setBpm(bpm);
        viewModel.setDistance(distance);
        viewModel.setStep(step);
        viewModel.setECalText(eCal);
        viewModel.setTCalText(cal);
    }

    private void findViewById() {
        sv = view.findViewById(R.id.scrollView2);

        profile2_bpm_m = view.findViewById(R.id.profile2_bpm_m);
        profile2_bpm_p = view.findViewById(R.id.profile2_bpm_p);
        profile2_step_m = view.findViewById(R.id.profile2_step_m);
        profile2_step_p = view.findViewById(R.id.profile2_step_p);
        profile2_distance_m = view.findViewById(R.id.profile2_distance_m);
        profile2_distance_p = view.findViewById(R.id.profile2_distance_p);
        profile2_ecal_m = view.findViewById(R.id.profile2_ecal_m);
        profile2_ecal_p = view.findViewById(R.id.profile2_ecal_p);
        profile2_cal_m = view.findViewById(R.id.profile2_cal_m);
        profile2_cal_p = view.findViewById(R.id.profile2_cal_p);
        profile2_save = view.findViewById(R.id.profile2_save);

        profile2_bpm = view.findViewById(R.id.profile2_bpm);
        profile2_step = view.findViewById(R.id.profile2_step);
        profile2_distance = view.findViewById(R.id.profile2_distance);
        profile2_ecal = view.findViewById(R.id.profile2_ecal);
        profile2_cal = view.findViewById(R.id.profile2_cal);
    }

    private void KeyboardUp(int size) {
        sv.postDelayed(new Runnable() {
            @Override
            public void run() {
                sv.smoothScrollTo(0, size);
            }
        }, 200);
    }
}

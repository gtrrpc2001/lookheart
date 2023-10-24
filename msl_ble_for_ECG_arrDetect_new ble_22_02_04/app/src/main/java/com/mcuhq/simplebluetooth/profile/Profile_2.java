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
import com.mcuhq.simplebluetooth.SharedViewModel;

public class Profile_2 extends Fragment {

    SharedViewModel viewModel;

    RetrofitServerManager retrofitServerManager;

    private SharedPreferences userDetailsSharedPref;
    private SharedPreferences.Editor userDetailsEditor;

    private Button profile2_bpm_m,profile2_bpm_p,profile2_step_m,profile2_step_p,
            profile2_distance_m,profile2_distance_p,profile2_ecal_m,profile2_ecal_p,
            profile2_cal_m,profile2_cal_p,profile2_save;

    private EditText profile2_bpm,profile2_step,profile2_distance,profile2_ecal,profile2_cal;

    private int bpm = 90, step = 2000, distance = 5, ecal = 500, cal = 3000;
    private boolean profile2check = false;
    private String email;

    ScrollView sv;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile2, container, false);

        sv = view.findViewById(R.id.scrollView2);
        retrofitServerManager = RetrofitServerManager.getInstance();

        SharedPreferences emailSharedPreferences = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        email = emailSharedPreferences.getString("email", "null");

        userDetailsSharedPref = getActivity().getSharedPreferences(email, Context.MODE_PRIVATE);
        userDetailsEditor = userDetailsSharedPref.edit();

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

        Bundle args = getArguments();

        //SharedPreferences에서 개인정보 불러오기
        String savedText1 = userDetailsSharedPref.getString("o_bpm", String.valueOf(bpm));
        String savedText2 = userDetailsSharedPref.getString("o_step", String.valueOf(step));
        String savedText3 = userDetailsSharedPref.getString("o_distance", String.valueOf(distance));
        String savedText4 = userDetailsSharedPref.getString("o_ecal", String.valueOf(ecal));
        String savedText5 = userDetailsSharedPref.getString("o_cal", String.valueOf(cal));
        profile2check = userDetailsSharedPref.getBoolean("profile2check",false);


        profile2_bpm.setText(savedText1);
        profile2_step.setText(savedText2);
        profile2_distance.setText(savedText3);
        profile2_ecal.setText(savedText4);
        profile2_cal.setText(savedText5);


        // 값을 int로 변환합니다.
        final int[] o_bpm = {Integer.parseInt(savedText1)};
        final int[] o_step = {Integer.parseInt(savedText2)};
        final int[] o_distance = {Integer.parseInt(savedText3)};
        final int[] o_ecal = {Integer.parseInt(savedText4)};
        final int[] o_cal = {Integer.parseInt(savedText5)};

        // TextView에 int 값으로 설정합니다.
        profile2_bpm.setText(String.valueOf(o_bpm[0]));
        profile2_step.setText(String.valueOf(o_step[0]));
        profile2_distance.setText(String.valueOf(o_distance[0]));
        profile2_ecal.setText(String.valueOf(o_ecal[0]));
        profile2_cal.setText(String.valueOf(o_cal[0]));


        profile2_bpm_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                o_bpm[0]++;
                profile2_bpm.setText(String.valueOf(o_bpm[0]));
            }
        });

        profile2_bpm_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                o_bpm[0]--;
                profile2_bpm.setText(String.valueOf(o_bpm[0]));
            }
        });

        profile2_step_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                o_step[0]++;
                profile2_step.setText(String.valueOf(o_step[0]));
            }
        });

        profile2_step_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                o_step[0]--;
                profile2_step.setText(String.valueOf(o_step[0]));
            }
        });

        profile2_distance_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                o_distance[0]++;
                profile2_distance.setText(String.valueOf(o_distance[0]));
            }
        });

        profile2_distance_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                o_distance[0]--;
                profile2_distance.setText(String.valueOf(o_distance[0]));
            }
        });

        profile2_ecal_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                o_ecal[0]++;
                profile2_ecal.setText(String.valueOf(o_ecal[0]));
            }
        });

        profile2_ecal_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                o_ecal[0]--;
                profile2_ecal.setText(String.valueOf(o_ecal[0]));
            }
        });

        profile2_cal_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                o_cal[0]++;
                profile2_cal.setText(String.valueOf(o_cal[0]));
            }
        });

        profile2_cal_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                o_cal[0]--;
                profile2_cal.setText(String.valueOf(o_cal[0]));
            }
        });


        profile2_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textToSave1 = profile2_bpm.getText().toString();
                String textToSave2 = profile2_step.getText().toString();
                String textToSave3 = profile2_distance.getText().toString();
                String textToSave4 = profile2_ecal.getText().toString();
                String textToSave5 = profile2_cal.getText().toString();

                userDetailsEditor.putString("o_bpm", textToSave1);
                userDetailsEditor.putString("o_step", textToSave2);
                userDetailsEditor.putString("o_distance", textToSave3);
                userDetailsEditor.putString("o_ecal", textToSave4);
                userDetailsEditor.putString("o_cal", textToSave5);
                userDetailsEditor.putBoolean("profile2check",true);
                userDetailsEditor.apply();

                saveProfileData();

                viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

                viewModel.setBpm(textToSave1);
                viewModel.setTCalText(textToSave5);
                viewModel.setECalText(textToSave4);
                viewModel.setDistance(textToSave3);
                viewModel.setStep(textToSave2);

                // 저장한 후, 필요한 작업 수행 (예: 토스트 메시지 표시 등)
//                Toast.makeText(getActivity(), getResources().getString(R.string.saveData), Toast.LENGTH_SHORT).show();
            }
        });


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

    public void KeyboardUp(int size) {
        sv.postDelayed(new Runnable() {
            @Override
            public void run() {
                sv.smoothScrollTo(0, size);
            }
        }, 200);
    }



    public void saveProfileData(){

        String name = userDetailsSharedPref.getString("name", "null");
        String number = userDetailsSharedPref.getString("number", "null");
        String birthday = userDetailsSharedPref.getString("birthday", "null");
        String age = userDetailsSharedPref.getString("age", "null");
        String gender = userDetailsSharedPref.getString("gender", "null");
        String height = userDetailsSharedPref.getString("height", "null");
        String weight = userDetailsSharedPref.getString("weight", "null");
        String sleep1 = userDetailsSharedPref.getString("sleep1", "null");
        String sleep2 = userDetailsSharedPref.getString("sleep2", "null");

        String bpm = profile2_bpm.getText().toString();
        String step = profile2_step.getText().toString();
        String distance = profile2_distance.getText().toString();
        String eCal = profile2_ecal.getText().toString();
        String cal = profile2_cal.getText().toString();


        try {
            retrofitServerManager.setProfile(email, name, number, gender, height, weight, age, birthday, sleep1, sleep2,
                    bpm, step, distance, cal, eCal, new RetrofitServerManager.ServerTaskCallback() {
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
}

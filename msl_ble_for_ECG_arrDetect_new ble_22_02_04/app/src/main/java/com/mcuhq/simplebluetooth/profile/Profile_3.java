package com.mcuhq.simplebluetooth.profile;

import android.content.Context;
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

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mcuhq.simplebluetooth.fragment.ProfileFragment;
import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.server.RetrofitServerManager;
import com.mcuhq.simplebluetooth.SharedViewModel;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

public class Profile_3 extends Fragment {

    SharedViewModel viewModel;

    RetrofitServerManager retrofitServerManager;
    private SharedPreferences userDetailsSharedPref;
    private SharedPreferences.Editor userDetailsEditor;
    private Button profile3_save;

    private EditText profile3_arrcnt,profile3_guard_num1,profile3_guard_num2;


    public interface KeyboardUpListener {
        void onKeyboardUp(int size);
    }

    private KeyboardUpListener mListener;

    private boolean s_emergency = false;
    private boolean s_arr = false;
    private boolean s_muscle = false;
    private boolean s_drop = false;
    private boolean s_fastarr = false;
    private boolean s_slowarr = false;
    private boolean s_irregular = false;
    private boolean s_guard = false;
    private boolean profile3check = false;

    private int s_arrcnt = 1;

    private String email;

    ScrollView sv;



    public Profile_3() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile3, container, false);

        retrofitServerManager = RetrofitServerManager.getInstance();

        SharedPreferences emailSharedPreferences = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        email = emailSharedPreferences.getString("email", "null");

        userDetailsSharedPref = getActivity().getSharedPreferences(email, Context.MODE_PRIVATE);
        userDetailsEditor = userDetailsSharedPref.edit();

        Button btn1 = view.findViewById(R.id.profile3_emergency_off);
        Button btn2 = view.findViewById(R.id.profile3_emergency_on);
        Button btn3 = view.findViewById(R.id.profile3_arr_off);
        Button btn4 = view.findViewById(R.id.profile3_arr_on);
        Button btn5 = view.findViewById(R.id.profile3_muscle_off);
        Button btn6 = view.findViewById(R.id.profile3_muscle_on);
        Button btn7 = view.findViewById(R.id.profile3_drop_off);
        Button btn8 = view.findViewById(R.id.profile3_drop_on);
        Button btn9 = view.findViewById(R.id.profile3_fastarr_off);
        Button btn10 = view.findViewById(R.id.profile3_fastarr_on);
        Button btn11 = view.findViewById(R.id.profile3_slowarr_off);
        Button btn12 = view.findViewById(R.id.profile3_slowarr_on);
        Button btn13 = view.findViewById(R.id.profile3_irregular_off);
        Button btn14 = view.findViewById(R.id.profile3_irregular_on);
        Button btn15 = view.findViewById(R.id.profile3_guard_off);
        Button btn16 = view.findViewById(R.id.profile3_guard_on);
        profile3_arrcnt = view.findViewById(R.id.profile3_arrcnt);
        Button btn17 = view.findViewById(R.id.profile3_arrcnt_m);
        Button btn18 = view.findViewById(R.id.profile3_arrcnt_p);
        profile3_save = view.findViewById(R.id.profile3_save);
        profile3_guard_num1 = view.findViewById(R.id.profile3_guard_num1);
        profile3_guard_num2 = view.findViewById(R.id.profile3_guard_num2);


        // sharedpreferences에서 가져오기
        s_emergency = userDetailsSharedPref.getBoolean("s_emergency", true);
        s_arr = userDetailsSharedPref.getBoolean("s_arr", true);
        s_muscle = userDetailsSharedPref.getBoolean("s_muscle", false);
        s_drop = userDetailsSharedPref.getBoolean("s_drop", true);
        s_fastarr = userDetailsSharedPref.getBoolean("s_fastarr", false);
        s_slowarr = userDetailsSharedPref.getBoolean("s_slowarr", false);
        s_irregular = userDetailsSharedPref.getBoolean("s_irregular", false);
        s_guard = userDetailsSharedPref.getBoolean("s_guard", false);
        profile3check = userDetailsSharedPref.getBoolean("profile3check",false);
        String savedText1 = userDetailsSharedPref.getString("s_arrcnt", String.valueOf(s_arrcnt));
        String savedText2 = userDetailsSharedPref.getString("s_guard_num1", "");
        String savedText3 = userDetailsSharedPref.getString("s_guard_num2", "");

        profile3_arrcnt.setText(savedText1);
        profile3_guard_num1.setText(savedText2);
        profile3_guard_num2.setText(savedText3);

        final int[] s_arrcnt = {Integer.parseInt(savedText1)};
        profile3_arrcnt.setText(String.valueOf(s_arrcnt[0]));

        //버튼 초기 세팅
        btn1.setTextColor(Color.WHITE);
        btn1.setBackgroundColor(Color.DKGRAY);
        btn2.setTextColor(Color.BLACK);
        btn2.setBackgroundColor(Color.LTGRAY);
        btn3.setTextColor(Color.WHITE);
        btn3.setBackgroundColor(Color.DKGRAY);
        btn4.setTextColor(Color.BLACK);
        btn4.setBackgroundColor(Color.LTGRAY);
        btn5.setTextColor(Color.WHITE);
        btn5.setBackgroundColor(Color.DKGRAY);
        btn6.setTextColor(Color.BLACK);
        btn6.setBackgroundColor(Color.LTGRAY);
        btn7.setTextColor(Color.WHITE);
        btn7.setBackgroundColor(Color.DKGRAY);
        btn8.setTextColor(Color.BLACK);
        btn8.setBackgroundColor(Color.LTGRAY);
        btn9.setTextColor(Color.WHITE);
        btn9.setBackgroundColor(Color.DKGRAY);
        btn10.setTextColor(Color.BLACK);
        btn10.setBackgroundColor(Color.LTGRAY);
        btn11.setTextColor(Color.WHITE);
        btn11.setBackgroundColor(Color.DKGRAY);
        btn12.setTextColor(Color.BLACK);
        btn12.setBackgroundColor(Color.LTGRAY);
        btn13.setTextColor(Color.WHITE);
        btn13.setBackgroundColor(Color.DKGRAY);
        btn14.setTextColor(Color.BLACK);
        btn14.setBackgroundColor(Color.LTGRAY);
        btn15.setTextColor(Color.WHITE);
        btn15.setBackgroundColor(Color.DKGRAY);
        btn16.setTextColor(Color.BLACK);
        btn16.setBackgroundColor(Color.LTGRAY);

        // 해제/사용
        if (s_emergency==false) {
            btn1.setTextColor(Color.WHITE);
            btn1.setBackgroundColor(Color.DKGRAY);
            btn2.setTextColor(Color.BLACK);
            btn2.setBackgroundColor(Color.LTGRAY);
        }
        else if(s_emergency){
            btn1.setTextColor(Color.BLACK);
            btn1.setBackgroundColor(Color.LTGRAY);
            btn2.setTextColor(Color.WHITE);
            btn2.setBackgroundColor(Color.DKGRAY);
        }
        if (s_arr==false) {
            btn3.setTextColor(Color.WHITE);
            btn3.setBackgroundColor(Color.DKGRAY);
            btn4.setTextColor(Color.BLACK);
            btn4.setBackgroundColor(Color.LTGRAY);
        }
        else if(s_arr){
            btn3.setTextColor(Color.BLACK);
            btn3.setBackgroundColor(Color.LTGRAY);
            btn4.setTextColor(Color.WHITE);
            btn4.setBackgroundColor(Color.DKGRAY);
        }
        if (s_muscle==false) {
            btn5.setTextColor(Color.WHITE);
            btn5.setBackgroundColor(Color.DKGRAY);
            btn6.setTextColor(Color.BLACK);
            btn6.setBackgroundColor(Color.LTGRAY);
        }
        else if (s_muscle){
            btn5.setTextColor(Color.BLACK);
            btn5.setBackgroundColor(Color.LTGRAY);
            btn6.setTextColor(Color.WHITE);
            btn6.setBackgroundColor(Color.DKGRAY);
        }
        if (s_drop==false) {
            btn7.setTextColor(Color.WHITE);
            btn7.setBackgroundColor(Color.DKGRAY);
            btn8.setTextColor(Color.BLACK);
            btn8.setBackgroundColor(Color.LTGRAY);
        }
        else if (s_drop){
            btn7.setTextColor(Color.BLACK);
            btn7.setBackgroundColor(Color.LTGRAY);
            btn8.setTextColor(Color.WHITE);
            btn8.setBackgroundColor(Color.DKGRAY);
        }
        if (s_fastarr==false) {
            btn9.setTextColor(Color.WHITE);
            btn9.setBackgroundColor(Color.DKGRAY);
            btn10.setTextColor(Color.BLACK);
            btn10.setBackgroundColor(Color.LTGRAY);
        }
        else if (s_fastarr){
            btn9.setTextColor(Color.BLACK);
            btn9.setBackgroundColor(Color.LTGRAY);
            btn10.setTextColor(Color.WHITE);
            btn10.setBackgroundColor(Color.DKGRAY);
        }
        if (s_slowarr==false) {
            btn11.setTextColor(Color.WHITE);
            btn11.setBackgroundColor(Color.DKGRAY);
            btn12.setTextColor(Color.BLACK);
            btn12.setBackgroundColor(Color.LTGRAY);
        }
        else if (s_slowarr){
            btn11.setTextColor(Color.BLACK);
            btn11.setBackgroundColor(Color.LTGRAY);
            btn12.setTextColor(Color.WHITE);
            btn12.setBackgroundColor(Color.DKGRAY);
        }
        if (s_irregular==false) {
            btn13.setTextColor(Color.WHITE);
            btn13.setBackgroundColor(Color.DKGRAY);
            btn14.setTextColor(Color.BLACK);
            btn14.setBackgroundColor(Color.LTGRAY);
        }
        else if (s_irregular){
            btn13.setTextColor(Color.BLACK);
            btn13.setBackgroundColor(Color.LTGRAY);
            btn14.setTextColor(Color.WHITE);
            btn14.setBackgroundColor(Color.DKGRAY);
        }
        if (s_guard==false) {
            btn15.setTextColor(Color.WHITE);
            btn15.setBackgroundColor(Color.DKGRAY);
            btn16.setTextColor(Color.BLACK);
            btn16.setBackgroundColor(Color.LTGRAY);
        }
        else if (s_guard){
            btn15.setTextColor(Color.BLACK);
            btn15.setBackgroundColor(Color.LTGRAY);
            btn16.setTextColor(Color.WHITE);
            btn16.setBackgroundColor(Color.DKGRAY);
        }



        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_emergency = false;
                //버튼효과
                btn1.setTextColor(Color.WHITE);
                btn1.setBackgroundColor(Color.DKGRAY);
                btn2.setTextColor(Color.BLACK);
                btn2.setBackgroundColor(Color.LTGRAY);
                savesetting();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_emergency = true;
                btn1.setTextColor(Color.BLACK);
                btn1.setBackgroundColor(Color.LTGRAY);
                btn2.setTextColor(Color.WHITE);
                btn2.setBackgroundColor(Color.DKGRAY);
                savesetting();
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_arr = false;
                btn3.setTextColor(Color.WHITE);
                btn3.setBackgroundColor(Color.DKGRAY);
                btn4.setTextColor(Color.BLACK);
                btn4.setBackgroundColor(Color.LTGRAY);
                savesetting();
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_arr = true;
                btn3.setTextColor(Color.BLACK);
                btn3.setBackgroundColor(Color.LTGRAY);
                btn4.setTextColor(Color.WHITE);
                btn4.setBackgroundColor(Color.DKGRAY);
                savesetting();
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_muscle = false;
                btn5.setTextColor(Color.WHITE);
                btn5.setBackgroundColor(Color.DKGRAY);
                btn6.setTextColor(Color.BLACK);
                btn6.setBackgroundColor(Color.LTGRAY);
                savesetting();
            }
        });

        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_muscle = true;
                btn5.setTextColor(Color.BLACK);
                btn5.setBackgroundColor(Color.LTGRAY);
                btn6.setTextColor(Color.WHITE);
                btn6.setBackgroundColor(Color.DKGRAY);
                savesetting();
            }
        });

        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_drop = false;
                btn7.setTextColor(Color.WHITE);
                btn7.setBackgroundColor(Color.DKGRAY);
                btn8.setTextColor(Color.BLACK);
                btn8.setBackgroundColor(Color.LTGRAY);
                savesetting();
            }
        });

        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_drop = true;
                btn7.setTextColor(Color.BLACK);
                btn7.setBackgroundColor(Color.LTGRAY);
                btn8.setTextColor(Color.WHITE);
                btn8.setBackgroundColor(Color.DKGRAY);
                savesetting();
            }
        });

        btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_fastarr = false;
                btn9.setTextColor(Color.WHITE);
                btn9.setBackgroundColor(Color.DKGRAY);
                btn10.setTextColor(Color.BLACK);
                btn10.setBackgroundColor(Color.LTGRAY);
                savesetting();
            }
        });

        btn10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_fastarr = true;
                btn9.setTextColor(Color.BLACK);
                btn9.setBackgroundColor(Color.LTGRAY);
                btn10.setTextColor(Color.WHITE);
                btn10.setBackgroundColor(Color.DKGRAY);
                savesetting();
            }
        });

        btn11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_slowarr = false;
                btn11.setTextColor(Color.WHITE);
                btn11.setBackgroundColor(Color.DKGRAY);
                btn12.setTextColor(Color.BLACK);
                btn12.setBackgroundColor(Color.LTGRAY);
                savesetting();
            }
        });

        btn12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_slowarr = true;
                btn11.setTextColor(Color.BLACK);
                btn11.setBackgroundColor(Color.LTGRAY);
                btn12.setTextColor(Color.WHITE);
                btn12.setBackgroundColor(Color.DKGRAY);
                savesetting();
            }
        });

        btn13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_irregular = false;
                btn13.setTextColor(Color.WHITE);
                btn13.setBackgroundColor(Color.DKGRAY);
                btn14.setTextColor(Color.BLACK);
                btn14.setBackgroundColor(Color.LTGRAY);
                savesetting();
            }
        });

        btn14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_irregular = true;
                btn13.setTextColor(Color.BLACK);
                btn13.setBackgroundColor(Color.LTGRAY);
                btn14.setTextColor(Color.WHITE);
                btn14.setBackgroundColor(Color.DKGRAY);
                savesetting();
            }
        });

        btn15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_guard = false;
                btn15.setTextColor(Color.WHITE);
                btn15.setBackgroundColor(Color.DKGRAY);
                btn16.setTextColor(Color.BLACK);
                btn16.setBackgroundColor(Color.LTGRAY);
                savesetting();
            }
        });

        btn16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_guard = true;
                btn15.setTextColor(Color.BLACK);
                btn15.setBackgroundColor(Color.LTGRAY);
                btn16.setTextColor(Color.WHITE);
                btn16.setBackgroundColor(Color.DKGRAY);
                savesetting();
            }
        });

        btn17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_arrcnt[0]--;
                profile3_arrcnt.setText(String.valueOf(s_arrcnt[0]));
            }
        });

        btn18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_arrcnt[0]++;
                profile3_arrcnt.setText(String.valueOf(s_arrcnt[0]));
            }
        });

        //저장버튼으로 알림발생 기준 횟수, 보호자연락처1,2 저장
        profile3_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textToSave1 = profile3_arrcnt.getText().toString();
                String textToSave2 = profile3_guard_num1.getText().toString();
                String textToSave3 = profile3_guard_num2.getText().toString();
                ArrayList<String> guardianList = new ArrayList<>();

                userDetailsEditor.putString("s_arrcnt", textToSave1);
                userDetailsEditor.putString("s_guard_num1", textToSave2);
                userDetailsEditor.putString("s_guard_num2", textToSave3);
                userDetailsEditor.putBoolean("profile3check",true);
                userDetailsEditor.apply();

                if (!textToSave2.isEmpty())
                    guardianList.add(textToSave2);
                if (!textToSave3.isEmpty())
                    guardianList.add(textToSave3);
                if (!textToSave2.isEmpty() || !textToSave3.isEmpty())
                    saveGuardianToServer(guardianList);

                // 저장한 후, 필요한 작업 수행 (예: 토스트 메시지 표시 등)
                Toast.makeText(getActivity(), getResources().getString(R.string.saveData), Toast.LENGTH_SHORT).show();
            }
        });

        //입력할때 키보드에 대한 높이조절
        profile3_arrcnt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

//                KeyboardUp(1200);
                someMethod(500);
            }

        });
        profile3_guard_num1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

//                KeyboardUp(1400);
//                someMethod(700);
                adjustForEditText(profile3_guard_num1);
            }

        });
        profile3_guard_num2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

//                KeyboardUp(1500);
//                someMethod(800);
                adjustForEditText(profile3_guard_num2);
            }

        });
        return view;
    }
    private void savesetting() {
        // SharedPreferences에 알림설정 저장
        userDetailsEditor.putBoolean("s_emergency", s_emergency);
        userDetailsEditor.putBoolean("s_arr", s_arr);
        userDetailsEditor.putBoolean("s_muscle", s_muscle);
        userDetailsEditor.putBoolean("s_drop", s_drop);
        userDetailsEditor.putBoolean("s_fastarr", s_fastarr);
        userDetailsEditor.putBoolean("s_slowarr", s_slowarr);
        userDetailsEditor.putBoolean("s_irregular", s_irregular);
        userDetailsEditor.putBoolean("s_guard", s_guard);
        userDetailsEditor.apply();

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        viewModel.setEmergency(s_emergency);
        viewModel.setArr(s_arr);
        viewModel.setMyo(s_muscle);
        viewModel.setNonContact(s_drop);
        viewModel.setFastarr(s_fastarr);
        viewModel.setSlowarr(s_slowarr);
        viewModel.setIrregular(s_irregular);
    }

    public void KeyboardUp(int size) {
        sv.postDelayed(new Runnable() {
            @Override
            public void run() {
                sv.smoothScrollTo(0, size);
            }
        }, 200);
    }

    public void adjustForEditText(EditText editText) {
        int[] location = new int[2];
        editText.getLocationOnScreen(location);
        int yPosition = location[1];

        someMethod(yPosition);
    }

    public void someMethod(int size) {
        ProfileFragment parentFragment = (ProfileFragment) getParentFragment();
        if (parentFragment != null) {
            parentFragment.controlScroll(size);
        }
    }

    private void saveGuardianToServer(ArrayList<String> guardian) {

        String utcOffsetAndCountry = timeZone();
        String writeTime = currentUtcTime();

        retrofitServerManager.setGuardian(email, utcOffsetAndCountry, writeTime, guardian, new RetrofitServerManager.ServerTaskCallback() {
            @Override
            public void onSuccess(String result) {
                Log.i("saveGuardianToServer", result);
                if (result.toLowerCase().contains("true")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.setGuardianComp), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.setGuardianFail), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getActivity(), getResources().getString(R.string.setGuardianFail), Toast.LENGTH_SHORT).show();
                Log.e("saveGuardianToServer", "send Err");
                e.printStackTrace();
            }
        });
    }

    public String timeZone(){
        // 국가 코드
        Locale current = getActivity().getResources().getConfiguration().getLocales().get(0);
        String currentCountry = current.getCountry();
        String utcOffset;
        String utcOffsetAndCountry;

        // 현재 시스템의 기본 타임 존
        TimeZone currentTimeZone = TimeZone.getDefault();

        // 타임 존의 아이디
        String timeZoneId = currentTimeZone.getID();

        ZoneId zoneId = ZoneId.of(timeZoneId);
        ZoneOffset offset = LocalDateTime.now().atZone(zoneId).getOffset();

        String utcTime = String.valueOf(offset);
        String firstChar = String.valueOf(utcTime.charAt(0));

        if (firstChar.equals("+") || firstChar.equals("-")){
            utcOffset = utcTime;
        }
        else {
            utcOffset = "+" + utcTime;
        }

        utcOffsetAndCountry = utcOffset + "/" + currentCountry;

        return utcOffsetAndCountry;
    }


    public String currentUtcTime(){
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        TimeZone currentTimeZone = TimeZone.getDefault();
        String timeZoneId = currentTimeZone.getID();
        ZonedDateTime currentTimezone = now.withZoneSameInstant(ZoneId.of(timeZoneId));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String formattedString = formatter.format(currentTimezone);

        return formattedString;
    }



}

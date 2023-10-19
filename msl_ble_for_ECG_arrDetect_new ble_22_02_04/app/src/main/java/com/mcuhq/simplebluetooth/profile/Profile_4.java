package com.mcuhq.simplebluetooth.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.SharedViewModel;

import java.util.ArrayList;
import java.util.List;

public class Profile_4 extends Fragment {

    List<String> lTx = new ArrayList<>();

    private SharedViewModel sharedViewModel;


    private boolean e_ecg = false;
    private boolean e_manual = false;

    private boolean esent = false;
    private boolean psent = false;
    private boolean asent = false;

    private boolean bsent = false;
    private boolean b1sent = false;
    private boolean b2sent = false;
    private boolean b3sent = false;
    private int e_x = 0;



    public Profile_4() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile4, container, false);


        Button btn1 = view.findViewById(R.id.profile4_ECGMODE);
        Button btn2 = view.findViewById(R.id.profile4_PEAKMODE);
        Button btn3 = view.findViewById(R.id.profile4_AUTO);
        Button btn4 = view.findViewById(R.id.profile4_MANUAL);
        Button btn5 = view.findViewById(R.id.profile4_X0_5);
        Button btn6 = view.findViewById(R.id.profile4_X1);
        Button btn7 = view.findViewById(R.id.profile4_X2);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        e_ecg = sharedPreferences.getBoolean("e_ecg", false);
        e_manual = sharedPreferences.getBoolean("e_auto", false);
        e_x = sharedPreferences.getInt("e_x",0 );

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
        btn6.setTextColor(Color.WHITE);
        btn6.setBackgroundColor(Color.DKGRAY);
        btn7.setTextColor(Color.WHITE);
        btn7.setBackgroundColor(Color.DKGRAY);

        if (e_ecg==false) {
            btn1.setTextColor(Color.WHITE);
            btn1.setBackgroundColor(Color.DKGRAY);
            btn2.setTextColor(Color.BLACK);
            btn2.setBackgroundColor(Color.LTGRAY);
        }
        else if(e_ecg){
            btn1.setTextColor(Color.BLACK);
            btn1.setBackgroundColor(Color.LTGRAY);
            btn2.setTextColor(Color.WHITE);
            btn2.setBackgroundColor(Color.DKGRAY);
        }
        if (e_manual==false) {
            btn3.setTextColor(Color.WHITE);
            btn3.setBackgroundColor(Color.DKGRAY);
            btn4.setTextColor(Color.BLACK);
            btn4.setBackgroundColor(Color.LTGRAY);
        }
        else if(e_manual){
            btn3.setTextColor(Color.BLACK);
            btn3.setBackgroundColor(Color.LTGRAY);
            btn4.setTextColor(Color.WHITE);
            btn4.setBackgroundColor(Color.DKGRAY);
        }
        if (e_x == 0){
            btn5.setTextColor(Color.BLACK);
            btn5.setBackgroundColor(Color.LTGRAY);
            btn6.setTextColor(Color.BLACK);
            btn6.setBackgroundColor(Color.LTGRAY);
            btn7.setTextColor(Color.BLACK);
            btn7.setBackgroundColor(Color.LTGRAY);
        }
        else if(e_manual && e_x == 1){
            btn5.setTextColor(Color.WHITE);
            btn5.setBackgroundColor(Color.DKGRAY);
            btn6.setTextColor(Color.BLACK);
            btn6.setBackgroundColor(Color.LTGRAY);
            btn7.setTextColor(Color.BLACK);
            btn7.setBackgroundColor(Color.LTGRAY);
        }
        else if (e_manual && e_x == 2){
            btn5.setTextColor(Color.BLACK);
            btn5.setBackgroundColor(Color.LTGRAY);
            btn6.setTextColor(Color.WHITE);
            btn6.setBackgroundColor(Color.DKGRAY);
            btn7.setTextColor(Color.BLACK);
            btn7.setBackgroundColor(Color.LTGRAY);
        }
        else if (e_manual && e_x == 3){
            btn5.setTextColor(Color.BLACK);
            btn5.setBackgroundColor(Color.LTGRAY);
            btn6.setTextColor(Color.BLACK);
            btn6.setBackgroundColor(Color.LTGRAY);
            btn7.setTextColor(Color.WHITE);
            btn7.setBackgroundColor(Color.DKGRAY);
        }
        else {
            btn5.setTextColor(Color.BLACK);
            btn5.setBackgroundColor(Color.LTGRAY);
            btn6.setTextColor(Color.BLACK);
            btn6.setBackgroundColor(Color.LTGRAY);
            btn7.setTextColor(Color.BLACK);
            btn7.setBackgroundColor(Color.LTGRAY);
        }


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                e_ecg = false;
                //버튼효과
                btn1.setTextColor(Color.WHITE);
                btn1.setBackgroundColor(Color.DKGRAY);
                btn2.setTextColor(Color.BLACK);
                btn2.setBackgroundColor(Color.LTGRAY);
                savesetting();

                if (!esent){
                    sendTx("e\n");
                    esent = true;
                    psent = false;
                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                e_ecg = true;
                btn1.setTextColor(Color.BLACK);
                btn1.setBackgroundColor(Color.LTGRAY);
                btn2.setTextColor(Color.WHITE);
                btn2.setBackgroundColor(Color.DKGRAY);
                savesetting();

                if (!psent){
                    sendTx("p\n");
                    psent = true;
                    esent = false;
                }
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                e_manual = false;
                e_x = 0;
                btn3.setTextColor(Color.WHITE);
                btn3.setBackgroundColor(Color.DKGRAY);
                btn4.setTextColor(Color.BLACK);
                btn4.setBackgroundColor(Color.LTGRAY);
                btn5.setTextColor(Color.BLACK);
                btn5.setBackgroundColor(Color.LTGRAY);
                btn6.setTextColor(Color.BLACK);
                btn6.setBackgroundColor(Color.LTGRAY);
                btn7.setTextColor(Color.BLACK);
                btn7.setBackgroundColor(Color.LTGRAY);
                savesetting();

                if (!asent){
                    sendTx("a\n");
                    asent = true;
                    bsent = false;
                }
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                e_manual = true;
                e_x = 2;
                btn3.setTextColor(Color.BLACK);
                btn3.setBackgroundColor(Color.LTGRAY);
                btn4.setTextColor(Color.WHITE);
                btn4.setBackgroundColor(Color.DKGRAY);
                btn5.setTextColor(Color.BLACK);
                btn5.setBackgroundColor(Color.LTGRAY);
                btn6.setTextColor(Color.WHITE);
                btn6.setBackgroundColor(Color.DKGRAY);
                btn7.setTextColor(Color.BLACK);
                btn7.setBackgroundColor(Color.LTGRAY);
                savesetting();

                if (!bsent){
                    sendTx("b\n");
                    sendTx("b1\n");
                    bsent = true;
                    asent = false;
                }
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (e_manual){
                    e_x = 1;
                    btn5.setTextColor(Color.WHITE);
                    btn5.setBackgroundColor(Color.DKGRAY);
                    btn6.setTextColor(Color.BLACK);
                    btn6.setBackgroundColor(Color.LTGRAY);
                    btn7.setTextColor(Color.BLACK);
                    btn7.setBackgroundColor(Color.LTGRAY);
                }
                else {
                    e_x = 0;
                    btn5.setTextColor(Color.BLACK);
                    btn5.setBackgroundColor(Color.LTGRAY);
                    btn6.setTextColor(Color.BLACK);
                    btn6.setBackgroundColor(Color.LTGRAY);
                    btn7.setTextColor(Color.BLACK);
                    btn7.setBackgroundColor(Color.LTGRAY);
                }
                savesetting();

                if (!b1sent){
                    sendTx("b1\n");
                    b1sent = true;
                    b2sent = false;
                    b3sent = false;
                }
            }
        });

        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (e_manual) {
                    e_x = 2;
                    btn5.setTextColor(Color.BLACK);
                    btn5.setBackgroundColor(Color.LTGRAY);
                    btn6.setTextColor(Color.WHITE);
                    btn6.setBackgroundColor(Color.DKGRAY);
                    btn7.setTextColor(Color.BLACK);
                    btn7.setBackgroundColor(Color.LTGRAY);
                }
                else {
                    e_x = 0;
                    btn5.setTextColor(Color.BLACK);
                    btn5.setBackgroundColor(Color.LTGRAY);
                    btn6.setTextColor(Color.BLACK);
                    btn6.setBackgroundColor(Color.LTGRAY);
                    btn7.setTextColor(Color.BLACK);
                    btn7.setBackgroundColor(Color.LTGRAY);
                }
                savesetting();

                if (!b2sent){
                    sendTx("b2\n");
                    b1sent = false;
                    b2sent = true;
                    b3sent = false;
                }
            }
        });

        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (e_manual) {
                    e_x = 3;
                    btn5.setTextColor(Color.BLACK);
                    btn5.setBackgroundColor(Color.LTGRAY);
                    btn6.setTextColor(Color.BLACK);
                    btn6.setBackgroundColor(Color.LTGRAY);
                    btn7.setTextColor(Color.WHITE);
                    btn7.setBackgroundColor(Color.DKGRAY);
                }
                else {
                    e_x = 0;
                    btn5.setTextColor(Color.BLACK);
                    btn5.setBackgroundColor(Color.LTGRAY);
                    btn6.setTextColor(Color.BLACK);
                    btn6.setBackgroundColor(Color.LTGRAY);
                    btn7.setTextColor(Color.BLACK);
                    btn7.setBackgroundColor(Color.LTGRAY);
                }
                savesetting();

                if (!b3sent){
                    sendTx("b3\n");
                    b1sent = false;
                    b2sent = false;
                    b3sent = true;
                }
            }
        });

        return view;
    }

    private void savesetting() {
        // SharedPreferences에 알림설정 저장
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("e_ecg", e_ecg);
        editor.putBoolean("e_manual", e_manual);
        editor.putInt("e_x", e_x);

        editor.apply();


    }
    public void sendTx(String s) {
        try {
            synchronized (lTx) {
                lTx.add(s);
            }

//            if (mConnectedThread!=null){
//                mConnectedThread.write(s);
//            }
        } catch (Exception ignored) {
        }
    }
}
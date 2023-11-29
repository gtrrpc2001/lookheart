package com.mcuhq.simplebluetooth.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.auth.Find_Pw;
import com.mcuhq.simplebluetooth.profile.Profile_1;
import com.mcuhq.simplebluetooth.profile.Profile_2;
import com.mcuhq.simplebluetooth.profile.Profile_3;

public class ProfileFragment extends Fragment {

    private TextView profile_name,profile_email,profile_day;
    private ScrollView sv;
    private Button profile_logout, profile_changepw;;
    private String email;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        SharedPreferences emailSharedPreferences = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        email = emailSharedPreferences.getString("email", "null");

        profile_name = view.findViewById(R.id.profile_name);
        profile_email = view.findViewById(R.id.profile_email);
        profile_day = view.findViewById(R.id.profile_day);
        sv = view.findViewById(R.id.profile_ScrollView);

        profile_logout = view.findViewById(R.id.profile_logout_btn);
        profile_changepw = view.findViewById(R.id.profile_changepw);

        //sharedpreferneces에서 불러오기 (이름,이메일)
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(email, Context.MODE_PRIVATE);
        String savedText1 = sharedPreferences.getString("name", "");
        String savedText2 = sharedPreferences.getString("email", "");
        String savedText3 = sharedPreferences.getString("current_date","");

        profile_name.setText(savedText1);
        profile_email.setText(savedText2);
        profile_day.setText(savedText3);


        // 프래그먼트 전환 버튼
        Button btn1 = view.findViewById(R.id.profile_information);
        Button btn2 = view.findViewById(R.id.profile_objective);
        Button btn3 = view.findViewById(R.id.profile_setting);
//        Button btn4 = view.findViewById(R.id.profile_ecg);



        //기본정보 프래그먼트(profile_1) 기본으로
        Profile_1 childFragment = new Profile_1();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.inner_fragment_container, childFragment);
        fragmentTransaction.commit();
        btn1.setTextColor(Color.BLACK);
        btn2.setTextColor(Color.LTGRAY);
        btn3.setTextColor(Color.LTGRAY);
//        btn4.setTextColor(Color.LTGRAY);
//        btn1.setTextSize(16);
//        btn2.setTextSize(16);
//        btn3.setTextSize(16);
//        btn4.setTextSize(20);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Profile_1 childFragment = new Profile_1();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.inner_fragment_container, childFragment);
                fragmentTransaction.commit();


                btn1.setTextColor(Color.BLACK);
                btn2.setTextColor(Color.LTGRAY);
                btn3.setTextColor(Color.LTGRAY);
//                btn4.setTextColor(Color.LTGRAY);
//                btn1.setTextSize(20);
//                btn2.setTextSize(20);
//                btn3.setTextSize(20);
//                btn4.setTextSize(20);


            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Profile_2 childFragment = new Profile_2();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.inner_fragment_container, childFragment);
                fragmentTransaction.commit();

                btn1.setTextColor(Color.LTGRAY);
                btn2.setTextColor(Color.BLACK);
                btn3.setTextColor(Color.LTGRAY);
//                btn4.setTextColor(Color.LTGRAY);
//                btn1.setTextSize(20);
//                btn2.setTextSize(20);
//                btn3.setTextSize(20);
//                btn4.setTextSize(20);

            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Profile_3 childFragment = new Profile_3();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.inner_fragment_container, childFragment);
                fragmentTransaction.commit();

                btn1.setTextColor(Color.LTGRAY);
                btn2.setTextColor(Color.LTGRAY);
                btn3.setTextColor(Color.BLACK);
//                btn4.setTextColor(Color.LTGRAY);
//                btn1.setTextSize(20);
//                btn2.setTextSize(20);
//                btn3.setTextSize(20);
//                btn4.setTextSize(20);

            }
        });

//        btn4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                profile_4 childFragment = new profile_4();
//                FragmentManager fragmentManager = getParentFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.inner_fragment_container, childFragment);
//                fragmentTransaction.commit();
//
//                btn1.setTextColor(Color.LTGRAY);
//                btn2.setTextColor(Color.LTGRAY);
//                btn3.setTextColor(Color.LTGRAY);
//                btn4.setTextColor(Color.BLACK);
//                btn1.setTextSize(20);
//                btn2.setTextSize(20);
//                btn3.setTextSize(20);
//                btn4.setTextSize(25);
//
//            }
//        });
        profile_changepw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Find_Pw.class);

                // 다른 데이터를 넘겨야 할 경우에는 Intent에 데이터를 추가할 수 있습니다.
                // intent.putExtra("key", "value");

                // 액티비티로 이동
                startActivity(intent);
            }
        });

        profile_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog();
            }
        });

        return view;
    }


    private void showConfirmationDialog() {
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
                        // 취소 버튼을 눌렀을 때 아무 작업 없이 다이얼로그 창을 닫습니다.
                        dialog.dismiss();
                    }
                });

        Dialog dialog = builder.create();
        dialog.show();
    }

    public void controlScroll(int size) {
        sv.postDelayed(() -> sv.smoothScrollTo(0, size), 200);
    }
}
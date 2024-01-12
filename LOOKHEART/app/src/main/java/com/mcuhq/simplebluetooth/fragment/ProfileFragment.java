package com.mcuhq.simplebluetooth.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.profile.GuardianProfile;
import com.mcuhq.simplebluetooth.profile.Profile_1;
import com.mcuhq.simplebluetooth.profile.Profile_2;
import com.mcuhq.simplebluetooth.profile.Profile_3;

import com.library.lookheartLibrary.server.UserProfileManager;
import com.library.lookheartLibrary.server.UserProfile;

public class ProfileFragment extends Fragment {

    private TextView profile_name,profile_email,profile_day;
    private ScrollView sv;
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

        //sharedpreferneces에서 불러오기 (이름,이메일)
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(email, Context.MODE_PRIVATE);
        String savedText1 = UserProfileManager.getInstance().getUserProfile().getName();
        String savedText2 = "◦" + UserProfileManager.getInstance().getUserProfile().getEmail();
        String savedText3 = "◦" + UserProfileManager.getInstance().getUserProfile().getJoinDate();

        profile_name.setText(savedText1);
        profile_email.setText(savedText2);
        profile_day.setText(savedText3);


        // 프래그먼트 전환 버튼
        Button btn1 = view.findViewById(R.id.profile_information);
        Button btn2 = view.findViewById(R.id.profile_objective);
        Button btn3 = view.findViewById(R.id.profile_setting);
        Button btn4 = view.findViewById(R.id.profile_guardian);




        //기본정보 프래그먼트(profile_1) 기본으로
        Profile_1 childFragment = new Profile_1();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.inner_fragment_container, childFragment);
        fragmentTransaction.commit();
        btn1.setTextColor(Color.BLACK);
        btn2.setTextColor(Color.LTGRAY);
        btn3.setTextColor(Color.LTGRAY);
        btn4.setTextColor(Color.LTGRAY);

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
                btn4.setTextColor(Color.LTGRAY);


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
                btn4.setTextColor(Color.LTGRAY);

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
                btn4.setTextColor(Color.LTGRAY);

            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GuardianProfile childFragment = new GuardianProfile();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.inner_fragment_container, childFragment);
                fragmentTransaction.commit();

                btn1.setTextColor(Color.LTGRAY);
                btn2.setTextColor(Color.LTGRAY);
                btn3.setTextColor(Color.LTGRAY);
                btn4.setTextColor(Color.BLACK);

            }
        });

        return view;
    }

    public void controlScroll(int size) {
        sv.postDelayed(() -> sv.smoothScrollTo(0, size), 200);
    }
}
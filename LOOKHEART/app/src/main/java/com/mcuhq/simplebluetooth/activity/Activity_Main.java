package com.mcuhq.simplebluetooth.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.library.lookheartLibrary.fragment.ArrFragment;
import com.mcuhq.simplebluetooth.fragment.home.HomeFragment;
import com.mcuhq.simplebluetooth.fragment.ProfileFragment;
import com.mcuhq.simplebluetooth.R;
import com.library.lookheartLibrary.fragment.SummaryFragment;
import com.library.lookheartLibrary.viewmodel.SharedViewModel;

import java.util.ArrayList;

public class Activity_Main extends AppCompatActivity {

    private enum FragmentType {
        HOME("Home"),
        SUMMARY("Summary"),
        ARR("Arr"),
        PROFILE("Profile");

        private final String type;

        FragmentType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    String email;
    BottomNavigationView bottomNav;
    private Fragment home, profile;
    FragmentManager fragmentManager;

    private ArrFragment arrFragment;
    private SummaryFragment summaryFragment;

    private ArrayList<Fragment> fragmentsList = new ArrayList<>();

    @SuppressLint("BatteryLife")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        setButtonEvent();

    }

    private void init() {
        SharedPreferences emailSharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        email = emailSharedPreferences.getString("email", "null");
        // 화면 자동 꺼짐 방지
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        bottomNav = findViewById(R.id.bottom_navigation);
        fragmentManager = getSupportFragmentManager();
    }

    private void setButtonEvent() {

        // Start Fragment
        home = new HomeFragment();
        fragmentManager.beginTransaction().replace(R.id.main_frame, home).commit();
        fragmentsList.add(home);

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch (id) {
                    case R.id.bottom_home:
                        home = (Fragment) getFragment(home, FragmentType.HOME);
                        setFragment(home, FragmentType.HOME);
                        break;
                    case R.id.bottom_summary:
                        summaryFragment = (SummaryFragment) getFragment(summaryFragment, FragmentType.SUMMARY);
                        setFragment(summaryFragment, FragmentType.SUMMARY);
                        break;
                    case R.id.bottom_arr:
                        if (arrFragment != null)
                            arrFragment.updateArrList();
                        arrFragment = (ArrFragment) getFragment(arrFragment, FragmentType.ARR);
                        setFragment(arrFragment, FragmentType.ARR);
                        break;

                    case R.id.bottom_profile:
                        profile = (Fragment) getFragment(profile, FragmentType.PROFILE);
                        setFragment(profile, FragmentType.PROFILE);
                        break;
                }

                return true;

            }
        });
    }

    private Object getFragment(Fragment fragment, FragmentType type) {
        if (fragment == null) {
            fragment = getTypeFragment(type);
            fragmentManager.beginTransaction().add(R.id.main_frame, fragment).commit();
            fragmentsList.add(fragment);
            return fragment;
        }
        return fragment;
    }

    private void setFragment(Fragment fragment, FragmentType type) {

        for (Fragment eventFragment : fragmentsList) {
            if (fragment == eventFragment) {
                fragmentManager.beginTransaction().show(eventFragment).commit();
            } else {
                if (eventFragment != null)
                    fragmentManager.beginTransaction().hide(eventFragment).commit();
            }
        }
    }

    private Fragment getTypeFragment(FragmentType type) {
        switch (type) {
            case HOME:
                return new HomeFragment();
            case SUMMARY:
                return new SummaryFragment();
            case ARR:
                return new ArrFragment(email);
            case PROFILE:
                return new ProfileFragment();
            default:
                return null;
        }
    }
}

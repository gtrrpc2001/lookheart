package com.mcuhq.simplebluetooth.profile;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.base.MyTimeZone;
import com.mcuhq.simplebluetooth.server.RetrofitServerManager;

import com.library.lookheartLibrary.server.UserProfileManager;
import com.library.lookheartLibrary.server.UserProfile;

import java.util.ArrayList;
import java.util.Objects;

public class GuardianProfile extends Fragment {

    private final static String phoneNumberPattern = "^[0-9]{9,20}$";

    private View view;
    private RetrofitServerManager retrofitServerManager;
    private MyTimeZone myTimeZone;
    private String email;

    private final static int FIRST_GUARDIAN_TAG = 0;
    private final static int SECOND_GUARDIAN_TAG = 1;

    private String[] guardianNumbers;
    private String firstNumber;
    private String secondNumber;

    private Boolean checkFirstNumberRegex;
    private Boolean checkSecondNumberRegex;

    // -------------------------- UI var --------------------------
    // region
    private ScrollView sv;
    private Button guardian_off;
    private Button guardian_on;
    private ArrayList<EditText> editTexts = new ArrayList<>();

    private Button plusArrCnt;
    private Button minusArrCnt;

    private Button saveGuardianButton;
    private EditText guardianArrCnt;
    private EditText firstGuardianNumber;
    private EditText secondGuardianNumber;
    // endRegion

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_profile_guardian, container, false);

        addFindViewById();

        setButtonEvent();

        setEditText();

        init();

        return view;
    }

    private void saveGuardianButton() {

        ArrayList<String> guardianNumberList = new ArrayList<>();

        if (!firstNumber.isEmpty()) {
            if (checkFirstNumberRegex)
                guardianNumberList.add(firstNumber);
        } else {
            String[] guardianNumbers = UserProfileManager.getInstance().getGuardianNumbers();
            if (guardianNumbers.length > 0 )
                guardianNumberList.add(guardianNumbers[0]);

        }

        if (!secondNumber.isEmpty()) {
            if (checkSecondNumberRegex)
                guardianNumberList.add(secondNumber);
        } else {
            String[] guardianNumbers = UserProfileManager.getInstance().getGuardianNumbers();
            if (guardianNumbers.length > 1 )
                guardianNumberList.add(guardianNumbers[1]);
        }

        System.out.println(firstNumber);
        System.out.println(secondNumber);
        if (guardianNumberList.size() > 0)
            serverTask(guardianNumberList);
    }

    private void serverTask(ArrayList<String> guardianNumberList) {

        String timeZone = myTimeZone.getTimeZone(getSafeActivity());
        String writeTime = myTimeZone.getCurrentUtcTime();

        retrofitServerManager.setGuardian(email, timeZone, writeTime, guardianNumberList, new RetrofitServerManager.ServerTaskCallback() {
            @Override
            public void onSuccess(String result) {
                if (result.toLowerCase().contains("true"))
                    showToast(getResources().getString(R.string.setGuardianComp));
                else
                    showToast(getResources().getString(R.string.setGuardianFail));
            }

            @Override
            public void onFailure(Exception e) {
                showToast(getResources().getString(R.string.setGuardianFail));
                e.printStackTrace();
            }
        });
    }

    private void init() {
        // Email
        email = UserProfileManager.getInstance().getUserProfile().getEmail();

        // TimeZone
        myTimeZone = MyTimeZone.getInstance();

        // GuardianNumbers
        guardianNumbers = UserProfileManager.getInstance().getGuardianNumbers();
        retrofitServerManager = RetrofitServerManager.getInstance();

        int i = 0;
        for (String numbers : guardianNumbers)
            editTexts.get(i++).setText(numbers);

    }

    private void setEditText() {
        setEditTextEvent(firstGuardianNumber, FIRST_GUARDIAN_TAG);
        setEditTextEvent(secondGuardianNumber, SECOND_GUARDIAN_TAG);
    }

    private void setEditTextEvent(EditText editText, int tag) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Start Focus
                    KeyboardUp(400);
                    editText.setText("");
                } else {
                    // 입력을 안하면 기존 번호를 보여 주는 기능 추가 필요
                }
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                inputText(s.toString(), tag);
            }
        });

        editTexts.add(editText);
    }

    private void inputText(String text, int tag) {
        switch (tag) {

            case FIRST_GUARDIAN_TAG:
                firstNumber = text;
                checkFirstNumberRegex = checkRegex(firstNumber);
                break;

            case SECOND_GUARDIAN_TAG:
                secondNumber = text;
                checkSecondNumberRegex = checkRegex(secondNumber);
                break;

            default:
                break;
        }
    }

    private boolean checkRegex(String text) {
        return text.trim().matches(phoneNumberPattern);
    }

    private void setButtonEvent() {
        // 추후 기능 추가 필요 : guardian 알림 기능, 알림 기준 횟수 기능
        setOnClickListener(guardian_off);
        setOnClickListener(guardian_on);
        setOnClickListener(minusArrCnt);
        setOnClickListener(plusArrCnt);

        // save button event
        saveGuardianButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGuardianButton();
            }
        });

    }

    private void setOnClickListener(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast(getResources().getString(R.string.functionMessage));
            }
        });
    }

    private void addFindViewById() {
        sv = view.findViewById(R.id.guardian_scrollView);

        guardian_off = view.findViewById(R.id.guardian_off);
        guardian_on = view.findViewById(R.id.guardian_on);

        minusArrCnt = view.findViewById(R.id.guardian_arrNumber_minus);
        plusArrCnt = view.findViewById(R.id.guardian_arrNumber_plus);

        saveGuardianButton = view.findViewById(R.id.guardian_save);

        guardianArrCnt = view.findViewById(R.id.guardian_arrNumber_editText);
        firstGuardianNumber = view.findViewById(R.id.first_guardian_editText);
        secondGuardianNumber = view.findViewById(R.id.second_guardian_editText);
    }

    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void KeyboardUp(int size) {
        sv.postDelayed(new Runnable() {
            @Override
            public void run() {
                sv.smoothScrollTo(0, size);
            }
        }, 200);
    }

    public Activity getSafeActivity() {
        return Objects.requireNonNull(getActivity());
    }
}

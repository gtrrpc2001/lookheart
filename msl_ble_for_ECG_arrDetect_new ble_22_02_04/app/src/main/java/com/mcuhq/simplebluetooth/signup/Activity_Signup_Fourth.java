package com.mcuhq.simplebluetooth.signup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mcuhq.simplebluetooth.activity.Activity_Login;
import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.server.RetrofitServerManager;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Activity_Signup_Fourth extends AppCompatActivity {

    final String namePattern = "^[가-힣]{1,5}|[a-zA-Z]{2,10}[a-zA-Z]{2,10}$";
    final String numberPattern = "^[0-9]{1,3}$";

    EditText nameEditText;
    EditText heightEditText;
    EditText weightEditText;

    TextView nameHelp;
    TextView heightHelp;
    TextView weightHelp;

    LinearLayout male;
    LinearLayout female;
    ImageView maleImg;
    ImageView femaleImg;

    Button birthdayButton;
    TextView birthdayText;

    int age;

    Button completeButton;
    Button backButton;

    ScrollView sv;

    // 입력 체크
    Map<String, Boolean> dataCheck;
    Boolean nameCheck;
    Boolean heightCheck;
    Boolean weightCheck;
    Boolean genderCheck;
    Boolean birthdayCheck;
    private String name;
    private String height;
    private String weight;
    private String gender; // true : male, false : female
    private String birthday;

    Boolean serverSignupCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_fourth);

        // EditText
        nameEditText = findViewById(R.id.editName);
        heightEditText = findViewById(R.id.editHeight);
        weightEditText = findViewById(R.id.editWeight);

        // editText Color
        ColorStateList notMatchColor = ColorStateList.valueOf(Color.RED);
        ColorStateList highlightColor = ColorStateList.valueOf(Color.parseColor("#62AFFF"));

        // 유효성 도움말
        nameHelp = findViewById(R.id.nameHelp);
        heightHelp = findViewById(R.id.heightHelp);
        weightHelp = findViewById(R.id.weightHelp);

        // male, female
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        maleImg = findViewById(R.id.maleImg);
        femaleImg = findViewById(R.id.femaleImg);

        // 생년월일 버튼, 텍스트
        birthdayButton = findViewById(R.id.birthday);
        birthdayText = findViewById(R.id.birthdayText);

        // 뒤로가기, 가입완료 버튼
        backButton = findViewById(R.id.signup_fourth_back);
        completeButton = findViewById(R.id.signup_complete);

        sv = findViewById(R.id.scrollView);

        // editText Hint 설정
        setHintText();

        // 초기화
        nameCheck = false;
        heightCheck = false;
        weightCheck = false;
        genderCheck = false;
        birthdayCheck = false;

        // 입력 데이터 초기화
        dataCheck = new HashMap<>();
        dataCheck.put("name", false);
        dataCheck.put("height", false);
        dataCheck.put("weight", false);
        dataCheck.put("birthday", false);

        // Name etitText focus
        nameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // 포커스가 없어질 때
                    if (nameCheck) {
                        // 입력 값이 유효한 경우
                    } else {
                        // 입력 값이 유효하지 않은 경우
                        nameHelp.setVisibility(View.VISIBLE);
                        ViewCompat.setBackgroundTintList(nameEditText, notMatchColor);
                    }
                } else {
                    // 포커스를 얻었을 때
                    ViewCompat.setBackgroundTintList(nameEditText, highlightColor);
                    KeyboardUp(0);
                }
            }
        });
        // Name etitText input
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                name = s.toString();

                if (s.toString().trim().matches(namePattern)) {
                    // 매칭되는 경우
                    dataCheck.put("name", true);
                    nameHelp.setVisibility(View.GONE);
                } else {
                    // 매칭되지 않는 경우
                    dataCheck.put("name", false);
                }
                // 유효성 체크
                nameCheck = dataCheck.get("name");
            }
        });

        // Height etitText focus
        heightEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // 포커스가 없어질 때
                    if (heightCheck) {
                        // 입력 값이 유효한 경우
                    } else {
                        // 입력 값이 유효하지 않은 경우
                        heightHelp.setVisibility(View.VISIBLE);
                        ViewCompat.setBackgroundTintList(heightEditText, notMatchColor);
                    }
                } else {
                    // 포커스를 얻었을 때
                    ViewCompat.setBackgroundTintList(heightEditText, highlightColor);
                    KeyboardUp(0);
                }
            }
        });
        // Height etitText input
        heightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                height = s.toString();

                if (s.toString().trim().matches(numberPattern)) {
                    // 매칭되는 경우
                    dataCheck.put("height", true);
                    heightHelp.setVisibility(View.GONE);
                } else {
                    // 매칭되지 않는 경우
                    dataCheck.put("height", false);
                }
                // 유효성 체크
                heightCheck = dataCheck.get("height");
            }
        });

        // Weight etitText focus
        weightEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // 포커스가 없어질 때
                    if (weightCheck) {
                        // 입력 값이 유효한 경우
                    } else {
                        // 입력 값이 유효하지 않은 경우
                        weightHelp.setVisibility(View.VISIBLE);
                        ViewCompat.setBackgroundTintList(weightEditText, notMatchColor);
                    }
                } else {
                    // 포커스를 얻었을 때
                    ViewCompat.setBackgroundTintList(weightEditText, highlightColor);
                    KeyboardUp(0);
                }
            }
        });
        // Weight etitText input
        weightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                weight = s.toString();

                if (s.toString().trim().matches(numberPattern)) {
                    // 매칭되는 경우
                    dataCheck.put("weight", true);
                    weightHelp.setVisibility(View.GONE);
                } else {
                    // 매칭되지 않는 경우
                    dataCheck.put("weight", false);
                }
                // 유효성 체크
                weightCheck = dataCheck.get("weight");
            }
        });

        // gender Button Click Event
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이미지 변경
                maleImg.setImageResource(R.drawable.signup_radiobutton_press);
                femaleImg.setImageResource(R.drawable.signup_radiobutton_normal);

                gender = "남자"; // male
                genderCheck = true; // 입력 체크


            }
        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이미지 변경
                femaleImg.setImageResource(R.drawable.signup_radiobutton_press);
                maleImg.setImageResource(R.drawable.signup_radiobutton_normal);

                gender = "여자"; // female
                genderCheck = true; // 입력 체크

            }
        });

        birthdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = 1968;
                int month = 0;
                int day = 1;

                // Create a new instance of DatePickerDialog with spinner style
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        Activity_Signup_Fourth.this,
                        R.style.RoundedDatePickerDialog,  // 새로운 스타일 적용,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Do something with the chosen date
                                getAge(year, month+1, dayOfMonth);

                                String formattedDate = getResources().getString(
                                        R.string.formatted_date,
                                        year,
                                        month + 1, // 0부터 시작하므로 1을 더함
                                        dayOfMonth
                                );

                                birthday = String.valueOf(year) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(dayOfMonth);
                                birthdayText.setText(formattedDate);
                                birthdayCheck = true;
                            }
                        }, year, month, day
                );

                if (datePickerDialog.getWindow() != null) {
                    datePickerDialog.getWindow().setLayout(
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.MATCH_PARENT);
                    datePickerDialog.getWindow().setGravity(Gravity.CENTER);
                }
                // 데이트피커 제목을 설정합니다.
//                datePickerDialog.setTitle("생년월일을 선택하세요");

                // 데이트피커 확인 버튼을 설정합니다.
                datePickerDialog.show();
            }
        });

        // 뒤로가기
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 로그인 완료
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 입력값 유효성 체크
                if (nameCheck && heightCheck && weightCheck && genderCheck && birthdayCheck) {

                    SharedPreferences sharedPref = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    String email =  sharedPref.getString("email", "null");
                    String pw = sharedPref.getString("password", "null");

                    RetrofitServerManager retrofitServerManager = RetrofitServerManager.getInstance();

                    Log.e("pw", pw);

                    // API 호출
                    retrofitServerManager.setSignupData(email, pw, name, gender, height, weight, String.valueOf(age), birthday, new RetrofitServerManager.ServerTaskCallback() {
                        @Override
                        public void onSuccess(String result) {
                            // 서버 응답 성공 처리
                            if(result.toLowerCase().contains("true")) {
                                // 회원가입 성공
                                runOnUiThread(() -> completeLogin());

                            } else {
                                // 회원가입 실패
                                Log.e("signupResult", result);
                                runOnUiThread(() -> Toast.makeText(Activity_Signup_Fourth.this, "회원가입 실패", Toast.LENGTH_SHORT).show());
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            // 서버 응답 실패 처리
                            // 에러 메시지 표시 등
                            runOnUiThread(() -> Toast.makeText(Activity_Signup_Fourth.this, "서버 응답 없음", Toast.LENGTH_SHORT).show());
                            Log.e("err", String.valueOf(e));
                        }
                    });

                } else {
                    String alertString = "";

                    if (!nameCheck) {
                        alertString += getResources().getString(R.string.name_Label) + ", ";
                    }
                    if (!heightCheck) {
                        alertString += getResources().getString(R.string.height) + ", ";
                    }
                    if (!weightCheck) {
                        alertString += getResources().getString(R.string.weight) + ", ";
                    }
                    if (gender == null) {
                        alertString += getResources().getString(R.string.gender) + ",";
                    }
                    if (!birthdayCheck) {
                        alertString += getResources().getString(R.string.birthday_Label) + ", ";
                    }

                    // 알림창
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Signup_Fourth.this);
                    builder.setTitle(getResources().getString(R.string.noti))
                            .setMessage(alertString.substring(0, alertString.length() - 2) + getResources().getString(R.string.enterAgain))
                            .setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // 취소 버튼 클릭 시 수행할 동작
                                    dialog.cancel(); // 팝업창 닫기
                                }
                            })
                            .show();
                }
            }
        });
    }

    public void SaveUserData() {

        String currentDate = getCurrentDate();

        SharedPreferences sharedPref = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        String email = sharedPref.getString("email", "null");

        SharedPreferences emailSharedPref = getSharedPreferences(email, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = emailSharedPref.edit();

        editor.putBoolean("agree", false);
        editor.putBoolean("privacy", false);

        // 기본 정보
        editor.putString("name", name);
        editor.putString("height", height);
        editor.putString("weight", weight);
        editor.putString("gender", gender);
        editor.putString("birthday", birthday);
        editor.putString("current_date", currentDate);
        editor.putString("sleep1", "23");
        editor.putString("sleep2", "7");

        // 기본 목표량
        editor.putString("o_bpm", "90"); // 활동 기준 bpm
        editor.putString("o_cal", "3000"); // 일일 목표 총 칼로리
        editor.putString("o_ecal", "500"); // 일일 목표 활동 칼로리
        editor.putString("o_step", "2000"); // 일일 목표 걸음수
        editor.putString("o_distance", "5"); // 일일 목표 걸음거리

        // 기본 설정 알람
        editor.putBoolean("s_emergency", true); // 응급 상황
        editor.putBoolean("s_arr", true); // 비정상 맥박
        editor.putBoolean("s_drop", true); // 전극 떨어짐
        editor.putBoolean("s_muscle", false); // 근전도
        editor.putBoolean("s_slowarr", false); // 서맥
        editor.putBoolean("s_fastarr", false); // 빈맥
        editor.putBoolean("s_irregular", false); // 불규칙 맥박

        editor.apply(); // 비동기 저장
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    public void KeyboardUp(int size) {
        sv.postDelayed(new Runnable() {
            @Override
            public void run() {
                sv.smoothScrollTo(0, size);
            }
        }, 200);
    }

    public void setHintText() {
        // EditText에 힌트 텍스트 스타일을 적용
        String nameHintText = getResources().getString(R.string.nameHintText);
        String heightHintText = getResources().getString(R.string.heightHintText);
        String weightHintText = getResources().getString(R.string.weightHintText);

        // 힌트 텍스트에 스타일을 적용
        SpannableString ssName = new SpannableString(nameHintText);
        SpannableString ssHeight = new SpannableString(heightHintText);
        SpannableString ssWeight = new SpannableString(weightHintText);

        AbsoluteSizeSpan assName = new AbsoluteSizeSpan(13, true); // 힌트 텍스트 크기 설정
        AbsoluteSizeSpan assHeight = new AbsoluteSizeSpan(13, true);
        AbsoluteSizeSpan assWeight = new AbsoluteSizeSpan(13, true);

        ssName.setSpan(assName, 0, ssName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 크기 적용
        ssHeight.setSpan(assHeight, 0, ssHeight.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssWeight.setSpan(assWeight, 0, ssWeight.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 힌트 텍스트 굵기 설정
        ssName.setSpan(new StyleSpan(Typeface.NORMAL), 0, ssName.length(), 0); // 굵게
        ssHeight.setSpan(new StyleSpan(Typeface.NORMAL), 0, ssHeight.length(), 0);
        ssWeight.setSpan(new StyleSpan(Typeface.NORMAL), 0, ssWeight.length(), 0);

        // 스타일이 적용된 힌트 텍스트를 EditText에 설정
        EditText nameText = (EditText) findViewById(R.id.editName);
        EditText heightText = (EditText) findViewById(R.id.editHeight);
        EditText weightText = (EditText) findViewById(R.id.editWeight);

        nameText.setHint(new SpannedString(ssName)); // 크기가 적용된 힌트 텍스트 설정
        heightText.setHint(new SpannedString(ssHeight));
        weightText.setHint(new SpannedString(ssWeight));

        nameText.setHintTextColor(Color.parseColor("#555555")); // 색 변경
        heightText.setHintTextColor(Color.parseColor("#555555"));
        weightText.setHintTextColor(Color.parseColor("#555555"));
    }

    public void getAge(int year, int month, int day){
        // 현재 시간
        Calendar today = Calendar.getInstance();
        int currentYear = today.get(Calendar.YEAR);
        int currentMonth = today.get(Calendar.MONTH) + 1;  // 0-based index
        int currentDay = today.get(Calendar.DAY_OF_MONTH);

        age = currentYear - year;

        if (month > currentMonth || (month == currentMonth && day > currentDay)) {
            age = age - 1;
        }
    }

    public void completeLogin(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Signup_Fourth.this);
        builder.setTitle(getResources().getString(R.string.signup_complete))
                .setMessage(getResources().getString(R.string.returnLogin))
                .setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // 데이터 저장
                        SaveUserData();

                        // 기존 stack에 있는 activity 모두 종료 시키고 login Activity로 이동
                        Intent intent = new Intent(Activity_Signup_Fourth.this, Activity_Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        dialog.cancel(); // 팝업창 닫기
                    }
                })
                .show();
    }
}
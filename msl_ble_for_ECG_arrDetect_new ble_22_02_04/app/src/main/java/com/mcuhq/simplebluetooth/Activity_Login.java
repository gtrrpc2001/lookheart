package com.mcuhq.simplebluetooth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mcuhq.simplebluetooth.signup.Activity_Signup_First;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Activity_Login extends AppCompatActivity {

    // 이메일/비밀번호 정규식
    final String emailPattern = "^[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,20}$";
    final String passwordPattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&<>*~:`-]).{10,}$";

    boolean autoLoginCheck;
    boolean autoLogin;
    Button autoLoginButton;
    ImageButton autoLoginImageButton;

    EditText emailEditText;
    EditText passwordEditText;

    Button signupButton;
    Button loginButton;
    ScrollView sv;

    private String email = "";
    private String password = "";

    Map<String, Boolean> dataCheck;
    Boolean emailCheck;
    Boolean passwordCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // edit text
        emailEditText = findViewById(R.id.editEmail);
        passwordEditText = findViewById(R.id.editPassword);
        // auto Login Button
        autoLoginButton = findViewById(R.id.autoLogin);
        autoLoginImageButton = findViewById(R.id.autoLoginImage);
        // signup Button
        signupButton = findViewById(R.id.singup);
        loginButton = findViewById(R.id.loginButton);

        sv = findViewById(R.id.scrollView);

        // 입력 데이터 초기화
        dataCheck = new HashMap<>();
        dataCheck.put("email", false);
        dataCheck.put("password", false);

        // 초기화
        autoLoginCheck = false;
        emailCheck = false;
        passwordCheck = false;

        // edit Text hint set
        setHintText();

        SharedPreferences autoLoginSP = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = autoLoginSP.edit();

        autoLogin = autoLoginSP.getBoolean("autoLogin", false);

        if (autoLogin) {
            Intent intent = new Intent(Activity_Login.this, Activity_Main.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        // email event
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // 포커스가 없어질 때
                    if (emailCheck) {
                        // 입력 값이 유효한 경우
                    }
                    else {
                        // 입력 값이 유효하지 않은 경우
                    }
                } else {
                    // 포커스를 얻었을 때
                    KeyboardUp(400);
                }
            }
        });
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                email = s.toString();

                if (s.toString().trim().matches(emailPattern)) {
                    // 매칭되는 경우
                    dataCheck.put("email", true);
                } else {
                    // 매칭되지 않는 경우
                    dataCheck.put("email", false);
                }
                // 유효성 체크
                emailCheck = dataCheck.get("email");
            }
        });

        // password event
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // 포커스가 없어질 때
                    if (passwordCheck) {
                        // 입력 값이 유효한 경우
                    }
                    else {
                        // 입력 값이 유효하지 않은 경우
                    }
                } else {
                    // 포커스를 얻었을 때
                    KeyboardUp(400);
                }
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                password = s.toString();

                if (s.toString().trim().matches(passwordPattern)) {
                    // 매칭되는 경우
                    dataCheck.put("password", true);
                } else {
                    // 매칭되지 않는 경우
                    dataCheck.put("password", false);
                }
                // 유효성 체크
                passwordCheck = dataCheck.get("password");
            }
        });

        // signup activity 전환
        signupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(Activity_Login.this, Activity_Signup_First.class);
                startActivity(signupIntent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);

                String getEmail = sharedPref.getString("email", "NULL");
                String getPassword = sharedPref.getString("password", "NULL");

                // 관리자 아이디
                if(email.equals("admin") && password.equals("admin")){
                    // 자동 로그인 정보 저장
                    editor.putString("email", "androidAdmin");
                    editor.putString("password", "admin");
                    editor.putString("name", "admin");
                    editor.putString("height", "180");
                    editor.putString("weight", "70");
                    editor.putString("gender", "남자");
                    editor.putString("birthday", "2023-01-01");
                    editor.putBoolean("autoLogin", autoLoginCheck);
                    editor.putBoolean("firstAutoLogin", autoLoginCheck);
                    editor.apply();

                    Intent intent = new Intent(Activity_Login.this, Activity_Main.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                    Toast.makeText(Activity_Login.this, "로그인 성공!", Toast.LENGTH_SHORT).show();
                    return;
                }


                // null 값 확인
                if ( (email == null || email.isEmpty()) && (password == null || password.isEmpty()) ) {
                    Toast.makeText(Activity_Login.this, "이메일과 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (email == null || email.isEmpty()) {
                    Toast.makeText(Activity_Login.this, "이메일을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (password == null || password.isEmpty()) {
                    Toast.makeText(Activity_Login.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                String encPw = encryptECB("MEDSYSLAB.CO.KR.LOOKHEART.ENCKEY", password);
                String modified = getPassword.trim().replaceAll("\\s", "");

                if(email.equals(getEmail) && encPw.trim().equals(modified) && emailCheck && passwordCheck ){

                    // 자동 로그인 정보 저장
                    editor.putBoolean("autoLogin", autoLoginCheck);
                    editor.putBoolean("firstAutoLogin", autoLoginCheck);
                    editor.apply();

                    Intent intent = new Intent(Activity_Login.this, Activity_Main.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                    Toast.makeText(Activity_Login.this, "로그인 성공!", Toast.LENGTH_SHORT).show();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Login.this);
                    builder.setTitle("로그인 실패")
                            .setMessage("아이디 또는 비밀번호를 잘못 입력했습니다.")
                            .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // 취소 버튼 클릭 시 수행할 동작
                                    dialog.cancel(); // 팝업창 닫기
                                }
                            })
                            .show();

//                    Toast.makeText(activity_login.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                }

                // 키보드 숨기기
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        });
    }

    public void KeyboardUp(int size) {
        sv.postDelayed(new Runnable() {
            @Override
            public void run() {
                sv.smoothScrollTo(0, size);
            }
        }, 200);
    }

    // 자동 로그인 클릭 이벤트
     public void autoLoginClickEvent(View v) {
        autoLoginCheck = !autoLoginCheck;
        if (autoLoginCheck) {
            autoLoginImageButton.setImageResource(R.drawable.login_autologin_press);
        }else {
            autoLoginImageButton.setImageResource(R.drawable.login_autologin_normal);
        }
    }

    public void setHintText(){
        // EditText에 힌트 텍스트 스타일을 적용
        String emailHintText = "이메일을 입력하세요";
        String passwordHintText = "비밀번호를 입력하세요";

        // 힌트 텍스트에 스타일을 적용
        SpannableString ssEmail = new SpannableString(emailHintText);
        SpannableString ssPassword = new SpannableString(passwordHintText);
        AbsoluteSizeSpan assEmail = new AbsoluteSizeSpan(12, true); // 힌트 텍스트 크기 설정
        AbsoluteSizeSpan assPassword = new AbsoluteSizeSpan(12, true);
        ssEmail.setSpan(assEmail, 0, ssEmail.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 크기 적용
        ssPassword.setSpan(assPassword, 0, ssPassword.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 힌트 텍스트 굵기 설정
        ssEmail.setSpan(new StyleSpan(Typeface.NORMAL), 0, ssEmail.length(), 0); // 굵게
        ssPassword.setSpan(new StyleSpan(Typeface.NORMAL), 0, ssPassword.length(), 0);

        // 스타일이 적용된 힌트 텍스트를 EditText에 설정
        EditText emailText = (EditText)findViewById(R.id.editEmail);
        EditText passwordText = (EditText)findViewById(R.id.editPassword);

        emailText.setHint(new SpannedString(ssEmail)); // 크기가 적용된 힌트 텍스트 설정
        passwordText.setHint(new SpannedString(ssPassword));

        emailText.setHintTextColor(Color.parseColor("#555555")); // 색 변
        passwordText.setHintTextColor(Color.parseColor("#555555"));
    }

    public static String encryptECB(String key, String value) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

            byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
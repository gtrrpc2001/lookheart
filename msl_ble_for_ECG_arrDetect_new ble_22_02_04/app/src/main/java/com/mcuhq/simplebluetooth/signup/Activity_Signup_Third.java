package com.mcuhq.simplebluetooth.signup;

import android.app.AlertDialog;
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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.ServerManager;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class Activity_Signup_Third extends AppCompatActivity {

    // 이메일/비밀번호 정규식
    final String emailPattern = "^[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,20}$";
    final String passwordPattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&<>*~:`-]).{10,}$";

    EditText emailEditText;
    EditText passwordEditText;
    EditText reEnterPasswordEditText;

    TextView emailHelp;
    TextView passwordHelp;
    TextView reEnterPasswordHelp;

    Button nextButton;
    Button backButton;

    ScrollView sv;

    // 입력 체크
    Map<String, Boolean> dataCheck;
    Boolean emailCheck;
    Boolean passwordCheck;
    Boolean reEnterpasswordCheck;

    Boolean idCheck = false;

    private String email;
    private String password;
    private String reEnterPassword;
    String encPw = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_third);

        // editText 입력창
        emailEditText = findViewById(R.id.editEmail);
        passwordEditText = findViewById(R.id.editPassword);
        reEnterPasswordEditText = findViewById(R.id.editReEnterPassword);

        // editText Color
        ColorStateList notMatchColor = ColorStateList.valueOf(Color.RED);
        ColorStateList highlightColor = ColorStateList.valueOf(Color.parseColor("#62AFFF"));

        // 유효성 도움말
        emailHelp = findViewById(R.id.emailHelp);
        passwordHelp = findViewById(R.id.passwordHelp);
        reEnterPasswordHelp = findViewById(R.id.reEnterPasswordHelp);

        // 뒤로가기, 다음으로 버튼
        nextButton = findViewById(R.id.signup_third_next);
        backButton = findViewById(R.id.signup_third_back);

        sv = findViewById(R.id.scrollView);

        // 인스턴스 얻기
        SharedPreferences sharedPref = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);

        // 값 저장 객체 얻기
        SharedPreferences.Editor editor = sharedPref.edit();

        // 입력 데이터 초기화
        dataCheck = new HashMap<>();
        dataCheck.put("email", false);
        dataCheck.put("password", false);
        dataCheck.put("reEnterPassword", false);

        // 초기화
        emailCheck = false;
        passwordCheck = false;
        reEnterpasswordCheck = false;

        // hint Text 설정
        setHintText();

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
                        emailHelp.setVisibility(View.VISIBLE);
                        ViewCompat.setBackgroundTintList(emailEditText, notMatchColor);
                    }
                } else {
                    // 포커스를 얻었을 때
                    ViewCompat.setBackgroundTintList(emailEditText, highlightColor);
                    KeyboardUp(300);
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
                    emailHelp.setVisibility(View.GONE);
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
                        passwordHelp.setVisibility(View.VISIBLE);
                        ViewCompat.setBackgroundTintList(passwordEditText, notMatchColor);
                    }
                } else {
                    // 포커스를 얻었을 때
                    ViewCompat.setBackgroundTintList(passwordEditText, highlightColor);
                    KeyboardUp(300);
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
                    passwordHelp.setVisibility(View.GONE);
                } else {
                    // 매칭되지 않는 경우
                    dataCheck.put("password", false);
                }
                // 유효성 체크
                passwordCheck = dataCheck.get("password");
            }
        });

        // reEnterPassword Event
        reEnterPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // 포커스가 없어질 때
                    if (reEnterpasswordCheck) {
                        // 입력 값이 유효한 경우
                    }
                    else {
                        // 입력 값이 유효하지 않은 경우
                        reEnterPasswordHelp.setVisibility(View.VISIBLE);
                        ViewCompat.setBackgroundTintList(reEnterPasswordEditText, notMatchColor);
                    }
                } else {
                    // 포커스를 얻었을 때
                    ViewCompat.setBackgroundTintList(reEnterPasswordEditText, highlightColor);
                    KeyboardUp(300);
                }
            }
        });
        reEnterPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                reEnterPassword = s.toString();

                if (reEnterPassword.equals(password)){
                    // 매칭되는 경우
                    dataCheck.put("reEnterPassword", true);
                    reEnterPasswordHelp.setVisibility(View.GONE);
                } else {
                    // 매칭되지 않는 경우
                    dataCheck.put("reEnterPassword", false);
                }
                // 유효성 체크
                reEnterpasswordCheck = dataCheck.get("reEnterPassword");
            }
        });

        // 뒤로가기
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 다음으로
        nextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // 입력값 유효성 체크
                if(emailCheck && passwordCheck && reEnterpasswordCheck){

                    try {
                        encPw = encryptECB("MEDSYSLAB.CO.KR.LOOKHEART.ENCKEY", password);
                        encPw = encPw.trim().replaceAll("\\s", "");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    // server에 email 중복 확인 요청
                    ServerManager serverManager = new ServerManager();
                    serverManager.checkIdTask(email, new ServerManager.ServerTaskCallback() {
                        @Override
                        public void onSuccess(Boolean result) {
                            if(result){
                                // email, password 저장
                                editor.putString("email", email);
                                editor.putString("password", encPw);
                                editor.apply();

                                Log.e("encPw", encPw);
                                // activiry 이동
                                Intent signupIntent = new Intent(Activity_Signup_Third.this, Activity_Signup_Fourth.class);
                                startActivity(signupIntent);
                            }
                            else {
                                // email 중복
                                runOnUiThread(() -> isIdDuplicate(v)); // 알림
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {

                        }
                    });

                    // email, password 저장
//                    editor.putString("email", email);
//                    editor.putString("password", encPw);
//                    editor.apply();
//
//                    Log.e("encPw", encPw);
//                    // activiry 이동
//                    Intent signupIntent = new Intent(Activity_Signup_Third.this, Activity_Signup_Fourth.class);
//                    startActivity(signupIntent);


                } else {
                    OnClickHandler(v);
                }
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

    public void OnClickHandler(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String alertString = "";

        if (!emailCheck) {
            alertString += "이메일,";
        }
        if (!passwordCheck) {
            alertString += "비밀번호,";
        }
        if (!reEnterpasswordCheck) {
            alertString += "비밀번호 재입력,";
        }

        builder.setTitle("알림")
                .setMessage(alertString.substring(0, alertString.length() - 1) + "을(를)\n다시 입력해주세요")
                .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 취소 버튼 클릭 시 수행할 동작
                        dialog.cancel(); // 팝업창 닫기
                    }
                })
                .show();
    }

    public void isIdDuplicate(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("알림")
                .setMessage("존재하는 아이디입니다.\n새로운 아이디를 다시 입력해주세요.")
                .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 취소 버튼 클릭 시 수행할 동작
                        dialog.cancel(); // 팝업창 닫기
                    }
                })
                .show();
    }

    public void setHintText(){
        // EditText에 힌트 텍스트 스타일을 적용
        String emailHintText = "이메일을 입력하세요";
        String passwordHintText = "비밀번호를 입력하세요";
        String reEnterPasswordHintText = "비밀번호를 재입력하세요";

        // 힌트 텍스트에 스타일을 적용
        SpannableString ssEmail = new SpannableString(emailHintText);
        SpannableString ssPassword = new SpannableString(passwordHintText);
        SpannableString ssReEnterPassword = new SpannableString(reEnterPasswordHintText);

        AbsoluteSizeSpan assEmail = new AbsoluteSizeSpan(13, true); // 힌트 텍스트 크기 설정
        AbsoluteSizeSpan assPassword = new AbsoluteSizeSpan(13, true);
        AbsoluteSizeSpan assReEnterPassword = new AbsoluteSizeSpan(13, true);

        ssEmail.setSpan(assEmail, 0, ssEmail.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 크기 적용
        ssPassword.setSpan(assPassword, 0, ssPassword.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssReEnterPassword.setSpan(assReEnterPassword, 0, ssReEnterPassword.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 힌트 텍스트 굵기 설정
        ssEmail.setSpan(new StyleSpan(Typeface.NORMAL), 0, ssEmail.length(), 0); // 굵게
        ssPassword.setSpan(new StyleSpan(Typeface.NORMAL), 0, ssPassword.length(), 0);
        ssReEnterPassword.setSpan(new StyleSpan(Typeface.NORMAL), 0, ssReEnterPassword.length(), 0);

        // 스타일이 적용된 힌트 텍스트를 EditText에 설정
        EditText emailText = (EditText)findViewById(R.id.editEmail);
        EditText passwordText = (EditText)findViewById(R.id.editPassword);
        EditText reEnterPasswordText = (EditText)findViewById(R.id.editReEnterPassword);

        emailText.setHint(new SpannedString(ssEmail)); // 크기가 적용된 힌트 텍스트 설정
        passwordText.setHint(new SpannedString(ssPassword));
        reEnterPasswordText.setHint(new SpannedString(ssReEnterPassword));

        emailText.setHintTextColor(Color.parseColor("#555555")); // 색 변경
        passwordText.setHintTextColor(Color.parseColor("#555555"));
        reEnterPasswordText.setHintTextColor(Color.parseColor("#555555"));
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

//    public static String decryptECB(String key, String encrypted) {
//        try {
//            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
//
//            // Base64 디코딩을 먼저 수행합니다.
//            byte[] decodedBytes = Base64.decode(encrypted, Base64.DEFAULT);
//
//            byte[] original = cipher.doFinal(decodedBytes);
//            return new String(original, StandardCharsets.UTF_8);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
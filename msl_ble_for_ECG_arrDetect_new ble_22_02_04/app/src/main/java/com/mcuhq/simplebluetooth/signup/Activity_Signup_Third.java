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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.auth.SendMail;
import com.mcuhq.simplebluetooth.server.RetrofitServerManager;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class Activity_Signup_Third extends AppCompatActivity {

    // 이메일/비밀번호 정규식
    final String emailPattern = "^[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,20}$";
    final String passwordPattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&<>*~:`-]).{10,}$";

    RetrofitServerManager retrofitServerManager;

    EditText emailEditText;
    EditText passwordEditText;
    EditText reEnterPasswordEditText;
    EditText editChecknum;

    TextView emailHelp;
    TextView passwordHelp;
    TextView reEnterPasswordHelp;

    Button nextButton, backButton, signup_idcheck, signup_numcheck;

    CheckBox emailCheckBox,numCheckBox;

    ScrollView sv;
    View numlayout;
    String getCode = "";

    // 입력 체크
    Map<String, Boolean> dataCheck;
    Boolean emailCheck;
    Boolean passwordCheck;
    Boolean reEnterpasswordCheck;

    private String email;
    private String password;
    private String reEnterPassword;
    private String encPw = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_third);

        retrofitServerManager = RetrofitServerManager.getInstance();

        // editText 입력창
        emailEditText = findViewById(R.id.editEmail);
        passwordEditText = findViewById(R.id.editPassword);
        reEnterPasswordEditText = findViewById(R.id.editReEnterPassword);
        editChecknum = findViewById(R.id.editChecknum);

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
        signup_numcheck = findViewById(R.id.signup_numcheck);
        signup_idcheck = findViewById(R.id.signup_idcheck);
        emailCheckBox = findViewById(R.id.emailCheckBox);
        numCheckBox = findViewById(R.id.numCheckBox);

        numlayout = findViewById(R.id.numberlayout);
        numlayout.setVisibility(View.GONE);

        sv = findViewById(R.id.scrollView);

        emailCheckBox.setEnabled(false);
        numCheckBox.setEnabled(false);

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

        signup_idcheck.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                signup_idcheck.setEnabled(false);

                String inputText = emailEditText.getText().toString().trim();
                hideKeyboard();

                if(inputText.isEmpty()){
                    OnClickHandler(v);
                } else if (emailCheck == false) {
                    emailOnClickHandler();

                } else if(emailCheck){

                    // server에 email 중복 확인 요청
                    retrofitServerManager.checkIdTask(email, new RetrofitServerManager.ServerTaskCallback() {
                        @Override
                        public void onSuccess(String result) {
                            if(result.toLowerCase().contains("true")){

                                SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                editor.putString("email", email);
                                editor.apply();

                                //send();
                                String getEmail = emailEditText.getText().toString();
                                SendMail mailServer = new SendMail(getEmail);
                                String title = "msl";
                                String content = "lookheart";

                                if (getEmail.length() != 0 && getEmail.contains("@")){
                                    runOnUiThread(() -> numOnClickHandler());
                                    boolean emailChecked = mailServer.sendSecurityCode(getApplicationContext(),title,content);
                                    Log.e("emailChecked", String.valueOf(emailChecked));

                                    getCode = mailServer.getCode();
                                    // code.requestFocus();
                                    if (getCode.equals("")){

                                    }
                                }

                            }
                            else {

                                runOnUiThread(() -> isIdDuplicate(v)); // 알림
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            runOnUiThread(() -> Toast.makeText(Activity_Signup_Third.this, "서버 응답 없음", Toast.LENGTH_SHORT).show());
                            Log.e("err", String.valueOf(e));
                        }
                    });

                }
            }
        });

        signup_numcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codeStr = editChecknum.getText().toString();
                if(codeStr.length() != 0){
                    if(codeStr.equals(getCode)){
                        codeStr = "";
//                        editChecknum.setText("");
//                        emailEditText.setText("");
                        result(getResources().getString(R.string.authSuccess));
                        numCheckBox.setChecked(true);


                    }
                    else{
                        failOnClickHandler(v);
                    }
                }
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
                    KeyboardUp(400);
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
                    retrofitServerManager.checkIdTask(email, new RetrofitServerManager.ServerTaskCallback() {
                        @Override
                        public void onSuccess(String result) {

                            if(result.toLowerCase().contains("true")){
                                // email, password 저장
                                editor.putString("email", email);
                                editor.putString("password", encPw);
                                editor.apply();

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
                            runOnUiThread(() -> Toast.makeText(Activity_Signup_Third.this, "서버 응답 없음", Toast.LENGTH_SHORT).show());
                            Log.e("err", String.valueOf(e));
                        }
                    });

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
            alertString += getResources().getString(R.string.email_Label) + ",";
        }
        if (!passwordCheck) {
            alertString += getResources().getString(R.string.password_Label) + ",";
        }
        if (!reEnterpasswordCheck) {
            alertString += getResources().getString(R.string.rpw_Label) + ",";
        }

        builder.setTitle(getResources().getString(R.string.noti))
                .setMessage(alertString.substring(0, alertString.length() - 1) + getResources().getString(R.string.enterAgain))
                .setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel(); // 팝업 닫기
                    }
                })
                .show();
    }

    public void isIdDuplicate(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getResources().getString(R.string.noti))
                .setMessage(getResources().getString(R.string.dupID))
                .setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 취소 버튼 클릭 시 수행할 동작
                        dialog.cancel(); // 팝업창 닫기
                    }
                })
                .show();
    }

    public void setHintText(){
        // EditText에 힌트 텍스트 스타일을 적용
        String emailHintText = getResources().getString(R.string.id_Label);
        String passwordHintText = getResources().getString(R.string.pw_Label);
        String reEnterPasswordHintText = getResources().getString(R.string.rpw_Label);

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

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void emailOnClickHandler() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.noti))
                .setMessage(getResources().getString(R.string.checkEmail))
                .setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 버튼 클릭 시 수행할 동작
                        signup_idcheck.setEnabled(true);
                        dialog.cancel(); // 팝업창 닫기
                    }
                })
                .show();
    }

    public void numOnClickHandler() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.noti))
                .setMessage(getResources().getString(R.string.sendVerification))
                .setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 확인 버튼 클릭 시 수행할 동작
                        emailCheckBox.setChecked(true);
                        signup_idcheck.setEnabled(true);
                        numlayout.setVisibility(View.VISIBLE);
                        dialog.cancel(); // 팝업창 닫기
                    }
                })
                .show();
    }
    public void failOnClickHandler(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.noti))
                .setMessage(getResources().getString(R.string.confirmVerification))
                .setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 버튼 클릭 시 수행할 동작
                        dialog.cancel(); // 팝업창 닫기
                    }
                })
                .show();
    }

    private void result(String Text){
        Toast.makeText(Activity_Signup_Third.this,Text,Toast.LENGTH_LONG).show();
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
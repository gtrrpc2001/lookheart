package com.mcuhq.simplebluetooth.auth;

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.activity.Activity_Login;
import com.mcuhq.simplebluetooth.server.RetrofitServerManager;
import com.mcuhq.simplebluetooth.signup.Activity_Signup_Fourth;
import com.mcuhq.simplebluetooth.signup.Activity_Signup_Third;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class Find_PwChange extends AppCompatActivity {
    final String passwordPattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&<>*~:`-]).{10,}$";
    RetrofitServerManager retrofitServerManager;
    EditText ch_passwordEditText;
    EditText ch_reEnterPasswordEditText;

    TextView ch_passwordHelp;
    TextView ch_reEnterPasswordHelp;
    Button ChangePw;

    ScrollView sv;

    // 입력 체크
    Map<String, Boolean> dataCheck;
    Boolean ch_passwordCheck;
    Boolean ch_reEnterpasswordCheck;

    Boolean idCheck = false;

    private String password;
    private String reEnterPassword;
    String encPw = "";
    String email = "";

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_pwchange);

        retrofitServerManager = RetrofitServerManager.getInstance();

        // editText 입력창
        ch_passwordEditText = findViewById(R.id.ch_editPassword);
        ch_reEnterPasswordEditText = findViewById(R.id.ch_editReEnterPassword);

        // editText Color
        ColorStateList notMatchColor = ColorStateList.valueOf(Color.RED);
        ColorStateList highlightColor = ColorStateList.valueOf(Color.parseColor("#62AFFF"));

        // 유효성 도움말
        ch_passwordHelp = findViewById(R.id.ch_passwordHelp);
        ch_reEnterPasswordHelp = findViewById(R.id.ch_reEnterPasswordHelp);
        ChangePw = findViewById(R.id.changepw_btn);


        sv = findViewById(R.id.ch_ScrollView);

        // 인스턴스 얻기
        SharedPreferences sharedPref = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);

        // 값 저장 객체 얻기
        SharedPreferences.Editor editor = sharedPref.edit();

        email = sharedPref.getString("email", "null");

        // 입력 데이터 초기화
        dataCheck = new HashMap<>();
        dataCheck.put("password", false);
        dataCheck.put("reEnterPassword", false);

        // 초기화
        ch_passwordCheck = false;
        ch_reEnterpasswordCheck = false;

        // hint Text 설정
        setHintText();

        // password event
        ch_passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // 포커스가 없어질 때
                    if (ch_passwordCheck) {
                        // 입력 값이 유효한 경우
                    }
                    else {
                        // 입력 값이 유효하지 않은 경우
                        ch_passwordHelp.setVisibility(View.VISIBLE);
                        ViewCompat.setBackgroundTintList(ch_passwordEditText, notMatchColor);
                    }
                } else {
                    // 포커스를 얻었을 때
                    ViewCompat.setBackgroundTintList(ch_passwordEditText, highlightColor);
                    KeyboardUp(300);
                }
            }
        });

        ch_passwordEditText.addTextChangedListener(new TextWatcher() {
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
                    ch_passwordHelp.setVisibility(View.GONE);
                } else {
                    // 매칭되지 않는 경우
                    dataCheck.put("password", false);
                }
                // 유효성 체크
                ch_passwordCheck = dataCheck.get("password");
            }
        });


        // reEnterPassword Event
        ch_reEnterPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // 포커스가 없어질 때
                    if (ch_reEnterpasswordCheck) {
                        // 입력 값이 유효한 경우
                    }
                    else {
                        // 입력 값이 유효하지 않은 경우
                        ch_reEnterPasswordHelp.setVisibility(View.VISIBLE);
                        ViewCompat.setBackgroundTintList(ch_reEnterPasswordEditText, notMatchColor);
                    }
                } else {
                    // 포커스를 얻었을 때
                    ViewCompat.setBackgroundTintList(ch_reEnterPasswordEditText, highlightColor);
                    KeyboardUp(300);
                }
            }
        });
        ch_reEnterPasswordEditText.addTextChangedListener(new TextWatcher() {
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
                    ch_reEnterPasswordHelp.setVisibility(View.GONE);
                } else {
                    // 매칭되지 않는 경우
                    dataCheck.put("reEnterPassword", false);
                }
                // 유효성 체크
                ch_reEnterpasswordCheck = dataCheck.get("reEnterPassword");
            }
        });

        ChangePw.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // 입력값 유효성 체크
                if(ch_passwordCheck && ch_reEnterpasswordCheck){

                    try {
                        encPw = encryptECB("MEDSYSLAB.CO.KR.LOOKHEART.ENCKEY", password);
                        encPw = encPw.trim().replaceAll("\\s", "");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    retrofitServerManager.updatePWD(email, encPw, new RetrofitServerManager.ServerTaskCallback() {
                        @Override
                        public void onSuccess(String result) {
                            if (result.toLowerCase().trim().contains("true"))
                                completeLogin(); // 성공
                            else
                                pwOnClickHandler(); // 실패
                        }

                        @Override
                        public void onFailure(Exception e) {
                            runOnUiThread(() -> Toast.makeText(Find_PwChange.this, getResources().getString(R.string.serverErr), Toast.LENGTH_SHORT).show());
                            Log.e("err", String.valueOf(e));
                        }
                    });
                    runOnUiThread(() -> completeLogin());

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

        if (!ch_passwordCheck) {
            alertString += getResources().getString(R.string.password_Label) + ",";
        }
        if (!ch_reEnterpasswordCheck) {
            alertString += getResources().getString(R.string.rpw_Label) + ",";
        }

        builder.setTitle(getResources().getString(R.string.noti))
                .setMessage(alertString.substring(0, alertString.length() - 1) + getResources().getString(R.string.enterAgain))
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
        String passwordHintText = getResources().getString(R.string.pw_Label);
        String reEnterPasswordHintText = getResources().getString(R.string.rpw_Label);

        // 힌트 텍스트에 스타일을 적용
        SpannableString ssPassword = new SpannableString(passwordHintText);
        SpannableString ssReEnterPassword = new SpannableString(reEnterPasswordHintText);

        AbsoluteSizeSpan assEmail = new AbsoluteSizeSpan(13, true); // 힌트 텍스트 크기 설정
        AbsoluteSizeSpan assPassword = new AbsoluteSizeSpan(13, true);
        AbsoluteSizeSpan assReEnterPassword = new AbsoluteSizeSpan(13, true);

        ssPassword.setSpan(assPassword, 0, ssPassword.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssReEnterPassword.setSpan(assReEnterPassword, 0, ssReEnterPassword.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 힌트 텍스트 굵기 설정
        ssPassword.setSpan(new StyleSpan(Typeface.NORMAL), 0, ssPassword.length(), 0);
        ssReEnterPassword.setSpan(new StyleSpan(Typeface.NORMAL), 0, ssReEnterPassword.length(), 0);

        // 스타일이 적용된 힌트 텍스트를 EditText에 설정
        EditText passwordText = (EditText)findViewById(R.id.ch_editPassword);
        EditText reEnterPasswordText = (EditText)findViewById(R.id.ch_editReEnterPassword);

        passwordText.setHint(new SpannedString(ssPassword));
        reEnterPasswordText.setHint(new SpannedString(ssReEnterPassword));

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

    public void completeLogin(){
        alertDialog = new AlertDialog.Builder(Find_PwChange.this)
                .setTitle(getResources().getString(R.string.passwordComp))
                .setMessage(getResources().getString(R.string.returnLogin))
                .setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // 기존 stack에 있는 activity 모두 종료 시키고 login Activity로 이동
//                        Intent intent = new Intent(Find_PwChange.this, Activity_Login.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);

                        dialog.cancel(); // 팝업창 닫기

                        Intent intent = new Intent(getApplicationContext(), Activity_Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        finish();

                    }
                }).create();

        alertDialog.show();
    }

    public void pwOnClickHandler() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.noti))
                .setMessage(getResources().getString(R.string.reconfirmPW))
                .setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 취소 버튼 클릭 시 수행할 동작
                        dialog.cancel(); // 팝업창 닫기
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }
}

package com.mcuhq.simplebluetooth.auth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.server.RetrofitServerManager;


public class Find_Pw extends AppCompatActivity {

    private final String emailPattern = "^[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,20}$";
    RetrofitServerManager retrofitServerManager;
    private Button findPw_btn, findPw_btn2;
    private String email;
    private boolean emailcheck = false;
    String getCode = "";
    EditText editText;

    TextView hinttext, numbertext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_pw);

        retrofitServerManager = RetrofitServerManager.getInstance();

        editText = findViewById(R.id.findPw_email);
        findPw_btn = findViewById(R.id.findPw_btn);
        EditText code = (EditText) findViewById(R.id.findNumber_number);
        findPw_btn2 = findViewById(R.id.findNumber_btn);
        editText.requestFocus();
        hinttext = findViewById(R.id.findPw_hint2);
        numbertext = findViewById(R.id.findPw_text2);


        findPw_btn2.setVisibility(View.INVISIBLE);


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                email = s.toString();

                if (s.toString().trim().matches(emailPattern)) {
                    // 매칭되는 경우
                    emailcheck = true;
                } else {
                    // 매칭되지 않는 경우
                    emailcheck = false;
                }
            }
        });

        findPw_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String inputText = editText.getText().toString().trim();
                hideKeyboard();

                if(inputText.isEmpty()){
                    OnClickHandler(v);
                } else if (emailcheck == false) {
                    emailOnClickHandler();

                } else if(emailcheck){

                    // server에 email 중복 확인 요청
                    retrofitServerManager.checkIdTask(email, new RetrofitServerManager.ServerTaskCallback() {
                        @Override
                        public void onSuccess(String result) {
                            if(!result.toLowerCase().trim().contains("true")){

                                SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                editor.putString("email", email);
                                editor.apply();

                                //send();
                                String getEmail = editText.getText().toString();
                                SendMail mailServer = new SendMail(getEmail);
                                String title = "msl";
                                String content = "lookheart";

                                if (getEmail.length() != 0 && getEmail.contains("@")){
                                    runOnUiThread(() -> numOnClickHandler());
                                    findPw_btn2.setVisibility(View.VISIBLE);
                                   boolean emailChecked = mailServer.sendSecurityCode(getApplicationContext(),title,content);
                                   Log.e("emailChecked", String.valueOf(emailChecked));

                                     getCode = mailServer.getCode();
                                   // code.requestFocus();
                                    if (getCode.equals("")){
                                        result(getResources().getString(R.string.failedPW));
                                    }
                                }else{
                                    result(getResources().getString(R.string.failedPW));
                                }

                            }
                            else {
                                runOnUiThread(() -> isIdDuplicate(v)); // 알림
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            runOnUiThread(() -> Toast.makeText(Find_Pw.this, getResources().getString(R.string.serverErr), Toast.LENGTH_SHORT).show());
                            Log.e("err", String.valueOf(e));
                        }
                    });

                }
            }
        });

        findPw_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codeStr = code.getText().toString();
                if(codeStr.length() != 0){
                    if(codeStr.equals(getCode)){
                        codeStr = "";
                        code.setText("");
                        editText.setText("");
                        result(getResources().getString(R.string.authSuccess));

                        Intent findemailIntent = new Intent(Find_Pw.this, Find_PwChange.class);
                        startActivity(findemailIntent);

                    }
                    else{
                        failOnClickHandler(v);
                    }
                }
            }
        });



    }

    public void OnClickHandler(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.noti))
                .setMessage(getResources().getString(R.string.noEnteredID))
                .setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 취소 버튼 클릭 시 수행할 동작
                        dialog.cancel(); // 팝업창 닫기
                    }
                })
                .show();
    }
    public void emailOnClickHandler() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.noti))
                .setMessage(getResources().getString(R.string.checkEmail))
                .setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 취소 버튼 클릭 시 수행할 동작
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
                        // 취소 버튼 클릭 시 수행할 동작
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
                        // 취소 버튼 클릭 시 수행할 동작
                        dialog.cancel(); // 팝업창 닫기
                    }
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    public void isIdDuplicate(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getResources().getString(R.string.noti))
                .setMessage(getResources().getString(R.string.emailNotExist))
                .setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 취소 버튼 클릭 시 수행할 동작
                        dialog.cancel(); // 팝업창 닫기
                    }
                })
                .show();
    }
    private void result(String Text){
        Toast.makeText(Find_Pw.this,Text,Toast.LENGTH_LONG).show();
    }
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}


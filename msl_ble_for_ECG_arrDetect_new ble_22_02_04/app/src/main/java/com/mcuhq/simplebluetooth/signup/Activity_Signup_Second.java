package com.mcuhq.simplebluetooth.signup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mcuhq.simplebluetooth.LanguageCheck;
import com.mcuhq.simplebluetooth.R;

import java.io.IOException;
import java.io.InputStream;

public class Activity_Signup_Second extends AppCompatActivity {
    TextView privacyTxt;
    ImageButton agreeImageButton;

    Button backButton;
    Button nextButton;

    boolean privacyCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_second);

        privacyTxt = findViewById(R.id.agreeTxt);
        agreeImageButton = findViewById(R.id.agreeImage);
        backButton = findViewById(R.id.signup_second_back);
        nextButton = findViewById(R.id.signup_second_next);

        // 데이터 읽기
        SharedPreferences sharedPref = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        privacyCheck = sharedPref.getBoolean("privacy", false);

        // img set
        if (!privacyCheck) {
            agreeImageButton.setImageResource(R.drawable.signup_agreebutton_normal);
        }
        else {
            agreeImageButton.setImageResource(R.drawable.login_autologin_press);
        }


        try {
            InputStream in;
            switch (LanguageCheck.checklanguage(this)){ // 메소드를 사용하고 읽어오고 싶은 텍스트 파일 읽어옴
                case "en":
                    in = getResources().openRawResource(R.raw.privacy_en);
                    break;
                case "ko":
                    in = getResources().openRawResource(R.raw.privacy);
                    break;
                default:
                    in = getResources().openRawResource(R.raw.privacy_en);
                    break;
            }
            // 현재 읽어올 수 있는 바이트 수 반환
            byte[] b = new byte[in.available()];
            // byte 만큼 데이터를 읽어 b에 저장
            in.read(b);
            // 읽어온 byte를 문자열 형태로 바꿈
            String s = new String(b);
            privacyTxt.setText(s);

        } catch (IOException e) {
            e.printStackTrace();
        }

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
                privacyCheck = sharedPref.getBoolean("privacy", false);
                if (!privacyCheck){
                    OnClickHandler(v);
                }else{
                    Intent signupIntent = new Intent(Activity_Signup_Second.this, Activity_Signup_Third.class);
                    startActivity(signupIntent);
                }

            }
        });
    }

    public void OnClickHandler(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.noti))
                .setMessage(getResources().getString(R.string.notAgree))
                .setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 취소 버튼 클릭 시 수행할 동작
                        dialog.cancel(); // 팝업창 닫기
                    }
                })
                .show();
    }

    public void privacyButtonClickEvent(View v) {
        // 인스턴스 얻기(이름,모드)
        SharedPreferences sharedPref = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        // 값 저장 객체 얻기
        SharedPreferences.Editor editor = sharedPref.edit();
        // 데이터 읽기
        privacyCheck = !privacyCheck;

        if (privacyCheck) {
            // 데이터 저장
            editor.putBoolean("privacy", privacyCheck);
            agreeImageButton.setImageResource(R.drawable.login_autologin_press);
        }else {
            editor.putBoolean("privacy", privacyCheck);
            agreeImageButton.setImageResource(R.drawable.signup_agreebutton_normal);
        }
        editor.apply(); // 비동기 저장
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
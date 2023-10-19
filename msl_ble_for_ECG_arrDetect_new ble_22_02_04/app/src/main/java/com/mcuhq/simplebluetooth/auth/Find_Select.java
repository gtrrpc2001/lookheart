package com.mcuhq.simplebluetooth.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.mcuhq.simplebluetooth.R;

public class Find_Select extends AppCompatActivity {
    Button findEmail;
    Button findPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_select);

        findEmail = findViewById(R.id.findEmail);
        findPw = findViewById(R.id.findPw);


        findEmail.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent findemailIntent = new Intent(Find_Select.this, Find_Email.class);
                startActivity(findemailIntent);
            }
        });


        findPw.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent findpwIntent = new Intent(Find_Select.this, Find_Pw.class);
                startActivity(findpwIntent);
            }
        });





    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

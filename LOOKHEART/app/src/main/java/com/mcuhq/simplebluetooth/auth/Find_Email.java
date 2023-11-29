package com.mcuhq.simplebluetooth.auth;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.server.RetrofitServerManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Find_Email extends AppCompatActivity {

    RetrofitServerManager retrofitServerManager;
    private EditText findEmail_name,findEmail_number;
    private ImageButton findEmail_birth_btn;
    private Button findEmailBtn;
    private String userName, userPhonenumber, userBirthday;
    private TextView find_birth_T,findEmail_age;
    private String finedID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_email);

        retrofitServerManager = RetrofitServerManager.getInstance();

        setViewID();

        setButtonEvent();
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                Find_Email.this,
                R.style.RoundedDatePickerDialog,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                        calendar.set(Calendar.YEAR, selectedYear);
                        calendar.set(Calendar.MONTH, selectedMonth);
                        calendar.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth);

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        String selectedDate = sdf.format(calendar.getTime());

                        // 선택된 날짜를 입력 필드에 표시
                        find_birth_T.setText(selectedDate);

                        // 나이 계산 및 표시
                        int age = calculateAge(calendar.getTime());
                        findEmail_age.setText(getResources().getString(R.string.age) + ": " + age);
                    }
                },
                year, month, dayOfMonth
        );

        datePickerDialog.show();
    }

    void setButtonEvent(){
        findEmail_birth_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        findEmailBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                userName = findEmail_name.getText().toString().trim(); // EditText의 내용을 변수에 저장
                userPhonenumber = findEmail_number.getText().toString().trim();
                userBirthday = find_birth_T.getText().toString().trim();

                if (userName.isEmpty()||userPhonenumber.isEmpty()||userBirthday.isEmpty()) {
                    showAlertDialog();  // 비어있는 경우 알림창 띄우기
                }
                else {
                    // 입력이 있으면 처리 로직 추가
                    // userInput 변수에 저장된 내용 사용 가능
                    try {
                        retrofitServerManager.findID(userName, userPhonenumber, userBirthday, new RetrofitServerManager.ServerTaskCallback() {

                            @Override
                            public void onSuccess(String id) {
                                Log.e("resultemail", id);
                                finedID = id;
                                runOnUiThread(() -> showEmail());
                            }

                            @Override
                            public void onFailure(Exception e) {

                            }

                        });
                    }catch (Exception e){

                    }


                }}
        });
    }

    void setViewID(){
        findEmail_name = findViewById(R.id.findEmail_name);
        findEmail_number = findViewById(R.id.findEmail_number);
        findEmail_birth_btn = findViewById(R.id.findEmail_birth_btn);
        findEmailBtn = findViewById(R.id.findEmailBtn);
        find_birth_T = findViewById(R.id.find_birth_T);
        findEmail_age = findViewById(R.id.findEmail_age);
    }

    private int calculateAge(Date birthDate) {
        Calendar birthCalendar = Calendar.getInstance();
        birthCalendar.setTime(birthDate);
        Calendar currentCalendar = Calendar.getInstance();

        int age = currentCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);
        if (currentCalendar.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.noti))
                .setMessage(getResources().getString(R.string.enterInfo))
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
    private void showEmail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.findIDResult))
                .setMessage(finedID) // 받은 id 값을 사용하여 메시지 설정
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}

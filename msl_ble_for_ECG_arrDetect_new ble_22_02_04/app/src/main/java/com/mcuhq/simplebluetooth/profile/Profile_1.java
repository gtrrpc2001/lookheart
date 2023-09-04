package com.mcuhq.simplebluetooth.profile;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import androidx.lifecycle.ViewModelProvider;

import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.SharedViewModel;

public class Profile_1 extends Fragment {

    SharedViewModel viewModel;

    private Button saveButton;

    private ImageButton birthbutton;
    private TextView editText3,ageTextView;
    private SharedPreferences sharedPreferences;

    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private EditText profile1_name,profile1_number,profile1_height,profile1_weight,profile1_sleep1,profile1_sleep2;
    private boolean profile1check = false;

    private TextView profile1_birth,profile1_gender;

    ScrollView sv;

    public Profile_1() {
        // Required empty public constructor
    }


    int age;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile1,container,false);

        sv = view.findViewById(R.id.scrollView1);

        Button saveButton = view.findViewById(R.id.profile1_save);

        birthbutton = view.findViewById(R.id.profile1_calendar);
        ageTextView = view.findViewById(R.id.profile1_age);

        // SharedPreferences 객체 얻기
        sharedPreferences = getActivity().getSharedPreferences("UserDetails", Context.MODE_PRIVATE);

        // 이미 저장된 생년월일이 있을 경우, TextView에 나이 표시
        String savedBirthday = sharedPreferences.getString("birthday", "");
        ageTextView.setText(calculateAge(savedBirthday));

        // DatePickerDialog의 리스너를 초기화합니다.
        myCalendar = Calendar.getInstance();

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                String birthday = year + "-" + (month + 1) + "-" + dayOfMonth;
                ageTextView.setText(calculateAge(birthday));

                // 생년월일을 선택한 후, 선택한 날짜를 EditText에 표시합니다.
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("birthday", birthday);
                editor.apply();

                updateEditText();

            }

        };
        //생년월일 설정
//        birthbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new DatePickerDialog(
//                        getActivity(),
//                        dateSetListener,
//                        myCalendar.get(Calendar.YEAR),
//                        myCalendar.get(Calendar.MONTH),
//                        myCalendar.get(Calendar.DAY_OF_MONTH)
//                ).show();
//            }
//        });

        //생년월일 설정
        birthbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                String myBirthday = (String) profile1_birth.getText();
                String[] spMyBirthday = myBirthday.split("-");

                int year = Integer.parseInt(spMyBirthday[0]);
                int month = Integer.parseInt(spMyBirthday[1]) - 1;
                int day = Integer.parseInt(spMyBirthday[2]);

                // Create a new instance of DatePickerDialog with spinner style
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        R.style.RoundedDatePickerDialog,  // 새로운 스타일 적용,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String birthday = year + "-" + (month + 1) + "-" + dayOfMonth;
                                ageTextView.setText(calculateAge(birthday));

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("birthday", birthday);

                                editor.apply();
                                profile1_birth.setText(birthday);

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


        //개인정보 edittext
        profile1_name = view.findViewById(R.id.profile1_name);
        profile1_number = view.findViewById(R.id.profile1_number);
        profile1_birth = view.findViewById(R.id.profile1_birth);
        profile1_height = view.findViewById(R.id.profile1_height);
        profile1_weight = view.findViewById(R.id.profile1_weight);
        profile1_gender = view.findViewById(R.id.profile1_gender);
        profile1_sleep1 = view.findViewById(R.id.profile1_sleep1);
        profile1_sleep2 = view.findViewById(R.id.profile1_sleep2);


        Bundle args = getArguments();


        //SharedPreferences에서 개인정보 불러오기
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        String savedText1 = sharedPreferences.getString("name", "null");
        String savedText2 = sharedPreferences.getString("number","01012345678");
        String savedText3 = sharedPreferences.getString("birthday", "");
        String savedText4 = sharedPreferences.getString("height", "180");
        String savedText5 = sharedPreferences.getString("weight", "72");
        String savedText6 = sharedPreferences.getString("gender", "남자");
        String savedText7 = sharedPreferences.getString("sleep1", "23");
        String savedText8 = sharedPreferences.getString("sleep2", "7");
        profile1check = sharedPreferences.getBoolean("profile1check",false);


        profile1_name.setText(savedText1);
        profile1_number.setText(savedText2);
        profile1_birth.setText(savedText3);
        profile1_height.setText(savedText4);
        profile1_weight.setText(savedText5);
        profile1_gender.setText(savedText6);
        profile1_sleep1.setText(savedText7);
        profile1_sleep2.setText(savedText8);


        //개인정보 저장버튼
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textToSave1 = profile1_name.getText().toString();
                String textToSave2 = profile1_number.getText().toString();
                String textToSave3 = profile1_birth.getText().toString();
                String textToSave4 = profile1_gender.getText().toString();
                String textToSave5 = profile1_height.getText().toString();
                String textToSave6 = profile1_weight.getText().toString();
                String textToSave7 = profile1_sleep1.getText().toString();
                String textToSave8 = profile1_sleep2.getText().toString();

                // SharedPreferences를 이용하여 데이터 저장
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name", textToSave1);
                editor.putString("number", textToSave2);
                editor.putString("birthday", textToSave3);
                editor.putString("gender", textToSave4);
                editor.putString("height", textToSave5);
                editor.putString("weight", textToSave6);
                editor.putString("sleep1", textToSave7);
                editor.putString("sleep2", textToSave8);
                editor.putBoolean("profile1check",true);
                editor.apply();

                viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

                viewModel.setAge(calculateAge(textToSave3));
                viewModel.setGender(textToSave4);
                viewModel.setHeight(textToSave5);
                viewModel.setWeight(textToSave6);
                viewModel.setSleep(textToSave7);
                viewModel.setWakeup(textToSave8);

                // 저장한 후, 필요한 작업 수행 (예: 토스트 메시지 표시 등)
                Toast.makeText(getActivity(), "저장되었습니다.", Toast.LENGTH_SHORT).show();



            }
        });

        profile1_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                KeyboardUp(100);
            }

        });

        //입력할때 키보드에 대한 높이조절
        profile1_number.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                KeyboardUp(300);
            }

        });
        profile1_height.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                KeyboardUp(700);
            }

        });
        profile1_weight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                KeyboardUp(700);
            }

        });
        profile1_sleep1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                KeyboardUp(900);
            }

        });
        profile1_sleep2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                KeyboardUp(900);
            }

        });






        return view;
    }
    //생년월일입력
    private void updateEditText() {
        String myFormat = "yyyy-MM-dd"; // 날짜 포맷을 지정합니다.
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        profile1_birth.setText(sdf.format(myCalendar.getTime()));
    }


    //나이 계산
    private String calculateAge(String birthDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date birthDateObj = sdf.parse(birthDate);
            Calendar birthCalendar = Calendar.getInstance();
            birthCalendar.setTime(birthDateObj);

            Calendar nowCalendar = Calendar.getInstance();
            age = nowCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);
            if (nowCalendar.get(Calendar.MONTH) < birthCalendar.get(Calendar.MONTH) ||
                    (nowCalendar.get(Calendar.MONTH) == birthCalendar.get(Calendar.MONTH) &&
                            nowCalendar.get(Calendar.DAY_OF_MONTH) < birthCalendar.get(Calendar.DAY_OF_MONTH))) {
                age--;
            }

            // 계산된 나이를 SharedPreferences에 저장
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("age", age);
            editor.apply();

            return String.valueOf(age);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";

    }

    public void KeyboardUp(int size) {
        sv.postDelayed(new Runnable() {
            @Override
            public void run() {
                sv.smoothScrollTo(0, size);
            }
        }, 200);
    }
}

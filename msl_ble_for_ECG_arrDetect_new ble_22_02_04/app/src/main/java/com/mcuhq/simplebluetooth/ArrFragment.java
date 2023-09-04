package com.mcuhq.simplebluetooth;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ArrFragment extends Fragment {

    SharedViewModel viewModel;

    String currentYear;
    String currentMonth;
    String currentDay;

    String currentDate;
    String currentTime;

    String targetYear;
    String targetMonth;
    String targetDay;
    String targetDate;

    SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");

    SimpleDateFormat year = new SimpleDateFormat("yyyy");
    SimpleDateFormat month = new SimpleDateFormat("MM");
    SimpleDateFormat day = new SimpleDateFormat("dd");

    DateTimeFormatter yearFormat = DateTimeFormatter.ofPattern("yyyy");
    DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MM");
    DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("dd");

    View view;

    LineChart arrChart;
    ScrollView scrollView;
    LinearLayout arrButton;
    LinearLayout arrText;
//    LinearLayout arrDelete;

    ImageButton yesterdayButton;
    ImageButton tomorrowButton;

    TextView dateDisplay;

    ArrayList<Button> buttonList = new ArrayList<>();
    ArrayList<Button> textList = new ArrayList<>();

    TextView status;
    TextView statusText;

    TextView arrStatus;
    TextView arrStatusText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_arr, container, false);

        arrButton = view.findViewById(R.id.arrButton);
        arrText = view.findViewById(R.id.arrText);

        statusText = view.findViewById(R.id.status);
        status = view.findViewById(R.id.statusValue);

        arrStatusText = view.findViewById(R.id.arrStatus);
        arrStatus = view.findViewById(R.id.arrStatusValue);

//        arrDelete = view.findViewById(R.id.arrDeleteButton);

        arrChart = view.findViewById(R.id.fragment_arrChart);

        yesterdayButton = view.findViewById(R.id.yesterdayButton);
        tomorrowButton = view.findViewById(R.id.tomorrowButton);
        dateDisplay = view.findViewById(R.id.dateDisplay);
        scrollView = view.findViewById(R.id.scrollView);

        currentTimeCheck();

        todayArrList();

        yesterdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yesterdayButtonEvent();
            }
        });

        tomorrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tomorrowButtonEvent();
            }
        });

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getArrRefreshCheck().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean check) {
                if(check){
                    arrChart.clear();
                    statusText.setText("");
                    status.setText("");
                    arrStatusText.setText("");
                    arrStatus.setText("");

                    currentTimeCheck();

                    todayArrList();
                    // 최하단 포커스
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }
        });

        return view;
    }

    public void tomorrowButtonEvent() {
        dateCalculate(1, true);

        todayArrList();
    }

    public void yesterdayButtonEvent() {
        dateCalculate(1, false);

        todayArrList();
    }

    public void dateCalculate(int myDay, boolean check) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date;

        if(check){
            // tomorrow
            date = LocalDate.parse(targetDate, formatter);
            date = date.plusDays(myDay);

            targetDate = date.format(formatter);
            System.out.println(targetDate);

        } else{
            // yesterday
            date = LocalDate.parse(targetDate, formatter);
            date = date.minusDays(myDay);

            targetDate = date.format(formatter);
            System.out.println(targetDate);
        }

            /*
            java.util.Date와 java.time.LocalDate는 Java의
            서로 다른 날짜/시간 API를 나타내는 클래스로, 서로 호환되지 않음
            */

        date = LocalDate.parse(targetDate, formatter);

        targetYear = date.format(yearFormat);
        targetMonth = date.format(monthFormat);
        targetDay = date.format(dayFormat);

    }


    public void todayArrList() {

        dateDisplay.setText(targetDate);
        // 자식 뷰 제거
        arrButton.removeAllViews();
        arrText.removeAllViews();
        // 최하단 포커스
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);

        // 경로
        String directoryName = "LOOKHEART/" + targetYear + "/" + targetMonth + "/" + targetDay + "/" + "arrEcgData";
        File directory = new File(getActivity().getFilesDir(), directoryName);

        if (directory.exists()){
            int number = 1;
            String fileName = null;

            // 디렉토리 존재
            File[] files = directory.listFiles();
            // 파일이 있음
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
//                        System.out.println(file.getName());
                        Button button = new Button(getActivity());
                        Button text = new Button(getActivity());
//                        ImageView delete = new ImageButton(getActivity());

                        button.setText(""+number);
                        button.setId(number+1000);
                        text.setId(number);
//                        delete.setImageResource(R.drawable.arr_delete);

                        try {
                            // 파일 생성 시간 = 비정상맥박 발생 시간

                            Path filePath = file.toPath();
                            BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);

                            FileTime creationTime = attrs.creationTime();
                            fileName = String.valueOf(creationTime);

                            Instant instant = Instant.parse(fileName);

                            // 문자열의 날짜 파싱
                            LocalDateTime parsedDate = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

                            // 파일 생성 시간
                            fileName = parsedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // LayoutParams 설정
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,    // 버튼의 너비
                                LinearLayout.LayoutParams.WRAP_CONTENT      // 버튼의 높이
                        );

                        // 마진 설정
                        int margin_in_dp = 5; // 3dp
                        final float scale = getResources().getDisplayMetrics().density;
                        int margin_in_px = (int) (margin_in_dp * scale + 0.5f); // dp를 px로 변환

                        params.setMargins(margin_in_px, margin_in_px, margin_in_px, margin_in_px);

                        button.setLayoutParams(params);
                        text.setLayoutParams(params);
//                        delete.setLayoutParams(params);

                        // 컬러 설정
                        button.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.arr_button_normal));
                        button.setTextColor(Color.WHITE);
                        button.setTextSize(14);
                        button.setTypeface(null, Typeface.BOLD);

                        text.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.home_bottom_button));
                        text.setText(fileName);
                        text.setClickable(false);// 터치 비활성화

//                        delete.setBackgroundColor(Color.WHITE);

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                for (Button otherButton : buttonList) {
                                    otherButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.arr_button_normal));
                                }
                                Button clickedButton = (Button) v;
                                String buttonText = clickedButton.getText().toString();
                                searchArrChart(buttonText);
                                clickedButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.arr_botton_press));

                                Button textButton = getActivity().findViewById(Integer.parseInt((String) clickedButton.getText()));
                                for (Button otherButton : textList) {
                                    otherButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.home_bottom_button));
                                }
                                textButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bpm_border));
                            }
                        });

                        text.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                for (Button otherButton : textList) {
                                    otherButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.home_bottom_button));
                                }
                                Button clickedButton = (Button) v;
                                clickedButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bpm_border));

                                Button button = getActivity().findViewById(Integer.parseInt(String.valueOf(clickedButton.getId()))+ 1000);
                                for (Button otherButton : buttonList) {
                                    otherButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.arr_button_normal));
                                }

                                button.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.arr_botton_press));
                                String buttonText = button.getText().toString();
                                searchArrChart(buttonText);

                            }
                        });

                        buttonList.add(button);
                        textList.add(text);
                        arrButton.addView(button);
                        arrText.addView(text);
//                        arrDelete.addView(delete);

                        number++;

                    }
                }
            }
        }
        else {
            // 디렉토리 없음
        }
    }

    public void searchArrChart(String buttonNumber) {

        arrChart.clear();
        statusText.setText("");
        status.setText("");
        arrStatusText.setText("");
        arrStatus.setText("");


        // 경로
        String directoryName = "LOOKHEART/" + targetYear + "/" + targetMonth + "/" + targetDay + "/" + "arrEcgData";
        File directory = new File(getActivity().getFilesDir(), directoryName);

        // 파일 경로와 이름
        File file = new File(directory, "arrEcgData_" + buttonNumber + ".csv");

        if (file.exists()) {
            // 파일 있음

            ArrayList<Double> arrArrayData = new ArrayList<>();
            List<Entry> entries = new ArrayList<>();

            try {
                // file read
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = br.readLine();
                String[] columns = line.split(",");
                String status = columns[2];
                String arrStatus = columns[3];

                setStatus(status, arrStatus);

                for(int i = 4 ; 500 > i ; i++) {
                    columns = line.split(","); // 데이터 구분
                    Double ecg = Double.parseDouble(columns[i]);

                    arrArrayData.add(ecg);
                }

                // 그래프에 들어갈 데이터 저장
                for (int i = 0; i < arrArrayData.size(); i++) {
                    entries.add(new Entry((float)i, arrArrayData.get(i).floatValue()));
                }

                br.close();

            }catch (IOException e) {
                e.printStackTrace();
            }

// 1
            LineDataSet arrChartDataSet = new LineDataSet(entries, null);
            arrChartDataSet.setDrawCircles(false);
            arrChartDataSet.setColor(Color.BLUE);
            arrChartDataSet.setMode(LineDataSet.Mode.LINEAR);
            arrChartDataSet.setDrawValues(false);

// 2
            LineData arrChartData = new LineData(arrChartDataSet);

            arrChart.setData(arrChartData);
            arrChart.getXAxis().setEnabled(false);
            arrChart.setNoDataText("");

            arrChart.setNoDataText("");
            arrChart.getLegend().setEnabled(false); // 라벨 제거
            arrChart.getAxisLeft().setAxisMaximum(1024);
            arrChart.getAxisLeft().setAxisMinimum(0);
            arrChart.getAxisRight().setEnabled(false);
            arrChart.setDrawMarkers(false);
            arrChart.setDragEnabled(false);
            arrChart.setPinchZoom(false);
            arrChart.setDoubleTapToZoomEnabled(false);
            arrChart.setHighlightPerTapEnabled(false);

            if (arrChart.getDescription() != null) {
                arrChart.getDescription().setEnabled(true);
                arrChart.getDescription().setTextSize(20f); // Note: MPAndroidChart doesn't directly support setting font. Only text size and typeface can be set.
            }

            arrChart.getDescription().setEnabled(false); // 차트 설명
            arrChart.getData().notifyDataChanged();
            arrChart.notifyDataSetChanged();
            arrChart.moveViewToX(0);

        }
        else {
            // 파일 없음
        }
    }

    public void currentTimeCheck() {

        Date mDate;
        Time mTime;

        // 시간 갱신 메서드
        long mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        mTime = new Time(mNow);

        currentYear = year.format(mDate);
        currentMonth = month.format(mDate);
        currentDay = day.format(mDate);

        currentDate = date.format(mDate);
        currentTime = time.format(mTime);

        targetYear = currentYear;
        targetMonth = currentMonth;
        targetDay = currentDay;

        targetDate = currentDate;
    }

    public void setStatus(String myStatus, String myArrStatus){

        statusText.setText("상태 :");
        arrStatusText.setText("종류 :");

        switch (myStatus){
            case "R":
                status.setText("휴식");
                break;
            case "E":
                status.setText("활동");
                break;
            case "S":
                status.setText("수면");
                break;
            default:
                status.setText("휴식");
                break;
        }

        switch (myArrStatus){
            case "arr":
                arrStatus.setText("비정상 맥박");
                break;
            case "fast":
                arrStatus.setText("빠른 맥박");
                break;
            case "slow":
                arrStatus.setText("느린 맥박");
                break;
            case "irregular":
                arrStatus.setText("불규칙 맥박");
                break;
            default:
                arrStatus.setText("비정상 맥박");
                break;
        }
    }
}
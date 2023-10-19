package com.mcuhq.simplebluetooth.summary;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.SharedViewModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SummaryBpm extends Fragment {

    private LineChart bpmChart;
    TextView dateDisplay;
    ImageButton yesterdayButton;
    ImageButton tomorrowButton;

    Button[] buttons;

    Button todayButton;
    Button twoDaysButton;
    Button threeDaysButton;

    TextView minBpm;
    TextView maxBpm;
    TextView avgBpm;

    TextView diffMinBpm;
    TextView diffMaxBpm;

    String currentYear;
    String currentMonth;
    String currentDay;

    String currentDate;
    String currentTime;

    String targetYear;
    String targetMonth;
    String targetDay;
    String targetDate;

    // tartget date를 기준으로 -1
    String twoDaysBpmYear;
    String twoDaysBpmMonth;
    String twoDaysBpmDay;
    String twoDaysBpmDate;

    // tartget date를 기준으로 -2
    String threeDaysBpmYear;
    String threeDaysBpmMonth;
    String threeDaysBpmDay;
    String threeDaysBpmDate;

    Boolean today;
    Boolean twoDays;
    Boolean threeDays;

    int avg = 0;
    int avgSum = 0;
    int avgCnt = 0;
    int max = 0;
    int min = 70;

    View view;

    SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");

    SimpleDateFormat year = new SimpleDateFormat("yyyy");
    SimpleDateFormat month = new SimpleDateFormat("MM");
    SimpleDateFormat day = new SimpleDateFormat("dd");

    DateTimeFormatter yearFormat = DateTimeFormatter.ofPattern("yyyy");
    DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MM");
    DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("dd");

    private String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_summary_bpm, container, false);

        SharedPreferences emailSharedPreferences = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        email = emailSharedPreferences.getString("email", "null");

        avgBpm = view.findViewById(R.id.summaryBpmAvg);
        maxBpm = view.findViewById(R.id.summaryBpmMax);
        minBpm = view.findViewById(R.id.summaryBpmMin);

        diffMaxBpm = view.findViewById(R.id.summaryBpmDiffMax);
        diffMinBpm = view.findViewById(R.id.summaryBpmDiffMin);

        bpmChart = view.findViewById(R.id.BpmChart);
        dateDisplay = view.findViewById(R.id.dateDisplay);

        yesterdayButton = view.findViewById(R.id.yesterdayButton);
        tomorrowButton = view.findViewById(R.id.tomorrowButton);

        todayButton = view.findViewById(R.id.summaryBpmTodayButton);
        twoDaysButton = view.findViewById(R.id.summaryBpmTwoDaysButton);
        threeDaysButton = view.findViewById(R.id.summaryBpmThreeDaysButton);

        buttons = new Button[] {todayButton, twoDaysButton, threeDaysButton};

        min = 70;
        currentTimeCheck();

        todayBpmChartGraph();

        today = true;

        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setColor(todayButton);
                todayBpmChartGraph();

                today = true;
                twoDays = false;
                threeDays = false;
            }
        });

        twoDaysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setColor(twoDaysButton);
                twoDaysBpmChartGraph();

                today = false;
                twoDays = true;
                threeDays = false;
            }
        });

        threeDaysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setColor(threeDaysButton);
                threeDaysBpmChartGraph();

                today = false;
                twoDays = false;
                threeDays = true;
            }
        });


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

        return view;
    }

    public void setColor(Button button) {
        // 클릭 버튼 색상 변경
        button.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.summary_button_press));
        button.setTextColor(Color.WHITE);

        // 그 외 버튼 색상 변경
        for (Button otherButton : buttons) {
            if (otherButton != button) {
                otherButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.summary_botton_noraml2));
                otherButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.lightGray));
            }
        }
    }

    public void tomorrowButtonEvent() {
        dateCalculate(1, true);

        if(today) {
            todayBpmChartGraph();
        }
        else if(twoDays) {
            twoDaysBpmChartGraph();
        }
        else {
            threeDaysBpmChartGraph();
        }
    }

    public void yesterdayButtonEvent() {
        dateCalculate(1, false);

        if(today) {
            todayBpmChartGraph();
        }
        else if(twoDays) {
            twoDaysBpmChartGraph();
        }
        else {
            threeDaysBpmChartGraph();
        }
    }

    public void calcMinMax(int bpm) {
        if (bpm != 0){
            if (min > bpm){
                min = bpm;
            }
            if (max < bpm){
                max = bpm;
            }

            avgSum += bpm;
            avgCnt++;
            avg =  avgSum/avgCnt;
        }
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

        calcDate();
    }

    public void todayBpmChartGraph() {

        dateDisplay.setText(targetDate);
        bpmChart.clear();

        avg = 0;
        avgSum = 0;
        avgCnt = 0;
        max = 0;
        min = 70;

        // 경로
        String directoryName = "LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth + "/" + targetDay;
        File directory = new File(getActivity().getFilesDir(), directoryName);

        // 파일 경로와 이름
        File file = new File(directory, "BpmData.csv");

        if (file.exists()) {
            // 파일이 있는 경우

            // bpm data가 저장되는 배열 리스트
            ArrayList<Double> bpmArrayData = new ArrayList<>();
            // bpm time data가 저장되는 배열 리스트
            ArrayList<String> bpmTimeData = new ArrayList<>();
            // 그래프의 x축(시간) y축(데이터)이 저장되는 Entry 리스트
            List<Entry> entries = new ArrayList<>();

            try {
                // file read
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    String[] columns = line.split(","); // 데이터 구분
                    Double bpmDataRow = Double.parseDouble(columns[2]); // bpm data
                    int bpm = Integer.parseInt(columns[2]); // minMaxAvg 찾는 변수

                    String[] bpmTimeCheck = columns[0].split(":"); // 시간 구분
                    String myBpmTimeRow = bpmTimeCheck[0] + ":" + bpmTimeCheck[1]; // 초 단위 제거

                    calcMinMax(bpm);

                    // 데이터 저장
                    bpmTimeData.add(myBpmTimeRow);
                    bpmArrayData.add(bpmDataRow);
                }

                // 그래프에 들어갈 데이터 저장
                for (int i = 0; i < bpmArrayData.size(); i++) {
                    entries.add(new Entry((float)i, bpmArrayData.get(i).floatValue()));
                }

                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 그래프 Set
            LineDataSet dataSet = new LineDataSet(entries, "BPM");
            dataSet.setDrawCircles(false);
            dataSet.setColor(Color.BLUE);
            dataSet.setLineWidth(0.5f);
            dataSet.setDrawValues(true);

            LineData lineData = new LineData(dataSet);
            bpmChart.setData(lineData);

            LineData bpmChartData = new LineData(dataSet);
            bpmChart.setData(bpmChartData);  // 차트에 표시되는 데이터 설정
            bpmChart.setNoDataText(""); // 데이터가 없는 경우 차트에 표시되는 텍스트 설정
            bpmChart.getXAxis().setEnabled(true);   // x축 활성화(true)
            bpmChart.getLegend().setTextSize(15f);  // 범례 텍스트 크기 설정("BPM" size)
            bpmChart.getLegend().setTypeface(Typeface.DEFAULT_BOLD);
            bpmChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(bpmTimeData));    // x축의 값 설정
            bpmChart.setVisibleXRangeMaximum(500);  // 한 번에 보여지는 x축 최대 값
            bpmChart.getXAxis().setGranularity(1f); // 축의 최소 간격
            bpmChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // x축 위치
            bpmChart.getXAxis().setDrawGridLines(false);    // 축의 그리드 선
            bpmChart.getDescription().setEnabled(false);    // 차트 설명

            bpmChart.getAxisLeft().setAxisMaximum(200f); // y 축 최대값
            bpmChart.getAxisLeft().setAxisMinimum(40f); // y 축 최소값
            bpmChart.getAxisRight().setEnabled(false);  // 참조 반환
            bpmChart.setDrawMarkers(false); // 값 마커
            bpmChart.setDragEnabled(true);  // 드래그 기능
            bpmChart.setPinchZoom(false);   // 줌 기능
            bpmChart.setDoubleTapToZoomEnabled(false);  // 더블 탭 줌 기능
            bpmChart.setHighlightPerDragEnabled(false); // 드래그 시 하이라이트

            bpmChart.getData().notifyDataChanged(); // 차트에게 데이터가 변경되었음을 알림
            bpmChart.notifyDataSetChanged();    // 차트에게 데이터가 변경되었음을 알림
            bpmChart.moveViewToX(0);    // 주어진 x값의 위치로 뷰 이동

            bpmChart.invalidate(); // 차트 다시 그림

        }
        else {
            // 파일이 없는 경우
        }

        // 줌 인 상태에서 다른 그래프 봤을 경우 대비 줌 아웃
        for(int i = 0 ; 20 > i ; i++) {
            bpmChart.zoomOut();
        }

        maxBpm.setText(""+max);
        minBpm.setText(""+min);
        avgBpm.setText(""+avg);
        diffMinBpm.setText("-"+(avg-min));
        diffMaxBpm.setText("+"+(max-avg));
    }

    public void twoDaysBpmChartGraph() {

        bpmChart.clear();
        dateDisplay.setText(twoDaysBpmMonth + "-" + twoDaysBpmDay + " ~ " + targetMonth + "-" + targetDay);

        avg = 0;
        avgSum = 0;
        avgCnt = 0;
        max = 0;
        min = 70;


        // 경로
        String directoryName = "LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth + "/" + targetDay;
        File directory = new File(getActivity().getFilesDir(), directoryName);

        // 파일 경로와 이름
        File targetFile = new File(directory, "BpmData.csv");

        // 경로
        directoryName = "LOOKHEART/" + email + "/" + twoDaysBpmYear + "/" + twoDaysBpmMonth + "/" + twoDaysBpmDay;
        directory = new File(getActivity().getFilesDir(), directoryName);

        // 파일 경로와 이름
        File twoDaysBpmFile = new File(directory, "BpmData.csv");

//        Log.d("targetFile", String.valueOf(targetFile));
//        Log.d("twoDaysBpmFile", String.valueOf(twoDaysBpmFile));

        if (targetFile.exists() && twoDaysBpmFile.exists()) {
            // 파일이 있는 경우

            /*
            target : 기준일
            twoDays : 기준일 -1
             */

            // bpm data가 저장되는 배열 리스트
            ArrayList<Double> targetBpmArrayData = new ArrayList<>();
            ArrayList<Double> twoDaysBpmArrayData = new ArrayList<>();

            // bpm time data가 저장되는 배열 리스트
            ArrayList<String> targetBpmTimeData = new ArrayList<>();
            ArrayList<String> twoDaysBpmTimeData = new ArrayList<>();

            // 그래프의 x축(시간) y축(데이터)이 저장되는 Entry 리스트
            List<Entry> targetEntries = new ArrayList<>();
            List<Entry> twoDaysEntries = new ArrayList<>();

            // target(기준일) 데이터 저장
            try {
                // file read
                BufferedReader br = new BufferedReader(new FileReader(targetFile));
                String line;

                while ((line = br.readLine()) != null) {
                    String[] columns = line.split(","); // 데이터 구분
                    Double bpmDataRow = Double.parseDouble(columns[2]); // bpm data
                    int bpm = Integer.parseInt(columns[2]); // minMaxAvg 찾는 변수

                    String[] bpmTimeCheck = columns[0].split(":"); // 시간 구분
                    String myBpmTimeRow = bpmTimeCheck[0] + ":" + bpmTimeCheck[1] + ":" + bpmTimeCheck[2];

                    calcMinMax(bpm);

                    // 데이터 저장
                    targetBpmTimeData.add(myBpmTimeRow);
                    targetBpmArrayData.add(bpmDataRow);
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // twoDays(이전일) 데이터 저장
            try {
                // file read
                BufferedReader br = new BufferedReader(new FileReader(twoDaysBpmFile));
                String line;

                while ((line = br.readLine()) != null) {
                    String[] columns = line.split(","); // 데이터 구분
                    Double bpmDataRow = Double.parseDouble(columns[2]); // bpm data
                    int bpm = Integer.parseInt(columns[2]); // minMaxAvg 찾는 변수

                    String[] bpmTimeCheck = columns[0].split(":"); // 시간 구분
                    String myBpmTimeRow = bpmTimeCheck[0] + ":" + bpmTimeCheck[1] + ":" + bpmTimeCheck[2];

                    calcMinMax(bpm);
                    // 데이터 저장
                    twoDaysBpmTimeData.add(myBpmTimeRow);
                    twoDaysBpmArrayData.add(bpmDataRow);
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*
            X 축 타임 테이블을 위해 시작 시간과 종료 시간을 구함
            */

            // 기준일 배열의 시작과 끝의 시간 값
            String[] startOfToday =  targetBpmTimeData.get(0).split(":");
            String[] endOfToday = targetBpmTimeData.get(targetBpmTimeData.size() - 1).split(":");

            // 이전일 배열의 시작과 끝의 시간 값
            String[] startOfYesterday =  twoDaysBpmTimeData.get(0).split(":");
            String[] endOfYesterday = twoDaysBpmTimeData.get(twoDaysBpmTimeData.size() - 1).split(":");

            int intTargetStartHour = Integer.parseInt(startOfToday[0]);
            int intTargetStartMinute = Integer.parseInt(startOfToday[1]);
            int intTargetEndHour = Integer.parseInt(endOfToday[0]);
            int intTargetEndMinute = Integer.parseInt(endOfToday[1]);

            int intTwoDaysStartHour = Integer.parseInt(startOfYesterday[0]);
            int intTwoDaysStartMinute = Integer.parseInt(startOfYesterday[1]);
            int intTwoDaysEndHour = Integer.parseInt(endOfYesterday[0]);
            int intTwoDaysEndMinute = Integer.parseInt(endOfYesterday[1]);

//            Log.d("startOfToday", intTargetStartHour +":"+intTargetStartMinute);
//            Log.d("endOfToday", intTargetEndHour +":"+intTargetEndMinute);
//            Log.d("startOfYesterday", intTwoDaysStartHour +":"+intTwoDaysStartMinute);
//            Log.d("endOfYesterday", intTwoDaysEndHour +":"+intTwoDaysEndMinute);

            /*
            제일 빠른 시작 시간과 제일 느린 시간을 저장하는 배열
            시작 시간과 종료 시간의 차이 값을 알아내기 위함
             */
            int[] startTime = new int[2];
            int[] endTime = new int[2];

            int hourDifference = 0; // 시간 차이
            int minuteDifference = 0; // 분 차이

            int totalXValue = 0; // x value

            // 시간 비교 LocalTime 변수
            LocalTime targetStartTime = LocalTime.of(intTargetStartHour, intTargetStartMinute);
            LocalTime targetEndTime = LocalTime.of(intTargetEndHour, intTargetEndMinute);

            LocalTime twoDaysStartTime = LocalTime.of(intTwoDaysStartHour, intTwoDaysStartMinute);
            LocalTime twoDaysEndTime = LocalTime.of(intTwoDaysEndHour, intTwoDaysEndMinute);

            // 시작 시간 비교
            if (targetStartTime.isBefore(twoDaysStartTime)){
                // targetStartTime가 더 빠른 경우
                startTime[0] = intTargetStartHour;
                startTime[1] = intTargetStartMinute;
            }else {
                // twoDaysStartTime 더 빠른 경우
                startTime[0] = intTwoDaysStartHour;
                startTime[1] = intTwoDaysStartMinute;
            }

            // 종료 시간 비교
            if (targetEndTime.isAfter(twoDaysEndTime)){
                // targetEndTime 가 더 빠른 경우
                endTime[0] = intTargetEndHour;
                endTime[1] = intTargetEndMinute;
            }else {
                // twoDaysEndTime 가 더 느린 경우
                endTime[0] = intTwoDaysEndHour;
                endTime[1] = intTwoDaysEndMinute;
            }

//            Log.d("startTime", Arrays.toString(startTime));
//            Log.d("endTime", Arrays.toString(endTime));

            // 시간 차이 계산
            LocalTime localStartTime = LocalTime.of(startTime[0], startTime[1]);
            LocalTime localEndTime = LocalTime.of(endTime[0], endTime[1]);

            Duration duration = Duration.between(localStartTime, localEndTime);

            long totalDiffInMinutes = duration.toMinutes();
            minuteDifference = (int) (totalDiffInMinutes % 60); // 분 차이
            hourDifference = (int) duration.toHours();  // 시간 차이

//            Log.d("minuteDifference", String.valueOf(minuteDifference));
//            Log.d("hourDifference", String.valueOf(hourDifference));

            // x축 개수
            totalXValue = (hourDifference * 360) + (minuteDifference * 6);
            // 시간값이 들어가는 테이블
            ArrayList<String> timeTable = new ArrayList<>();

            // 시간 시간(정수)
            int intStartHour = startTime[0];
            int intStartMinute = startTime[1];
            int secondCnt = 0;

            // 시간 시간(문자열)
            String StringStartHour = String.valueOf(startTime[0]);
            String StringStartMinute = String.valueOf(startTime[1]);


            // x 시간 축
            for(int i = 0; totalXValue > i ; i++) {
                String timeHour = String.valueOf(intStartHour);
                String timeMinute = String.valueOf(intStartMinute);
                String time;

                // hour, minute가 1의 자리 숫자인 경우 문자열 비교를 위해 앞에 0 추가
                if (intStartHour < 10){
                    timeHour = "0" + intStartHour;
                }
                if (intStartMinute < 10){
                    timeMinute  = "0" + intStartMinute;
                }

                // timeTable에 time 값 추가
                time = timeHour + ":" + timeMinute + ":" + secondCnt;
                timeTable.add(time);
                secondCnt++;

                // 초 -> 분
                if ( secondCnt == 6 ){
                    if(intStartMinute < 9) {
                        StringStartMinute = "0" + (intStartMinute + 1);
                    }
                    else {
                        StringStartMinute = String.valueOf(intStartMinute + 1);
                    }
                    intStartMinute++;
                    secondCnt = 0;
                }

                // 분 -> 시
                if (StringStartMinute.equals("60")){
                    if (intStartMinute < 9) {
                        StringStartHour = "0" + (intStartHour + 1);
                    }
                    else {
                        StringStartHour = String.valueOf(intStartHour + 1);
                    }
                    StringStartMinute = "00";
                    intStartHour++;
                    intStartMinute = 0;
                }
            }

            int bpmTimeCount = 0;
            int timeTableCount = 0;

//            Log.d("timeTable", String.valueOf(timeTable));

            // target(기준일) 그래프 시작 포인트
            LocalTime localTargetTime = LocalTime.of(intTargetStartHour, intTargetStartMinute);

            duration = Duration.between(localStartTime, localTargetTime);

            totalDiffInMinutes = duration.toMinutes();
            minuteDifference = (int) (totalDiffInMinutes % 60); // 분 차이
            hourDifference = (int) duration.toHours();  // 시간 차이

            timeTableCount = (hourDifference * 360) + (minuteDifference * 6);

//            Log.d("line", "oooooooooooooooooooooo");
//            Log.d("bpmTime", Arrays.toString(startTime));
//            Log.d("bpmSecond", String.valueOf(endTime));

            try{
                // Target Graph
                for( int i = 0 ; totalXValue - 1 > i ; i++) {
                    String[] bpmTime = targetBpmTimeData.get(bpmTimeCount).split(":");
                    String[] checkTimeTable = timeTable.get(timeTableCount).split(":");
                    String bpmSecond = String.valueOf(bpmTime[2].charAt(0));

//                Log.d("bpmTime", Arrays.toString(bpmTime));
//                Log.d("bpmSecond", String.valueOf(bpmSecond));
//                Log.d("checkTimeTable", Arrays.toString(checkTimeTable));

                    boolean check = false;

                    // 값이 있는 경우
                    if(bpmTime[0].equals(checkTimeTable[0])){ // hour
                        if(bpmTime[1].equals(checkTimeTable[1])) { // minute
                            if(bpmSecond.equals(checkTimeTable[2])) { // second

                                Entry bpmDataEntry = new Entry((float)timeTableCount, targetBpmArrayData.get(bpmTimeCount).floatValue());
                                targetEntries.add(bpmDataEntry);

//                            Log.d("check", String.valueOf(bpmDataEntry));

                                bpmTimeCount += 1; // 값이 있으니까 +1
                                check = true;
                            }
                        } else {
                            // 분 값이 없는 경우 이전 값을 씀
                            if(bpmTimeCount > 0) {
                                Entry bpmDataEntry = new Entry((float)timeTableCount, targetBpmArrayData.get(bpmTimeCount-1).floatValue());
                                targetEntries.add(bpmDataEntry);
                            }
                        }
                    } else {
                        // 시간 값이 없는 경우 이전 값을 씀
                        if(bpmTimeCount > 0) {
                            Entry bpmDataEntry = new Entry((float)timeTableCount, targetBpmArrayData.get(bpmTimeCount-1).floatValue());
                            targetEntries.add(bpmDataEntry);
                        }
                    }

                    // 같은 초가 나오는 경우 현재 타임 테이블 값과 다음값 비교 (ex: 10 -> 19)
                    if (check  && bpmTimeCount < targetBpmArrayData.size()) {
                        bpmTime = targetBpmTimeData.get(bpmTimeCount).split(":");
                        bpmSecond = String.valueOf(bpmTime[2].charAt(0));

                        while (bpmSecond.equals(checkTimeTable[2])){
                            Entry bpmDataEntry = new Entry((float)timeTableCount, targetBpmArrayData.get(bpmTimeCount).floatValue());
                            targetEntries.add(bpmDataEntry);

                            bpmTimeCount += 1;
                            // 마지막 값인지 확인
                            bpmTime = targetBpmTimeData.get(bpmTimeCount).split(":");
                            bpmSecond = String.valueOf(bpmTime[2].charAt(0));
                        }
                    }

                    timeTableCount += 1;

                    // 마지막 값 확인
                    if(targetBpmTimeData.size() - 10 < bpmTimeCount){
                        break;
                    }
                }

                bpmTimeCount = 0;
                timeTableCount = 0;

                // twoDays(이전일) 그래프 시작 포인트
                LocalTime localTwoDaysTime = LocalTime.of(intTwoDaysStartHour, intTwoDaysStartMinute);

                duration = Duration.between(localStartTime, localTwoDaysTime);

                totalDiffInMinutes = duration.toMinutes();
                minuteDifference = (int) (totalDiffInMinutes % 60); // 분 차이
                hourDifference = (int) duration.toHours();  // 시간 차이

                timeTableCount = (hourDifference * 360) + (minuteDifference * 6);

//            Log.d("minuteDifference", String.valueOf(minuteDifference));
//            Log.d("hourDifference", String.valueOf(hourDifference));
//            Log.d("timeTableCount", String.valueOf(timeTableCount));

                // twoDays Graph
                for( int i = 0 ; totalXValue - 1 > i ; i++) {
                    String[] bpmTime = twoDaysBpmTimeData.get(bpmTimeCount).split(":");
                    String[] checkTimeTable = timeTable.get(timeTableCount).split(":");
                    String bpmSecond = String.valueOf(bpmTime[2].charAt(0));
//                Log.d("bpmTime", Arrays.toString(bpmTime));
//                Log.d("bpmSecond", String.valueOf(bpmSecond));
//                Log.d("checkTimeTable", Arrays.toString(checkTimeTable));

                    boolean check = false;

                    // 값이 있는 경우
                    if(bpmTime[0].equals(checkTimeTable[0])){ // hour
                        if(bpmTime[1].equals(checkTimeTable[1])) { // minute
                            if(bpmSecond.equals(checkTimeTable[2])) { // second

                                Entry bpmDataEntry = new Entry((float)timeTableCount, twoDaysBpmArrayData.get(bpmTimeCount).floatValue());
                                twoDaysEntries.add(bpmDataEntry);

//                            Log.d("check", String.valueOf(bpmDataEntry));

                                bpmTimeCount += 1; // 값이 있으니까 +1
                                check = true;
                            }
                        } else {
                            // 분 값이 없는 경우 이전 값을 씀
                            if(bpmTimeCount > 0){
                                Entry bpmDataEntry = new Entry((float)timeTableCount, twoDaysBpmArrayData.get(bpmTimeCount-1).floatValue());
                                twoDaysEntries.add(bpmDataEntry);
                            }
                        }
                    } else {
                        // 시간 값이 없는 경우 이전 값을 씀
                        if(bpmTimeCount > 0){
                            Entry bpmDataEntry = new Entry((float)timeTableCount, twoDaysBpmArrayData.get(bpmTimeCount-1).floatValue());
                            twoDaysEntries.add(bpmDataEntry);
                        }
                    }

                    // 같은 초가 나오는 경우 현재 타임 테이블 값과 다음값 비교 (ex: 10 -> 19)
                    if (check  && bpmTimeCount < twoDaysBpmArrayData.size()) {
                        bpmTime = twoDaysBpmTimeData.get(bpmTimeCount).split(":");
                        bpmSecond = String.valueOf(bpmTime[2].charAt(0));

                        while (bpmSecond.equals(checkTimeTable[2])){
                            Entry bpmDataEntry = new Entry((float)timeTableCount, twoDaysBpmArrayData.get(bpmTimeCount).floatValue());
                            twoDaysEntries.add(bpmDataEntry);

                            bpmTimeCount += 1;
                            // 마지막 값인지 확인
                            bpmTime = twoDaysBpmTimeData.get(bpmTimeCount).split(":");
                            bpmSecond = String.valueOf(bpmTime[2].charAt(0));
                        }
                    }

                    timeTableCount += 1;

                    // 마지막 값 확인
                    if(twoDaysBpmTimeData.size() - 10 < bpmTimeCount){
                        break;
                    }
                }


                timeTable.clear();

                // 시간 시간(정수)
                intStartHour = startTime[0];
                intStartMinute = startTime[1];
                secondCnt = 0;

                // 시간 시간(문자열)
                StringStartHour = String.valueOf(startTime[0]);
                StringStartMinute = String.valueOf(startTime[1]);

                // remove second
                for(int i = 0; totalXValue > i ; i++) {
                    String time = StringStartHour+ ":" + StringStartMinute;

                    timeTable.add(time);
                    secondCnt++;

                    // 초 -> 분
                    if ( secondCnt == 6 ){
                        if(intStartMinute < 9) {
                            StringStartMinute = "0" + (intStartMinute + 1);
                        }
                        else {
                            StringStartMinute = String.valueOf(intStartMinute + 1);
                        }
                        intStartMinute += 1;
                        secondCnt = 0;
                    }

                    // 분 -> 시
                    if (StringStartMinute.equals("60")){
                        if (intStartMinute < 9) {
                            StringStartHour = "0" + (intStartHour + 1);
                        }
                        else {
                            StringStartHour = String.valueOf(intStartHour + 1);
                        }
                        StringStartMinute = "00";
                        intStartHour++;
                        intStartMinute = 0;
                    }
                }
            }catch (Exception e){
//                Log.d("Log", e.);
            }

            // 그래프 Set
            LineDataSet targetDataSet = new LineDataSet(targetEntries, targetMonth+"-"+targetDay);
            targetDataSet.setDrawCircles(false);
            targetDataSet.setColor(Color.RED);
            targetDataSet.setLineWidth(0.5f);
            targetDataSet.setDrawValues(true);

            // 그래프 Set
            LineDataSet twoDaysDataSet = new LineDataSet(twoDaysEntries, twoDaysBpmMonth+"-"+twoDaysBpmDay);
            twoDaysDataSet.setDrawCircles(false);
            twoDaysDataSet.setColor(Color.BLUE);
            twoDaysDataSet.setLineWidth(0.5f);
            twoDaysDataSet.setDrawValues(true);

            ArrayList<ILineDataSet> twoDaysBpmChartdataSets = new ArrayList<>();
            twoDaysBpmChartdataSets.add(twoDaysDataSet);
            twoDaysBpmChartdataSets.add(targetDataSet);

            LineData twoDaysBpmChartData = new LineData(twoDaysBpmChartdataSets);

            bpmChart.setData(twoDaysBpmChartData);
            bpmChart.setNoDataText("");// 데이터가 없는 경우 차트에 표시되는 텍스트 설정
            bpmChart.getXAxis().setEnabled(true);   // x축 활성화(true)
            bpmChart.getLegend().setTextSize(15f);  // 범례 텍스트 크기 설정("BPM" size)
            bpmChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(timeTable));    // x축의 값 설정
            bpmChart.setVisibleXRangeMaximum(500);  // 한 번에 보여지는 x축 최대 값
            bpmChart.getXAxis().setGranularity(1f); // 축의 최소 간격
            bpmChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // x축 위치
            bpmChart.getXAxis().setDrawGridLines(false);    // 축의 그리드 선
            bpmChart.getDescription().setEnabled(false);    // 차트 설명

            bpmChart.getAxisLeft().setAxisMaximum(200f); // y 축 최대값
            bpmChart.getAxisLeft().setAxisMinimum(40f); // y 축 최소값
            bpmChart.getAxisRight().setEnabled(false);  // 참조 반환
            bpmChart.setDrawMarkers(false); // 값 마커
            bpmChart.setDragEnabled(true);  // 드래그 기능
            bpmChart.setPinchZoom(false);   // 줌 기능
            bpmChart.setDoubleTapToZoomEnabled(false);  // 더블 탭 줌 기능
            bpmChart.setHighlightPerDragEnabled(false); // 드래그 시 하이라이트

            bpmChart.getData().notifyDataChanged(); // 차트에게 데이터가 변경되었음을 알림
            bpmChart.notifyDataSetChanged();    // 차트에게 데이터가 변경되었음을 알림
            bpmChart.moveViewToX(0);    // 주어진 x값의 위치로 뷰 이동

            bpmChart.invalidate(); // 차트 다시 그림

            // 줌 인 상태에서 다른 그래프 봤을 경우 대비 줌 아웃
            for(int i = 0 ; 20 > i ; i++) {
                bpmChart.zoomOut();
            }

            maxBpm.setText(""+max);
            minBpm.setText(""+min);
            avgBpm.setText(""+avg);
            diffMinBpm.setText("-"+(avg-min));
            diffMaxBpm.setText("+"+(max-avg));

        }
        else {
            // 파일이 없는 경우
        }
    }

    public void threeDaysBpmChartGraph() {

        bpmChart.clear();
        dateDisplay.setText(threeDaysBpmMonth + "-" + threeDaysBpmDay + " ~ " + targetMonth + "-" + targetDay);

        // 경로
        String directoryName = "LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth + "/" + targetDay;
        File directory = new File(getActivity().getFilesDir(), directoryName);

        // 파일 경로와 이름
        File targetFile = new File(directory, "BpmData.csv");

        // 경로
        directoryName = "LOOKHEART/" + email + "/" + twoDaysBpmYear + "/" + twoDaysBpmMonth + "/" + twoDaysBpmDay;
        directory = new File(getActivity().getFilesDir(), directoryName);

        // 파일 경로와 이름
        File twoDaysBpmFile = new File(directory, "BpmData.csv");

        // 경로
        directoryName = "LOOKHEART/" + email + "/" + threeDaysBpmYear + "/" + threeDaysBpmMonth + "/" + threeDaysBpmDay;
        directory = new File(getActivity().getFilesDir(), directoryName);

        // 파일 경로와 이름
        File threeDaysBpmFile = new File(directory, "BpmData.csv");

        if (targetFile.exists() && twoDaysBpmFile.exists() && threeDaysBpmFile.exists()) {
            // 파일이 있는 경우

            /*
            target : 기준일
            twoDays : 기준일 -2
             */

            // bpm data가 저장되는 배열 리스트
            ArrayList<Double> targetBpmArrayData = new ArrayList<>();
            ArrayList<Double> twoDaysBpmArrayData = new ArrayList<>();
            ArrayList<Double> threeDaysBpmArrayData = new ArrayList<>();

            // bpm time data가 저장되는 배열 리스트
            ArrayList<String> targetBpmTimeData = new ArrayList<>();
            ArrayList<String> twoDaysBpmTimeData = new ArrayList<>();
            ArrayList<String> threeDaysBpmTimeData = new ArrayList<>();

            // 그래프의 x축(시간) y축(데이터)이 저장되는 Entry 리스트
            List<Entry> targetEntries = new ArrayList<>();
            List<Entry> twoDaysEntries = new ArrayList<>();
            List<Entry> threeDaysEntries = new ArrayList<>();

            // target(기준일) 데이터 저장
            try {
                // file read
                BufferedReader br = new BufferedReader(new FileReader(targetFile));
                String line;

                while ((line = br.readLine()) != null) {
                    String[] columns = line.split(","); // 데이터 구분
                    Double bpmDataRow = Double.parseDouble(columns[2]); // bpm data
                    int bpm = Integer.parseInt(columns[2]); // minMaxAvg 찾는 변수

                    String[] bpmTimeCheck = columns[0].split(":"); // 시간 구분
                    String myBpmTimeRow = bpmTimeCheck[0] + ":" + bpmTimeCheck[1] + ":" + bpmTimeCheck[2];

                    calcMinMax(bpm);

                    // 데이터 저장
                    targetBpmTimeData.add(myBpmTimeRow);
                    targetBpmArrayData.add(bpmDataRow);
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // twoDays(이틀전) 데이터 저장
            try {
                // file read
                BufferedReader br = new BufferedReader(new FileReader(twoDaysBpmFile));
                String line;

                while ((line = br.readLine()) != null) {
                    String[] columns = line.split(","); // 데이터 구분
                    Double bpmDataRow = Double.parseDouble(columns[2]); // bpm data
                    int bpm = Integer.parseInt(columns[2]); // minMaxAvg 찾는 변수

                    String[] bpmTimeCheck = columns[0].split(":"); // 시간 구분
                    String myBpmTimeRow = bpmTimeCheck[0] + ":" + bpmTimeCheck[1] + ":" + bpmTimeCheck[2];

                    calcMinMax(bpm);

                    // 데이터 저장
                    twoDaysBpmTimeData.add(myBpmTimeRow);
                    twoDaysBpmArrayData.add(bpmDataRow);
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // threeDays(엊그제) 데이터 저장
            try {
                // file read
                BufferedReader br = new BufferedReader(new FileReader(threeDaysBpmFile));
                String line;

                while ((line = br.readLine()) != null) {
                    String[] columns = line.split(","); // 데이터 구분
                    Double bpmDataRow = Double.parseDouble(columns[2]); // bpm data
                    int bpm = Integer.parseInt(columns[2]); // minMaxAvg 찾는 변수

                    String[] bpmTimeCheck = columns[0].split(":"); // 시간 구분
                    String myBpmTimeRow = bpmTimeCheck[0] + ":" + bpmTimeCheck[1] + ":" + bpmTimeCheck[2];

                    calcMinMax(bpm);

                    // 데이터 저장
                    threeDaysBpmTimeData.add(myBpmTimeRow);
                    threeDaysBpmArrayData.add(bpmDataRow);
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*
            X 축 타임 테이블을 위해 시작 시간과 종료 시간을 구함
            */

            // 기준일 배열의 시작과 끝의 시간 값
            String[] startOfToday =  targetBpmTimeData.get(0).split(":");
            String[] endOfToday = targetBpmTimeData.get(targetBpmTimeData.size() - 1).split(":");

            // 이틀전 배열의 시작과 끝의 시간 값
            String[] startOfYesterday =  twoDaysBpmTimeData.get(0).split(":");
            String[] endOfYesterday = twoDaysBpmTimeData.get(twoDaysBpmTimeData.size() - 1).split(":");

            // 엊그제 배열의 시작과 끝의 시간 값
            String[] startOfTwoDaysAgo =  threeDaysBpmTimeData.get(0).split(":");
            String[] endOfTwoDaysAgo = threeDaysBpmTimeData.get(threeDaysBpmTimeData.size() - 1).split(":");

            int intTargetStartHour = Integer.parseInt(startOfToday[0]);
            int intTargetStartMinute = Integer.parseInt(startOfToday[1]);
            int intTargetEndHour = Integer.parseInt(endOfToday[0]);
            int intTargetEndMinute = Integer.parseInt(endOfToday[1]);

            int intTwoDaysStartHour = Integer.parseInt(startOfYesterday[0]);
            int intTwoDaysStartMinute = Integer.parseInt(startOfYesterday[1]);
            int intTwoDaysEndHour = Integer.parseInt(endOfYesterday[0]);
            int intTwoDaysEndMinute = Integer.parseInt(endOfYesterday[1]);

            int intThreeDaysStartHour = Integer.parseInt(startOfTwoDaysAgo[0]);
            int intThreeDaysStartMinute = Integer.parseInt(startOfTwoDaysAgo[1]);
            int intThreeDaysEndHour = Integer.parseInt(endOfTwoDaysAgo[0]);
            int intThreeDaysEndMinute = Integer.parseInt(endOfTwoDaysAgo[1]);

                        /*
            제일 빠른 시작 시간과 제일 느린 시간을 저장하는 배열
            시작 시간과 종료 시간의 차이 값을 알아내기 위함
             */

            int[] startTime = new int[2];
            int[] endTime = new int[2];

            int hourDifference = 0; // 시간 차이
            int minuteDifference = 0; // 분 차이

            int totalXValue = 0; // x value

            // 시간 비교 LocalTime 변수
            LocalTime targetStartTime = LocalTime.of(intTargetStartHour, intTargetStartMinute);
            LocalTime targetEndTime = LocalTime.of(intTargetEndHour, intTargetEndMinute);

            LocalTime twoDaysStartTime = LocalTime.of(intTwoDaysStartHour, intTwoDaysStartMinute);
            LocalTime twoDaysEndTime = LocalTime.of(intTwoDaysEndHour, intTwoDaysEndMinute);

            LocalTime threeDaysStartTime = LocalTime.of(intThreeDaysStartHour, intThreeDaysStartMinute);
            LocalTime threeDaysEndTime = LocalTime.of(intThreeDaysEndHour, intThreeDaysEndMinute);

            // 시작 시간 비교
            if (targetStartTime.isBefore(twoDaysStartTime)){
                // targetStartTime가 더 빠른 경우
                startTime[0] = intTargetStartHour;
                startTime[1] = intTargetStartMinute;
            }else {
                // twoDaysStartTime 더 빠른 경우
                startTime[0] = intTwoDaysStartHour;
                startTime[1] = intTwoDaysStartMinute;
            }

            LocalTime checkTime = LocalTime.of(startTime[0], startTime[1]);

            if (threeDaysStartTime.isBefore(checkTime)){
                // threeDaysStartTime 더 빠른 경우
                startTime[0] = intThreeDaysStartHour;
                startTime[1] = intThreeDaysStartMinute;
            }


            // 종료 시간 비교
            if (targetEndTime.isAfter(twoDaysEndTime)){
                // targetEndTime 가 더 느린 경우
                endTime[0] = intTargetEndHour;
                endTime[1] = intTargetEndMinute;
            }else {
                // twoDaysEndTime 가 더 느린 경우
                endTime[0] = intTwoDaysEndHour;
                endTime[1] = intTwoDaysEndMinute;
            }

            checkTime = LocalTime.of(endTime[0], endTime[1]);

            if (threeDaysEndTime.isAfter(checkTime)){
                // threeDaysEndTime 가 더 느린 경우
                endTime[0] = intThreeDaysEndHour;
                endTime[1] = intThreeDaysEndMinute;
            }

            // 시간 차이 계산
            LocalTime localStartTime = LocalTime.of(startTime[0], startTime[1]);
            LocalTime localEndTime = LocalTime.of(endTime[0], endTime[1]);

            Duration duration = Duration.between(localStartTime, localEndTime);

            long totalDiffInMinutes = duration.toMinutes();
            minuteDifference = (int) (totalDiffInMinutes % 60); // 분 차이
            hourDifference = (int) duration.toHours();  // 시간 차이

            // x축 개수
            totalXValue = (hourDifference * 360) + (minuteDifference * 6);
            // 시간값이 들어가는 테이블
            ArrayList<String> timeTable = new ArrayList<>();

            // 시간 시간(정수)
            int intStartHour = startTime[0];
            int intStartMinute = startTime[1];
            int secondCnt = 0;

            // 시간 시간(문자열)
            String StringStartHour = String.valueOf(startTime[0]);
            String StringStartMinute = String.valueOf(startTime[1]);

            // x 시간 축
            for(int i = 0; totalXValue > i ; i++) {
                String timeHour = String.valueOf(intStartHour);
                String timeMinute = String.valueOf(intStartMinute);
                String time;

                // hour, minute가 1의 자리 숫자인 경우 문자열 비교를 위해 앞에 0 추가
                if (intStartHour < 10){
                    timeHour = "0" + intStartHour;
                }
                if (intStartMinute < 10){
                    timeMinute  = "0" + intStartMinute;
                }

                // timeTable에 time 값 추가
                time = timeHour + ":" + timeMinute + ":" + secondCnt;
                timeTable.add(time);
                secondCnt++;

                // 초 -> 분
                if ( secondCnt == 6 ){
                    if(intStartMinute < 9) {
                        StringStartMinute = "0" + (intStartMinute + 1);
                    }
                    else {
                        StringStartMinute = String.valueOf(intStartMinute + 1);
                    }
                    intStartMinute++;
                    secondCnt = 0;
                }

                // 분 -> 시
                if (StringStartMinute.equals("60")){
                    if (intStartMinute < 9) {
                        StringStartHour = "0" + (intStartHour + 1);
                    }
                    else {
                        StringStartHour = String.valueOf(intStartHour + 1);
                    }
                    StringStartMinute = "00";
                    intStartHour++;
                    intStartMinute = 0;
                }
            }

            int bpmTimeCount = 0;
            int timeTableCount = 0;

            // target(기준일) 그래프 시작 포인트
            LocalTime localTargetTime = LocalTime.of(intTargetStartHour, intTargetStartMinute);

            duration = Duration.between(localStartTime, localTargetTime);

            totalDiffInMinutes = duration.toMinutes();
            minuteDifference = (int) (totalDiffInMinutes % 60); // 분 차이
            hourDifference = (int) duration.toHours();  // 시간 차이

            timeTableCount = (hourDifference * 360) + (minuteDifference * 6);

            // Target Graph
            for( int i = 0 ; totalXValue - 1 > i ; i++) {
                String[] bpmTime = targetBpmTimeData.get(bpmTimeCount).split(":");
                String[] checkTimeTable = timeTable.get(timeTableCount).split(":");
                String bpmSecond = String.valueOf(bpmTime[2].charAt(0));

//                Log.d("bpmTime", Arrays.toString(bpmTime));
//                Log.d("bpmSecond", String.valueOf(bpmSecond));
//                Log.d("checkTimeTable", Arrays.toString(checkTimeTable));

                boolean check = false;

                // 값이 있는 경우
                if(bpmTime[0].equals(checkTimeTable[0])){ // hour
                    if(bpmTime[1].equals(checkTimeTable[1])) { // minute
                        if(bpmSecond.equals(checkTimeTable[2])) { // second

                            Entry bpmDataEntry = new Entry((float)timeTableCount, targetBpmArrayData.get(bpmTimeCount).floatValue());
                            targetEntries.add(bpmDataEntry);

//                            Log.d("check", String.valueOf(bpmDataEntry));

                            bpmTimeCount += 1; // 값이 있으니까 +1
                            check = true;
                        }
                    } else {
                        // 분 값이 없는 경우 이전 값을 씀
                        if(bpmTimeCount > 0) {
                            Entry bpmDataEntry = new Entry((float)timeTableCount, targetBpmArrayData.get(bpmTimeCount-1).floatValue());
                            targetEntries.add(bpmDataEntry);
                        }
                    }
                } else {
                    // 시간 값이 없는 경우 이전 값을 씀
                    if(bpmTimeCount > 0) {
                        Entry bpmDataEntry = new Entry((float)timeTableCount, targetBpmArrayData.get(bpmTimeCount-1).floatValue());
                        targetEntries.add(bpmDataEntry);
                    }
                }

                // 같은 초가 나오는 경우 현재 타임 테이블 값과 다음값 비교 (ex: 10 -> 19)
                if (check  && bpmTimeCount < targetBpmArrayData.size()) {
                    bpmTime = targetBpmTimeData.get(bpmTimeCount).split(":");
                    bpmSecond = String.valueOf(bpmTime[2].charAt(0));

                    while (bpmSecond.equals(checkTimeTable[2])){
                        Entry bpmDataEntry = new Entry((float)timeTableCount, targetBpmArrayData.get(bpmTimeCount).floatValue());
                        targetEntries.add(bpmDataEntry);

                        bpmTimeCount += 1;
                        // 마지막 값인지 확인
                        bpmTime = targetBpmTimeData.get(bpmTimeCount).split(":");
                        bpmSecond = String.valueOf(bpmTime[2].charAt(0));

                        // 마지막 값 확인
                        if(targetBpmTimeData.size() - 10 < bpmTimeCount){
                            break;
                        }

                    }
                }

                timeTableCount += 1;

                // 마지막 값 확인
                if(targetBpmTimeData.size() - 10 < bpmTimeCount){
                    break;
                }
            }

            bpmTimeCount = 0;
            timeTableCount = 0;

            // twoDays(이전일) 그래프 시작 포인트
            LocalTime localTwoDaysTime = LocalTime.of(intTwoDaysStartHour, intTwoDaysStartMinute);

            duration = Duration.between(localStartTime, localTwoDaysTime);

            totalDiffInMinutes = duration.toMinutes();
            minuteDifference = (int) (totalDiffInMinutes % 60); // 분 차이
            hourDifference = (int) duration.toHours();  // 시간 차이

            timeTableCount = (hourDifference * 360) + (minuteDifference * 6);

//            Log.d("minuteDifference", String.valueOf(minuteDifference));
//            Log.d("hourDifference", String.valueOf(hourDifference));
//            Log.d("timeTableCount", String.valueOf(timeTableCount));

            // twoDays Graph
            for( int i = 0 ; totalXValue - 1 > i ; i++) {
                String[] bpmTime = twoDaysBpmTimeData.get(bpmTimeCount).split(":");
                String[] checkTimeTable = timeTable.get(timeTableCount).split(":");
                String bpmSecond = String.valueOf(bpmTime[2].charAt(0));
//                Log.d("bpmTime", Arrays.toString(bpmTime));
//                Log.d("bpmSecond", String.valueOf(bpmSecond));
//                Log.d("checkTimeTable", Arrays.toString(checkTimeTable));

                boolean check = false;

                // 값이 있는 경우
                if(bpmTime[0].equals(checkTimeTable[0])){ // hour
                    if(bpmTime[1].equals(checkTimeTable[1])) { // minute
                        if(bpmSecond.equals(checkTimeTable[2])) { // second

                            Entry bpmDataEntry = new Entry((float)timeTableCount, twoDaysBpmArrayData.get(bpmTimeCount).floatValue());
                            twoDaysEntries.add(bpmDataEntry);

//                            Log.d("check", String.valueOf(bpmDataEntry));

                            bpmTimeCount += 1; // 값이 있으니까 +1
                            check = true;
                        }
                    } else {
                        // 분 값이 없는 경우 이전 값을 씀
                        if(bpmTimeCount > 0){
                            Entry bpmDataEntry = new Entry((float)timeTableCount, twoDaysBpmArrayData.get(bpmTimeCount-1).floatValue());
                            twoDaysEntries.add(bpmDataEntry);
                        }
                    }
                } else {
                    // 시간 값이 없는 경우 이전 값을 씀
                    if(bpmTimeCount > 0){
                        Entry bpmDataEntry = new Entry((float)timeTableCount, twoDaysBpmArrayData.get(bpmTimeCount-1).floatValue());
                        twoDaysEntries.add(bpmDataEntry);
                    }
                }

                // 같은 초가 나오는 경우 현재 타임 테이블 값과 다음값 비교 (ex: 10 -> 19)
                if (check  && bpmTimeCount < twoDaysBpmArrayData.size()) {
                    bpmTime = twoDaysBpmTimeData.get(bpmTimeCount).split(":");
                    bpmSecond = String.valueOf(bpmTime[2].charAt(0));

                    while (bpmSecond.equals(checkTimeTable[2])){
                        Entry bpmDataEntry = new Entry((float)timeTableCount, twoDaysBpmArrayData.get(bpmTimeCount).floatValue());
                        twoDaysEntries.add(bpmDataEntry);

                        bpmTimeCount += 1;
                        // 마지막 값인지 확인
                        bpmTime = twoDaysBpmTimeData.get(bpmTimeCount).split(":");
                        bpmSecond = String.valueOf(bpmTime[2].charAt(0));

                        // 마지막 값 확인
                        if(twoDaysBpmTimeData.size() - 10 < bpmTimeCount){
                            break;
                        }
                    }
                }

                timeTableCount += 1;

                // 마지막 값 확인
                if(twoDaysBpmTimeData.size() - 10 < bpmTimeCount){
                    break;
                }
            }

            bpmTimeCount = 0;
            timeTableCount = 0;

            // twoDays(이전일) 그래프 시작 포인트
            LocalTime localThreeDaysTime = LocalTime.of(intThreeDaysStartHour, intThreeDaysStartMinute);

            duration = Duration.between(localStartTime, localThreeDaysTime);

            totalDiffInMinutes = duration.toMinutes();
            minuteDifference = (int) (totalDiffInMinutes % 60); // 분 차이
            hourDifference = (int) duration.toHours();  // 시간 차이

            timeTableCount = (hourDifference * 360) + (minuteDifference * 6);

//            Log.d("minuteDifference", String.valueOf(minuteDifference));
//            Log.d("hourDifference", String.valueOf(hourDifference));
//            Log.d("timeTableCount", String.valueOf(timeTableCount));
//            Log.d("threeDaysBpmTimeData", String.valueOf(threeDaysBpmTimeData.size()));

            // threeDays Graph
            for( int i = 0 ; totalXValue - 1 > i ; i++) {
                String[] bpmTime = threeDaysBpmTimeData.get(bpmTimeCount).split(":");
                String[] checkTimeTable = timeTable.get(timeTableCount).split(":");
                String bpmSecond = String.valueOf(bpmTime[2].charAt(0));

//                Log.d("bpmTime", Arrays.toString(bpmTime));
//                Log.d("bpmSecond", String.valueOf(bpmSecond));
//                Log.d("checkTimeTable", Arrays.toString(checkTimeTable));

                boolean check = false;

                // 값이 있는 경우
                if(bpmTime[0].equals(checkTimeTable[0])){ // hour
                    if(bpmTime[1].equals(checkTimeTable[1])) { // minute
                        if(bpmSecond.equals(checkTimeTable[2])) { // second

                            Entry bpmDataEntry = new Entry((float)timeTableCount, threeDaysBpmArrayData.get(bpmTimeCount).floatValue());
                            threeDaysEntries.add(bpmDataEntry);

//                            Log.d("check", String.valueOf(bpmDataEntry));

                            bpmTimeCount += 1; // 값이 있으니까 +1
                            check = true;
                        }
                    } else {
                        // 분 값이 없는 경우 이전 값을 씀
                        if(bpmTimeCount > 0){
                            Entry bpmDataEntry = new Entry((float)timeTableCount, threeDaysBpmArrayData.get(bpmTimeCount-1).floatValue());
                            threeDaysEntries.add(bpmDataEntry);
                        }
                    }
                } else {
                    // 시간 값이 없는 경우 이전 값을 씀
                    if(bpmTimeCount > 0){
                        Entry bpmDataEntry = new Entry((float)timeTableCount, threeDaysBpmArrayData.get(bpmTimeCount-1).floatValue());
                        threeDaysEntries.add(bpmDataEntry);
                    }
                }

                // 같은 초가 나오는 경우 현재 타임 테이블 값과 다음값 비교 (ex: 10 -> 19)
                if (check  && bpmTimeCount < threeDaysBpmArrayData.size()) {
                    bpmTime = threeDaysBpmTimeData.get(bpmTimeCount).split(":");
                    bpmSecond = String.valueOf(bpmTime[2].charAt(0));

                    while (bpmSecond.equals(checkTimeTable[2])){
                        Entry bpmDataEntry = new Entry((float)timeTableCount, threeDaysBpmArrayData.get(bpmTimeCount).floatValue());
                        threeDaysEntries.add(bpmDataEntry);

                        bpmTimeCount += 1;
                        // 마지막 값인지 확인
                        bpmTime = threeDaysBpmTimeData.get(bpmTimeCount).split(":");
                        bpmSecond = String.valueOf(bpmTime[2].charAt(0));

                        // 마지막 값 확인
                        if(threeDaysBpmTimeData.size() - 10 < bpmTimeCount){
                            break;
                        }

                    }
                }

                timeTableCount += 1;

                // 마지막 값 확인
                if(threeDaysBpmTimeData.size() - 10 < bpmTimeCount){
                    break;
                }

//                Log.d("bpmTimeCount", String.valueOf(bpmTimeCount));
//                Log.d("threeDaysBpmTimeData", String.valueOf(threeDaysBpmTimeData.size()));
            }

            timeTable.clear();

            // 시간 시간(정수)
            intStartHour = startTime[0];
            intStartMinute = startTime[1];
            secondCnt = 0;

            // 시간 시간(문자열)
            StringStartHour = String.valueOf(startTime[0]);
            StringStartMinute = String.valueOf(startTime[1]);

            // remove second
            for(int i = 0; totalXValue > i ; i++) {
                String time = StringStartHour+ ":" + StringStartMinute;

                timeTable.add(time);
                secondCnt++;

                // 초 -> 분
                if ( secondCnt == 6 ){
                    if(intStartMinute < 9) {
                        StringStartMinute = "0" + (intStartMinute + 1);
                    }
                    else {
                        StringStartMinute = String.valueOf(intStartMinute + 1);
                    }
                    intStartMinute += 1;
                    secondCnt = 0;
                }

                // 분 -> 시
                if (StringStartMinute.equals("60")){
                    if (intStartMinute < 9) {
                        StringStartHour = "0" + (intStartHour + 1);
                    }
                    else {
                        StringStartHour = String.valueOf(intStartHour + 1);
                    }
                    StringStartMinute = "00";
                    intStartHour++;
                    intStartMinute = 0;
                }
            }

            // 그래프 Set
            LineDataSet targetDataSet = new LineDataSet(targetEntries, targetMonth+"-"+targetDay);
            targetDataSet.setDrawCircles(false);
            targetDataSet.setColor(Color.RED);
            targetDataSet.setLineWidth(0.5f);
            targetDataSet.setDrawValues(true);


            // 그래프 Set
            LineDataSet twoDaysDataSet = new LineDataSet(twoDaysEntries, twoDaysBpmMonth+"-"+twoDaysBpmDay);
            twoDaysDataSet.setDrawCircles(false);
            twoDaysDataSet.setColor(Color.BLUE);
            twoDaysDataSet.setLineWidth(0.5f);
            twoDaysDataSet.setDrawValues(true);

            // 그래프 Set
            LineDataSet threeDaysDataSet = new LineDataSet(threeDaysEntries, threeDaysBpmMonth+"-"+threeDaysBpmDay);
            threeDaysDataSet.setDrawCircles(false);
            threeDaysDataSet.setColor(Color.parseColor("#138A1E"));
            threeDaysDataSet.setLineWidth(0.5f);
            threeDaysDataSet.setDrawValues(true);

            ArrayList<ILineDataSet> threeDaysBpmChartdataSets = new ArrayList<>();
            threeDaysBpmChartdataSets.add(threeDaysDataSet);
            threeDaysBpmChartdataSets.add(twoDaysDataSet);
            threeDaysBpmChartdataSets.add(targetDataSet);

            LineData twoDaysBpmChartData = new LineData(threeDaysBpmChartdataSets);

            bpmChart.setData(twoDaysBpmChartData);
            bpmChart.setNoDataText("");// 데이터가 없는 경우 차트에 표시되는 텍스트 설정
            bpmChart.getXAxis().setEnabled(true);   // x축 활성화(true)
            bpmChart.getLegend().setTextSize(15f);  // 범례 텍스트 크기 설정("BPM" size)
            bpmChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(timeTable));    // x축의 값 설정
            bpmChart.setVisibleXRangeMaximum(500);  // 한 번에 보여지는 x축 최대 값
            bpmChart.getXAxis().setGranularity(1f); // 축의 최소 간격
            bpmChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // x축 위치
            bpmChart.getXAxis().setDrawGridLines(false);    // 축의 그리드 선
            bpmChart.getDescription().setEnabled(false);    // 차트 설명

            bpmChart.getAxisLeft().setAxisMaximum(200f); // y 축 최대값
            bpmChart.getAxisLeft().setAxisMinimum(40f); // y 축 최소값
            bpmChart.getAxisRight().setEnabled(false);  // 참조 반환
            bpmChart.setDrawMarkers(false); // 값 마커
            bpmChart.setDragEnabled(true);  // 드래그 기능
            bpmChart.setPinchZoom(false);   // 줌 기능
            bpmChart.setDoubleTapToZoomEnabled(false);  // 더블 탭 줌 기능
            bpmChart.setHighlightPerDragEnabled(false); // 드래그 시 하이라이트

            bpmChart.getData().notifyDataChanged(); // 차트에게 데이터가 변경되었음을 알림
            bpmChart.notifyDataSetChanged();    // 차트에게 데이터가 변경되었음을 알림
            bpmChart.moveViewToX(0);    // 주어진 x값의 위치로 뷰 이동

            bpmChart.invalidate(); // 차트 다시 그림

            // 줌 인 상태에서 다른 그래프 봤을 경우 대비 줌 아웃
            for(int i = 0 ; 20 > i ; i++) {
                bpmChart.zoomOut();
            }

            maxBpm.setText(""+max);
            minBpm.setText(""+min);
            avgBpm.setText(""+avg);
            diffMinBpm.setText("-"+(avg-min));
            diffMaxBpm.setText("+"+(max-avg));

        }
        else {
            // 파일이 없는 경우
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

        calcDate();
    }

    public void calcDate() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date;

        // target을 기준으로 -1
        date = LocalDate.parse(targetDate, formatter);
        date = date.minusDays(1);

        twoDaysBpmDate = date.format(formatter);
        twoDaysBpmYear = date.format(yearFormat);
        twoDaysBpmMonth = date.format(monthFormat);
        twoDaysBpmDay = date.format(dayFormat);

        date = LocalDate.parse(targetDate, formatter);
        date = date.minusDays(2);

        // target을 기준으로 -2
        threeDaysBpmDate = date.format(formatter);
        threeDaysBpmYear = date.format(yearFormat);
        threeDaysBpmMonth = date.format(monthFormat);
        threeDaysBpmDay = date.format(dayFormat);

//        Log.d("twoDaysBpmDate", twoDaysBpmDate);
//        Log.d("twoDaysBpmYear", twoDaysBpmYear);
//        Log.d("twoDaysBpmMonth", twoDaysBpmMonth);
//        Log.d("twoDaysBpmDay", twoDaysBpmDay);
//
//        Log.d("threeDaysBpmDate", threeDaysBpmDate);
//        Log.d("threeDaysBpmYear", threeDaysBpmYear);
//        Log.d("threeDaysBpmMonth", threeDaysBpmMonth);
//        Log.d("threeDaysBpmDay", threeDaysBpmDay);
    }
}
package com.mcuhq.simplebluetooth.summary;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.mcuhq.simplebluetooth.controller.LineChartController;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class SummaryBpm extends Fragment {

    /*button*/
    //region
    Button[] buttons;
    Button todayButton;
    Button twoDaysButton;
    Button threeDaysButton;
    //endregion

    /*TextView*/
    //region
    TextView dateDisplay;
    TextView minBpm;
    TextView maxBpm;
    TextView avgBpm;
    TextView diffMinBpm;
    TextView diffMaxBpm;
    //endregion

    /*imagebutton*/
    //region
    ImageButton yesterdayButton;
    ImageButton tomorrowButton;
    //endregion

    /*currentTime*/
    //region
    String currentYear;
    String currentMonth;
    String currentDay;
    String currentDate;
    String currentTime;
    //endregion

    /*targetTime*/
    //region
    String targetYear;
    String targetMonth;
    String targetDay;
    String targetDate;
    //endregion

    /*tartget date를 기준으로 -1*/
    //region
    String twoDaysBpmYear;
    String twoDaysBpmMonth;
    String twoDaysBpmDay;
    String twoDaysBpmDate;
    //endregion

    /*tartget date를 기준으로 -2*/
    //region
    String threeDaysBpmYear;
    String threeDaysBpmMonth;
    String threeDaysBpmDay;
    String threeDaysBpmDate;
    //endregion

    /*dayboolean*/
    //region
    Boolean today;
    Boolean twoDays;
    Boolean threeDays;
    //endregion

    /*data-max_avg_min_cnt*/
    //region
    int avg = 0;
    int avgSum = 0;
    int avgCnt = 0;
    int max = 0;
    int min = 70;
    //endregion

    /*SimpleDateFormat*/
    //region
    SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat year = new SimpleDateFormat("yyyy");
    SimpleDateFormat month = new SimpleDateFormat("MM");
    SimpleDateFormat day = new SimpleDateFormat("dd");
    //endregion

    /*DateTimeFormatter*/
    //region
    DateTimeFormatter yearFormat = DateTimeFormatter.ofPattern("yyyy");
    DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MM");
    DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("dd");
    //endregion

    private LineChart bpmChart;

    View view;

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
        LocalDate date = LocalDate.parse(targetDate, formatter);

        if(check){
            // tomorrow
            date = date.plusDays(myDay);
        } else{
            // yesterday
            date = date.minusDays(myDay);
        }
        targetDate = date.format(formatter);

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
        Clear(targetDate,true);

        // 경로
        File directory = getFileDirectory("LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth + "/" + targetDay);

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
                setBpmLoop(file,bpmTimeData,bpmArrayData);

                // 그래프에 들어갈 데이터 저장
                LineChartController.setChartData(entries,bpmArrayData);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 그래프 Set
            LineDataSet dataSet = LineChartController.getLineData(entries,"BPM",Color.BLUE);
            LineData bpmChartData = new LineData(dataSet);
            LineChartController.setChartOption(bpmChart,bpmChartData,bpmTimeData);
            bpmChart.getLegend().setTypeface(Typeface.DEFAULT_BOLD);
        }
        else {
            // 파일이 없는 경우
        }

        // 줌 인 상태에서 다른 그래프 봤을 경우 대비 줌 아웃
        LineChartController.setZoom(bpmChart);

        setBpmText();
    }

    public void twoDaysBpmChartGraph() {
        Clear(twoDaysBpmMonth + "-" + twoDaysBpmDay + " ~ " + targetMonth + "-" + targetDay,true);

        // 경로
        File directory = getFileDirectory("LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth + "/" + targetDay);

        // 파일 경로와 이름
        File targetFile = new File(directory, "BpmData.csv");

        // 경로
        directory = getFileDirectory("LOOKHEART/" + email + "/" + twoDaysBpmYear + "/" + twoDaysBpmMonth + "/" + twoDaysBpmDay);

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
                setBpmLoop(targetFile,targetBpmTimeData,targetBpmArrayData);
                setBpmLoop(twoDaysBpmFile,twoDaysBpmTimeData,twoDaysBpmArrayData);
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*
            X 축 타임 테이블을 위해 시작 시간과 종료 시간을 구함
            */
            int firstIndex = targetBpmTimeData.size()-1 ;
            int secondIndex = twoDaysBpmTimeData.size()-1;
            int totalIndex = firstIndex + secondIndex;

            ArrayList<String> totalAXis = new ArrayList<>();
            int j = 0;
            for(int i = 0; i <= totalIndex; i++){
                String Xlabel;
                if(i <= firstIndex){
                    Xlabel =  targetBpmTimeData.get(i);
                }else{
                    Xlabel =  twoDaysBpmTimeData.get(j);
                    j++;
                }
                totalAXis.add(Xlabel);
            }

            //totalAXis.stream().map(d-> d.split(" ")[1]); //날짜 년월일 제외

            Collections.sort(totalAXis); //시간 정렬

            int k = 0;
            int z = 0;
            try {
                for(int i = 0; i <= totalIndex; i++){
                    String time = totalAXis.get(i);
                    if(k <= firstIndex){
                        if(time == targetBpmTimeData.get(k)){
                            Entry bpmDataEntry = new Entry((float)i, targetBpmArrayData.get(k).floatValue());
                            targetEntries.add(bpmDataEntry);
                            k++;
                        }
                    }
                    if(z <= secondIndex){
                        if(time == twoDaysBpmTimeData.get(z)){
                            Entry bpmDataEntry = new Entry((float)i, twoDaysBpmArrayData.get(z).floatValue());
                            twoDaysEntries.add(bpmDataEntry);
                            z++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Collections.sort(totalAXis); //시간 정렬

            // totalAXis.stream().map(d-> d.split(" ")[1]); //날짜 년월일 제외

            LineDataSet targetDataSet = LineChartController.getLineData(targetEntries,targetMonth+"-"+targetDay,Color.RED);
            LineDataSet twoDaysDataSet = LineChartController.getLineData(twoDaysEntries,twoDaysBpmMonth+"-"+twoDaysBpmDay,Color.BLUE);

            // 그래프 Set

            ArrayList<ILineDataSet> twoDaysBpmChartdataSets = new ArrayList<>();
            twoDaysBpmChartdataSets.add(twoDaysDataSet);
            twoDaysBpmChartdataSets.add(targetDataSet);

            LineData twoDaysBpmChartData = new LineData(twoDaysBpmChartdataSets);

            LineChartController.setChartOption(bpmChart,twoDaysBpmChartData,totalAXis);

            // 줌 인 상태에서 다른 그래프 봤을 경우 대비 줌 아웃
            LineChartController.setZoom(bpmChart);

            setBpmText();
        }
        else {
            // 파일이 없는 경우
        }
    }

    public void threeDaysBpmChartGraph() {

        bpmChart.clear();
        dateDisplay.setText(threeDaysBpmMonth + "-" + threeDaysBpmDay + " ~ " + targetMonth + "-" + targetDay);

        // 경로
        File directory = getFileDirectory("LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth + "/" + targetDay);

        // 파일 경로와 이름
        File targetFile = new File(directory, "BpmData.csv");

        // 경로
        directory = getFileDirectory("LOOKHEART/" + email + "/" + twoDaysBpmYear + "/" + twoDaysBpmMonth + "/" + twoDaysBpmDay);

        // 파일 경로와 이름
        File twoDaysBpmFile = new File(directory, "BpmData.csv");

        // 경로
        directory = getFileDirectory("LOOKHEART/" + email + "/" + threeDaysBpmYear + "/" + threeDaysBpmMonth + "/" + threeDaysBpmDay);

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
                setBpmLoop(targetFile,targetBpmTimeData,targetBpmArrayData);
                setBpmLoop(twoDaysBpmFile,twoDaysBpmTimeData,twoDaysBpmArrayData);
                setBpmLoop(threeDaysBpmFile,threeDaysBpmTimeData,threeDaysBpmArrayData);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int firstIndex = targetBpmTimeData.size();
            int secondIndex = twoDaysBpmTimeData.size();
            int thirdIndex = threeDaysBpmTimeData.size() ;
            int totalIndex = firstIndex + secondIndex + thirdIndex;

            ArrayList<String> totalAXis = new ArrayList<>();

            int j = 0;
            int k = 0;
            for(int i = 0; i < totalIndex; i++){
                String Xlabel;
                if(i < firstIndex){
                    Xlabel =  targetBpmTimeData.get(i);
                    totalAXis.add(Xlabel);
                }else if(j < secondIndex){
                    Xlabel =  twoDaysBpmTimeData.get(j);
                    totalAXis.add(Xlabel);
                    j++;
                }else if(k < thirdIndex){
                    Xlabel =  threeDaysBpmTimeData.get(k);
                    totalAXis.add(Xlabel);
                    k++;
                }
            }

            Collections.sort(totalAXis, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            }); //시간 정렬

            int a = 0;
            int b = 0;
            int z = 0;

            try {
                // file read
                for(int i = 0; i < totalIndex; i++){
                    Entry bpmDataEntry;
                    String time = totalAXis.get(i);

                    if(b < secondIndex){
                        if(twoDaysBpmTimeData.contains(time)){
                            bpmDataEntry = new Entry((float)i, twoDaysBpmArrayData.get(b).floatValue());
                            twoDaysEntries.add(bpmDataEntry);
                            b++;
                        }
                    }

                    if(a < firstIndex){
                        if(targetBpmTimeData.contains(time)){
                            bpmDataEntry = new Entry((float)i, targetBpmArrayData.get(a).floatValue());
                            targetEntries.add(bpmDataEntry);
                            a++;
                        }
                    }

                    if(z < thirdIndex){
                        if(threeDaysBpmTimeData.contains(time)){
                            bpmDataEntry = new Entry((float)i, threeDaysBpmArrayData.get(z).floatValue());
                            threeDaysEntries.add(bpmDataEntry);
                            z++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 그래프 Set
            LineDataSet targetDataSet = LineChartController.getLineData(targetEntries,targetMonth+"-"+targetDay,Color.RED);

            // 그래프 Set
            LineDataSet twoDaysDataSet = LineChartController.getLineData(twoDaysEntries,twoDaysBpmMonth+"-"+twoDaysBpmDay,Color.BLUE);

            // 그래프 Set
            LineDataSet threeDaysDataSet = LineChartController.getLineData(threeDaysEntries,threeDaysBpmMonth+"-"+threeDaysBpmDay,Color.parseColor("#138A1E"));

            ArrayList<ILineDataSet> threeDaysBpmChartdataSets = new ArrayList<>();
            threeDaysBpmChartdataSets.add(threeDaysDataSet);
            threeDaysBpmChartdataSets.add(twoDaysDataSet);
            threeDaysBpmChartdataSets.add(targetDataSet);

            LineData BpmChartData = new LineData(threeDaysBpmChartdataSets);

            LineChartController.setChartOption(bpmChart,BpmChartData,totalAXis);

            // 줌 인 상태에서 다른 그래프 봤을 경우 대비 줌 아웃
            LineChartController.setZoom(bpmChart);

            setBpmText();
        }
        else {
            // 파일이 없는 경우
        }
    }

    void setBpmLoop(File file,ArrayList<String> bpmTimeData,ArrayList<Double> bpmArrayData) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        while ((line = br.readLine()) != null) {
            String[] columns = line.split(","); // 데이터 구분
            Double bpmDataRow = Double.parseDouble(columns[2]); // bpm data
            int bpm = Integer.parseInt(columns[2]); // minMaxAvg 찾는 변수

            String[] bpmTimeCheck = columns[0].split(":"); // 시간 구분
            String myBpmTimeRow = bpmTimeCheck[0] + ":" + bpmTimeCheck[1] + ":" + bpmTimeCheck[2];

            calcMinMax(bpm);
            // 데이터 저장
            bpmTimeData.add(myBpmTimeRow);
            bpmArrayData.add(bpmDataRow);
        }
        br.close();
    }

    void Clear(String displayText,boolean clearInt){
        dateDisplay.setText(displayText);
        bpmChart.clear();
        if(clearInt){
            avg = 0;
            avgSum = 0;
            avgCnt = 0;
            max = 0;
            min = 70;
        }
    }

    void setBpmText(){
        maxBpm.setText(""+max);
        minBpm.setText(""+min);
        avgBpm.setText(""+avg);
        diffMinBpm.setText("-"+(avg-min));
        diffMaxBpm.setText("+"+(max-avg));
    }

    File getFileDirectory(String Name){
        String directoryName = Name;
        return new File(getActivity().getFilesDir(), directoryName);
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


    }
}
package com.mcuhq.simplebluetooth.summary;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

@RequiresApi(api = Build.VERSION_CODES.O)
public class SummaryHRV extends Fragment {

    /*imagebutton*/
    //region
    ImageButton yesterdayButton;
    ImageButton tomorrowButton;
    //endregion

    /*Button*/
    //region
    Button[] buttons;
    Button todayButton;
    Button twoDaysButton;
    Button threeDaysButton;
    //endregion

    /*TextView*/
    //region
    TextView dateDisplay;
    TextView minHrv;
    TextView maxHrv;
    TextView avgHrv;

    TextView diffMinHrv;
    TextView diffMaxHrv;
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
    String twoDaysHrvYear;
    String twoDaysHrvMonth;
    String twoDaysHrvDay;
    String twoDaysHrvDate;
    //endregion

    /*tartget date를 기준으로 -2*/
    //region
    String threeDaysHrvYear;
    String threeDaysHrvMonth;
    String threeDaysHrvDay;
    String threeDaysHrvDate;
    //endregion

    /*booleanDays*/
    //region
    Boolean today = true;
    Boolean twoDays;
    Boolean threeDays;
    //endregion

    /*max_min_avg_cnt*/
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

    View view;

    private String email;

    private LineChart hrvChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_summary_hrv, container, false);

        SharedPreferences emailSharedPreferences = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        email = emailSharedPreferences.getString("email", "null");

        avgHrv = view.findViewById(R.id.summaryHrvAvg);
        maxHrv = view.findViewById(R.id.summaryHrvMax);
        minHrv = view.findViewById(R.id.summaryHrvMin);

        diffMaxHrv = view.findViewById(R.id.summaryHrvDiffMax);
        diffMinHrv = view.findViewById(R.id.summaryHrvDiffMin);

        hrvChart = view.findViewById(R.id.HrvChart);
        dateDisplay = view.findViewById(R.id.dateDisplay);

        yesterdayButton = view.findViewById(R.id.yesterdayButton);
        tomorrowButton = view.findViewById(R.id.tomorrowButton);

        todayButton = view.findViewById(R.id.summaryHrvTodayButton);
        twoDaysButton = view.findViewById(R.id.summaryHrvTwoDaysButton);
        threeDaysButton = view.findViewById(R.id.summaryHrvThreeDaysButton);

        buttons = new Button[] {todayButton, twoDaysButton, threeDaysButton};

        currentTimeCheck();

        todayHrvChartGraph();

        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setColor(todayButton);
                todayHrvChartGraph();

                today = true;
                twoDays = false;
                threeDays = false;
            }
        });

        twoDaysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setColor(twoDaysButton);
                twoDaysHrvChartGraph();

                today = false;
                twoDays = true;
                threeDays = false;
            }
        });

        threeDaysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setColor(threeDaysButton);
                threeDaysHrvChartGraph();

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
        setButtonEvent(true);
    }

    public void yesterdayButtonEvent() {
        setButtonEvent(false);
    }

    void setButtonEvent(boolean check){
        dateCalculate(1, check);

        if(today) {
            todayHrvChartGraph();
        }
        else if(twoDays) {
            twoDaysHrvChartGraph();
        }
        else {
            threeDaysHrvChartGraph();
        }
    }

    public void calcMinMax(int hrv) {

        if (hrv != 0){
            if (min > hrv){
                min = hrv;
            }
            if (max < hrv){
                max = hrv;
            }

            avgSum += hrv;
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

    public void todayHrvChartGraph() {

        dateDisplay.setText(targetDate);
        hrvChart.clear();
        Clear();

        // 경로
        File directory = getHrvDirectory("LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth + "/" + targetDay);

        // 파일 경로와 이름
        File file = new File(directory, "BpmData.csv");

        if (file.exists()) {
            // 파일이 있는 경우

            // hrv data가 저장되는 배열 리스트
            ArrayList<Double> hrvArrayData = new ArrayList<>();
            // hrv time data가 저장되는 배열 리스트
            ArrayList<String> hrvTimeData = new ArrayList<>();
            // 그래프의 x축(시간) y축(데이터)이 저장되는 Entry 리스트
            List<Entry> entries = new ArrayList<>();

            try {
                // file read
                setHrvLoop(file,hrvTimeData,hrvArrayData);



                // 그래프에 들어갈 데이터 저장
                LineChartController.setChartData(entries,hrvArrayData);

            } catch (IOException e) {
                e.printStackTrace();
            }

            // 그래프 Set
            LineDataSet dataSet = LineChartController.getLineData(entries,"HRV",Color.BLUE);

            LineData hrvChartData = new LineData(dataSet);

            LineChartController.setChartOption(hrvChart,hrvChartData,hrvTimeData);

            // 줌 인 상태에서 다른 그래프 봤을 경우 대비 줌 아웃
            LineChartController.setZoom(hrvChart);

            setHrvText();
        }
        else {
            // 파일이 없는 경우
        }
    }

    public void twoDaysHrvChartGraph() {

        hrvChart.clear();
        dateDisplay.setText(twoDaysHrvMonth + "-" + twoDaysHrvDay + " ~ " + targetMonth + "-" + targetDay);

        Clear();

        // 경로
        File directory = getHrvDirectory("LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth + "/" + targetDay);

        // 파일 경로와 이름
        File targetFile = new File(directory, "BpmData.csv");

        // 경로
        directory = getHrvDirectory("LOOKHEART/" + email + "/" + twoDaysHrvYear + "/" + twoDaysHrvMonth + "/" + twoDaysHrvDay);

        // 파일 경로와 이름
        File twoDaysHrvFile = new File(directory, "BpmData.csv");

        if (targetFile.exists() && twoDaysHrvFile.exists()) {
            // 파일이 있는 경우

            /*
            target : 기준일
            twoDays : 기준일 -1
             */

            // hrv data가 저장되는 배열 리스트
            ArrayList<Double> targetHrvArrayData = new ArrayList<>();
            ArrayList<Double> twoDaysHrvArrayData = new ArrayList<>();

            // hrv time data가 저장되는 배열 리스트
            ArrayList<String> targetHrvTimeData = new ArrayList<>();
            ArrayList<String> twoDaysHrvTimeData = new ArrayList<>();

            // 그래프의 x축(시간) y축(데이터)이 저장되는 Entry 리스트
            List<Entry> targetEntries = new ArrayList<>();
            List<Entry> twoDaysEntries = new ArrayList<>();

            // target(기준일) 데이터 저장
            try {
                // file read
                setHrvLoop(targetFile,targetHrvTimeData,targetHrvArrayData);
                setHrvLoop(twoDaysHrvFile,twoDaysHrvTimeData,twoDaysHrvArrayData);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int firstIndex = targetHrvTimeData.size()-1 ;
            int secondIndex = twoDaysHrvTimeData.size()-1;
            int totalIndex = firstIndex + secondIndex;

            ArrayList<String> totalAXis = new ArrayList<>();
            int j = 0;
            for(int i = 0; i <= totalIndex; i++){
                String Xlabel;
                if(i <= firstIndex){
                    Xlabel =  targetHrvTimeData.get(i);
                }else{
                    Xlabel =  twoDaysHrvTimeData.get(j);
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
                        if(time == targetHrvTimeData.get(k)){
                            Entry HrvDataEntry = new Entry((float)i, targetHrvArrayData.get(k).floatValue());
                            targetEntries.add(HrvDataEntry);
                            k++;
                        }
                    }
                    if(z <= secondIndex){
                        if(time == twoDaysHrvTimeData.get(z)){
                            Entry HrvDataEntry = new Entry((float)i, twoDaysHrvArrayData.get(z).floatValue());
                            twoDaysEntries.add(HrvDataEntry);
                            z++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Collections.sort(totalAXis); //시간 정렬

            // totalAXis.stream().map(d-> d.split(" ")[1]); //날짜 년월일 제외

            // 그래프 Set
            LineDataSet targetDataSet = LineChartController.getLineData(targetEntries,targetMonth+"-"+targetDay,Color.RED);

            // 그래프 Set
            LineDataSet twoDaysDataSet = LineChartController.getLineData(twoDaysEntries,twoDaysHrvMonth+"-"+twoDaysHrvDay,Color.BLUE);

            ArrayList<ILineDataSet> twoDaysHrvChartdataSets = new ArrayList<>();
            twoDaysHrvChartdataSets.add(twoDaysDataSet);
            twoDaysHrvChartdataSets.add(targetDataSet);

            LineData twoDaysHrvChartData = new LineData(twoDaysHrvChartdataSets);

            LineChartController.setChartOption(hrvChart,twoDaysHrvChartData,totalAXis);

            // 줌 인 상태에서 다른 그래프 봤을 경우 대비 줌 아웃
            LineChartController.setZoom(hrvChart);

            setHrvText();

        }
        else {
            // 파일이 없는 경우
        }
    }

    public void threeDaysHrvChartGraph() {

        hrvChart.clear();
        dateDisplay.setText(threeDaysHrvMonth + "-" + threeDaysHrvDay + " ~ " + targetMonth + "-" + targetDay);

        // 경로
        File directory = getHrvDirectory("LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth + "/" + targetDay);

        // 파일 경로와 이름
        File targetFile = new File(directory, "BpmData.csv");

        // 경로
        directory = getHrvDirectory("LOOKHEART/" + email + "/" + twoDaysHrvYear + "/" + twoDaysHrvMonth + "/" + twoDaysHrvDay);

        // 파일 경로와 이름
        File twoDaysHrvFile = new File(directory, "BpmData.csv");

        // 경로
        directory = getHrvDirectory("LOOKHEART/" + email + "/" + threeDaysHrvYear + "/" + threeDaysHrvMonth + "/" + threeDaysHrvDay);

        // 파일 경로와 이름
        File threeDaysHrvFile = new File(directory, "BpmData.csv");

        if (targetFile.exists() && twoDaysHrvFile.exists() && threeDaysHrvFile.exists()) {
            // 파일이 있는 경우

            /*
            target : 기준일
            twoDays : 기준일 -2
             */

            // Hrv data가 저장되는 배열 리스트
            ArrayList<Double> targetHrvArrayData = new ArrayList<>();
            ArrayList<Double> twoDaysHrvArrayData = new ArrayList<>();
            ArrayList<Double> threeDaysHrvArrayData = new ArrayList<>();

            // Hrv time data가 저장되는 배열 리스트
            ArrayList<String> targetHrvTimeData = new ArrayList<>();
            ArrayList<String> twoDaysHrvTimeData = new ArrayList<>();
            ArrayList<String> threeDaysHrvTimeData = new ArrayList<>();

            // 그래프의 x축(시간) y축(데이터)이 저장되는 Entry 리스트
            List<Entry> targetEntries = new ArrayList<>();
            List<Entry> twoDaysEntries = new ArrayList<>();
            List<Entry> threeDaysEntries = new ArrayList<>();

            // target(기준일) 데이터 저장
            try {
                // file read
                setHrvLoop(targetFile,targetHrvTimeData,targetHrvArrayData);
                setHrvLoop(twoDaysHrvFile,twoDaysHrvTimeData,twoDaysHrvArrayData);
                setHrvLoop(threeDaysHrvFile,threeDaysHrvTimeData,threeDaysHrvArrayData);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int firstIndex = targetHrvTimeData.size();
            int secondIndex = twoDaysHrvTimeData.size();
            int thirdIndex = threeDaysHrvTimeData.size() ;
            int totalIndex = firstIndex + secondIndex + thirdIndex;

            ArrayList<String> totalAXis = new ArrayList<>();

            int j = 0;
            int k = 0;
            for(int i = 0; i < totalIndex; i++){
                String Xlabel;
                if(i < firstIndex){
                    Xlabel =  targetHrvTimeData.get(i);
                    totalAXis.add(Xlabel);
                }else if(j < secondIndex){
                    Xlabel =  twoDaysHrvTimeData.get(j);
                    totalAXis.add(Xlabel);
                    j++;
                }else if(k < thirdIndex){
                    Xlabel =  threeDaysHrvTimeData.get(k);
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
                for(int i = 0; i < totalIndex; i++){
                    Entry HrvDataEntry;
                    String time = totalAXis.get(i);

                    if(b < secondIndex){
                        if(twoDaysHrvTimeData.contains(time)){
                            HrvDataEntry = new Entry((float)i, twoDaysHrvArrayData.get(b).floatValue());
                            twoDaysEntries.add(HrvDataEntry);
                            b++;
                        }
                    }

                    if(a < firstIndex){
                        if(targetHrvTimeData.contains(time)){
                            HrvDataEntry = new Entry((float)i, targetHrvArrayData.get(a).floatValue());
                            targetEntries.add(HrvDataEntry);
                            a++;
                        }
                    }

                    if(z < thirdIndex){
                        if(threeDaysHrvTimeData.contains(time)){
                            HrvDataEntry = new Entry((float)i, threeDaysHrvArrayData.get(z).floatValue());
                            threeDaysEntries.add(HrvDataEntry);
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
            LineDataSet twoDaysDataSet = LineChartController.getLineData(twoDaysEntries,twoDaysHrvMonth+"-"+twoDaysHrvDay,Color.BLUE);

            // 그래프 Set
            LineDataSet threeDaysDataSet = LineChartController.getLineData(threeDaysEntries,threeDaysHrvMonth+"-"+threeDaysHrvDay,Color.parseColor("#138A1E"));

            ArrayList<ILineDataSet> threeDaysHrvChartdataSets = new ArrayList<>();
            threeDaysHrvChartdataSets.add(threeDaysDataSet);
            threeDaysHrvChartdataSets.add(twoDaysDataSet);
            threeDaysHrvChartdataSets.add(targetDataSet);

            LineData HrvChartData = new LineData(threeDaysHrvChartdataSets);

            LineChartController.setChartOption(hrvChart,HrvChartData,totalAXis);

            // 줌 인 상태에서 다른 그래프 봤을 경우 대비 줌 아웃
            LineChartController.setZoom(hrvChart);

            setHrvText();
        }
        else {
            // 파일이 없는 경우
        }
    }

    void setHrvLoop(File file,ArrayList<String> timeData,ArrayList<Double> arrayData) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        while ((line = br.readLine()) != null) {
            String[] columns = line.split(","); // 데이터 구분
            Double hrvDataRow = Double.parseDouble(columns[4]); // hrv data
            int hrv = Integer.parseInt(columns[4]); // minMaxAvg 찾는 변수

            String[] hrvTimeCheck = columns[0].split(":"); // 시간 구분
            String myHrvTimeRow = hrvTimeCheck[0] + ":" + hrvTimeCheck[1]; // 초 단위 제거

            calcMinMax(hrv);

            // 데이터 저장
            timeData.add(myHrvTimeRow);
            arrayData.add(hrvDataRow);
        }
        br.close();
    }

    void setHrvText(){
        maxHrv.setText(""+max);
        minHrv.setText(""+min);
        avgHrv.setText(""+avg);
        diffMinHrv.setText("-"+(avg-min));
        diffMaxHrv.setText("+"+(max-avg));
    }

    File getHrvDirectory(String name){
        String directoryName = name;
        return new File(getActivity().getFilesDir(), directoryName);
    }

    void Clear(){
        avg = 0;
        avgSum = 0;
        avgCnt = 0;
        max = 0;
        min = 70;
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

        twoDaysHrvDate = date.format(formatter);
        twoDaysHrvYear = date.format(yearFormat);
        twoDaysHrvMonth = date.format(monthFormat);
        twoDaysHrvDay = date.format(dayFormat);

        date = LocalDate.parse(targetDate, formatter);
        date = date.minusDays(2);

        // target을 기준으로 -2
        threeDaysHrvDate = date.format(formatter);
        threeDaysHrvYear = date.format(yearFormat);
        threeDaysHrvMonth = date.format(monthFormat);
        threeDaysHrvDay = date.format(dayFormat);
    }
}
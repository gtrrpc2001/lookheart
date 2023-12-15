package com.mcuhq.simplebluetooth.summary;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.mcuhq.simplebluetooth.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SummaryArr extends Fragment {

    private BarChart arrChart;
    private String email;

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

    /*preWeekTargetYear*/
    //region
    String preWeekTargetYear;
    String preWeekTargetMonth;
    String preWeekTargetDay;
    String preWeekTargetDate;
    //endregion

    /*Week*/
    //region
    ArrayList<Double> weekArrArrayData = new ArrayList<>();
    ArrayList<String> weekArrTimeData = new ArrayList<>();
    List<BarEntry> weekEntries = new ArrayList<>();
    //endregion

    /*month*/
    //region
    ArrayList<Double> monthArrData = new ArrayList<>();
    ArrayList<String> monthArrTimeData = new ArrayList<>();
    List<BarEntry> monthEntries = new ArrayList<>();
    //endregion

    /*year*/
    //region
    ArrayList<Double> yearArrData = new ArrayList<>();
    ArrayList<String> yearArrTimeData = new ArrayList<>();
    List<BarEntry> yearEntries = new ArrayList<>();
    //endregion

   /*count*/
    //region
   int dailyArrCnt;
    int weekArrCnt;
    int monthArrCnt;
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

    /*check*/
    //region
    Boolean dayCheck = true;
    Boolean weekCheck;
    Boolean monthCheck;
    Boolean yearCheck;
    //endregion

    /*ImageButton*/
    //region
    ImageButton yesterdayButton;
    ImageButton tomorrowButton;
    //endregion

    /*Button*/
    //region
    Button dayButton;
    Button weekButton;
    Button monthButton;
    Button yearButton;
    Button[] buttons;
    //endregion

    /*TextView*/
    //region
    TextView dateDisplay;
    TextView arrCnt;
    TextView arrText;
    //endregion

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_summary_arr, container, false);

        SharedPreferences emailSharedPreferences = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        email = emailSharedPreferences.getString("email", "null");

        setFindView();

        buttons = new Button[] {dayButton, weekButton, monthButton, yearButton};

        currentTimeCheck();

        todayArrChartGraph();

        dayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setColor(dayButton);
                todayArrChartGraph();

                dayCheck = true;
                weekCheck = false;
                monthCheck = false;
                yearCheck = false;
            }
        });

        weekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setColor(weekButton);
                weekArrChartGraph();

                dayCheck = false;
                weekCheck = true;
                monthCheck = false;
                yearCheck = false;
            }
        });

        monthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setColor(monthButton);
                monthArrChartGraph();

                dayCheck = false;
                weekCheck = false;
                monthCheck = true;
                yearCheck = false;
            }
        });

        yearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setColor(yearButton);
                yearArrChartGraph();

                dayCheck = false;
                weekCheck = false;
                monthCheck = false;
                yearCheck = true;
            }
        });

        tomorrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tomorrowButtonEvent();
            }
        });

        yesterdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yesterdayButtonEvent();
            }
        });
        return view;

    }

    public void tomorrowButtonEvent() {

        for( int i = 0 ; 20 > i ; i++) {
            arrChart.zoomOut();
        }

        setDayButtonEvent(true);
    }

    public void yesterdayButtonEvent() {
        setDayButtonEvent(false);
    }


    public void dateCalculate(int myDay, boolean check) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date;

        if(check){
            // tomorrow
            date = LocalDate.parse(targetDate, formatter);
            date = date.plusDays(myDay);

            targetDate = date.format(formatter);
//            Log.d("targetDate", targetDate);

        } else{
            // yesterday
            date = LocalDate.parse(targetDate, formatter);
            date = date.minusDays(myDay);

            targetDate = date.format(formatter);
//            Log.d("targetDate", targetDate);
        }

        date = LocalDate.parse(targetDate, formatter);

        targetYear = date.format(yearFormat);
        targetMonth = date.format(monthFormat);
        targetDay = date.format(dayFormat);

    }

    public void monthDateCalculate(boolean check) {
        LocalDate today = LocalDate.of(Integer.parseInt(targetYear), Integer.parseInt(targetMonth), Integer.parseInt(targetDay)); // Here you can specify the date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate resultDate;

        if (check) {
            resultDate = today.plusMonths(1);
        }
        else {
            resultDate = today.minusMonths(1);
        }

        setMonthYear(resultDate);
    }

    public void yearDateCalculate(boolean check) {
        LocalDate today = LocalDate.of(Integer.parseInt(targetYear), Integer.parseInt(targetMonth), Integer.parseInt(targetDay)); // Here you can specify the date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate resultDate;

        if (check) {
            resultDate = today.plusYears(1);
        }
        else {
            resultDate = today.minusYears(1);
        }

        setMonthYear(resultDate);

        targetDate = String.valueOf(resultDate);
        targetYear = String.valueOf(resultDate.getYear());
    }

    public void todayArrChartGraph(){

        arrChart.clear();

        dateDisplay.setText(targetDate);
        dailyArrCnt = 0;

        // 경로
        File directory = getFileDirectory("LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth + "/" + targetDay);

        // 파일 경로와 이름
        File file = new File(directory, "CalAndDistanceData.csv");

        if (file.exists()) {
            // 파일이 있는 경우

            // arr data가 저장되는 배열 리스트
            ArrayList<Double> arrArrayData = new ArrayList<>();
            // arr time data가 저장되는 배열 리스트
            ArrayList<String> arrTimeData = new ArrayList<>();
            // 그래프의 x축(시간) y축(데이터)이 저장되는 Entry 리스트
            List<BarEntry> entries = new ArrayList<>();

            try {
                // file read
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    String[] columns = line.split(","); // 데이터 구분
                    Double arrDataRow = Double.parseDouble(columns[6]); // arr data

                    String myArrTimeRow = columns[0];

                    dailyArrCnt += Integer.parseInt(columns[6]);

                    // 데이터 저장
                    arrTimeData.add(myArrTimeRow);
                    arrArrayData.add(arrDataRow);
                }

                // 그래프에 들어갈 데이터 저장
                entries = getChartData(arrArrayData);

                br.close();

            }catch (IOException e) {
                e.printStackTrace();
            }

            // 그래프 Set
            setChartOption(entries,arrTimeData,true,0,false);
        }

        else {
            // 파일이 없는 경우
        }

        arrText.setText(getResources().getString(R.string.arrTimes));
        arrCnt.setText(""+dailyArrCnt);

    }

    public void weekArrChartGraph(){

        arrChart.clear();

        weekArrTimeData.clear();
        weekArrArrayData.clear();
        weekEntries.clear();
        weekArrCnt = 0;

        calcWeek();

        // 그래프에 들어갈 데이터 저장
        weekEntries = getChartData(weekArrArrayData);

        // 그래프 Set
        setChartOption(weekEntries,weekArrTimeData,true,0,false);
    }

    public void calcWeek(){

        // 화면에 보여주는 날짜 값
        String displayMonth;
        String displayDay;

        int weekArrSum = 0;

        LocalDate specificDate = LocalDate.of(Integer.parseInt(targetYear), Integer.parseInt(targetMonth), Integer.parseInt(targetDay)); // Here you can specify the date
        DayOfWeek dayOfWeek = specificDate.getDayOfWeek();

        String[] weekDays = {
                getResources().getString(R.string.Monday),
                getResources().getString(R.string.Tuesday),
                getResources().getString(R.string.Wednesday),
                getResources().getString(R.string.Thursday),
                getResources().getString(R.string.Friday),
                getResources().getString(R.string.Saturday),
                getResources().getString(R.string.Sunday)};

//        String today = weekDays[dayOfWeek.getValue() - 1];

        int searchMonday = 0; // 월요일 찾기

        switch (dayOfWeek) {
            case MONDAY:
                searchMonday = 0;
                break;
            case TUESDAY:
                searchMonday = 1;
                break;
            case WEDNESDAY:
                searchMonday = 2;
                break;
            case THURSDAY:
                searchMonday = 3;
                break;
            case FRIDAY:
                searchMonday = 4;
                break;
            case SATURDAY:
                searchMonday = 5;
                break;
            case SUNDAY:
                searchMonday = 6;
                break;
        }

        // 기존 Date
        setPrevDate();

        dateCalculate(searchMonday, false);

        // 화면에 보여줄 Date
        displayMonth = targetMonth;
        displayDay = targetDay;

        // 월 ~ 일
        for(int i = 0; 7 > i ; i++){
            // 경로
            weekArrCnt = 0;

            File directory = getFileDirectory("LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth + "/" + targetDay);

            // 파일 경로와 이름
            File file = new File(directory, "CalAndDistanceData.csv");

            dateCalculate(1, true);
//            Log.d("file", String.valueOf(file));

            if (file.exists()){
                // 파일이 있는 경우

                try {
                    // file read
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;

                    while ((line = br.readLine()) != null) {
                        String[] columns = line.split(","); // 데이터 구분
                        Double arrDataRow = Double.parseDouble(columns[6]); // arr data

                        String myArrTimeRow = columns[0];

                        weekArrCnt += Integer.parseInt(columns[6]);
                        weekArrSum += Integer.parseInt(columns[6]);

                    }
                    // 데이터 저장
                    weekArrArrayData.add((double) weekArrCnt);
                    weekArrTimeData.add(weekDays[i]);

                    br.close();

                }catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else {
                // 파일이 없는 경우

                // 데이터 저장
                weekArrArrayData.add(0.0);
                weekArrTimeData.add(weekDays[i]);
            }
        }

        dateDisplay.setText(displayMonth+"." + displayDay + " ~ " + targetMonth + "." + targetDay);
        arrText.setText(getResources().getString(R.string.arrTimes));
        arrCnt.setText(""+weekArrSum);

        // 기존 날짜로 변경
        setOriginalTime();
    }

    public void monthArrChartGraph(){

        arrChart.clear();

        monthArrData.clear();
        monthArrTimeData.clear();
        monthEntries.clear();

        calcMonth();

        // 그래프에 들어갈 데이터 저장
        monthEntries = getChartData(monthArrData);

        setChartOption(monthEntries,monthArrTimeData,false,15,true);
    }

    public void calcMonth() {
        YearMonth yearMonth = YearMonth.of(Integer.parseInt(targetYear), Integer.parseInt(targetMonth));
        int daysInMonth = yearMonth.lengthOfMonth();

        int monthArrSum = 0;
        int timeData = 0;
        int days = lastModifiedDirectory("LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth); // 마지막으로 수정된 파일 넘버 찾기

        // 기존 Date
        setPrevDate();

        // 1일까지 날짜 이동
        dateCalculate(days - 1, false);

        for( int i = 0;  days > i ; i++ ){

            monthArrCnt = 0;

            File directory = getFileDirectory("LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth + "/" + targetDay);

            // 파일 경로와 이름
            File file = new File(directory, "CalAndDistanceData.csv");

            dateCalculate(1, true);

            timeData = i + 1;

            if(file.exists()) {
                // 파일이 있는 경우

                try {
                    // file read
                    setCalTimeLoop(file);

                    // 데이터 저장
                    monthArrData.add((double) monthArrCnt);
                    monthArrTimeData.add(String.valueOf(timeData));



                }catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                // 파일이 없는 경우
                // 데이터 저장
                monthArrData.add(0.0);
                monthArrTimeData.add(String.valueOf(timeData));
            }
        }

        dateDisplay.setText(preWeekTargetYear + "." + preWeekTargetMonth);
        arrText.setText(getResources().getString(R.string.arrTimes));
        arrCnt.setText(""+monthArrSum);

        // 기존 날짜로 변경
        setOriginalTime();
    }

    public int lastModifiedDirectory(String fileName){
        File directory = getFileDirectory(fileName);
        // 현재 디렉토리를 지정

        // 현재 디렉토리의 모든 파일과 디렉토리를 배열로 받아옴
        File[] files = directory.listFiles();

        if (files != null && files.length > 0) {
            Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

            // 디렉토리만 필터링
            for (File file : files) {
                if (file.isDirectory()) {
                    System.out.println("The last modified directory is: " + file.getName());

                    return Integer.parseInt(file.getName());
                }
            }
        } else {
            System.out.println("The directory is empty or doesn't exist.");
            return 0;
        }
        return 0;
    }

    public void yearArrChartGraph(){

        arrChart.clear();

        yearArrData.clear();
        yearArrTimeData.clear();
        yearEntries.clear();

        calcYear();

        // 그래프에 들어갈 데이터 저장
        yearEntries = getChartData(yearArrData);

        setChartOption(yearEntries,yearArrTimeData,false,15,true);
    }

    public void calcYear() {

        // 기존 Date
        setPrevDate();

        int month = lastModifiedDirectory("LOOKHEART/" + email + "/" + targetYear);
        int yearArrSum = 0;
        int timeData = 0;

        targetDate = targetYear + "-" + "01-01";
        targetMonth = "01";
        targetDay = "01";

        // 1월부터 지정 월까지 반복
        // month
        for (int i = 0; month > i ; i++) {
            YearMonth yearMonth = YearMonth.of(Integer.parseInt(targetYear), Integer.parseInt(targetMonth));
            int daysInMonth = yearMonth.lengthOfMonth();
            monthArrCnt = 0;

            // day
            for ( int j = 0 ; daysInMonth > j ; j++) {

                File directory = getFileDirectory("LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth + "/" + targetDay);

                // 파일 경로와 이름
                File file = new File(directory, "CalAndDistanceData.csv");

                dateCalculate(1, true);

                timeData = i + 1;

                if(file.exists()) {
                    // 파일이 있는 경우
                    try {
                        // file read
                        setCalTimeLoop(file);
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    // 파일이 없는 경우
                }
            }
            // 데이터 저장
            yearArrData.add((double) monthArrCnt);
            yearArrTimeData.add(String.valueOf(timeData));
        }

        dateDisplay.setText(targetYear);
        arrText.setText(getResources().getString(R.string.arrTimes));
        arrCnt.setText(""+yearArrSum);

        // 기존 날짜로 변경
        setOriginalTime();
    }

    void setCalTimeLoop(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        while ((line = br.readLine()) != null) {
            String[] columns = line.split(","); // 데이터 구분
            monthArrCnt += Integer.parseInt(columns[6]);
        }
        br.close();
    }

    void setPrevDate(){
        preWeekTargetDate = targetDate;
        preWeekTargetYear = targetYear;
        preWeekTargetMonth = targetMonth;
        preWeekTargetDay = targetDay;
    }

    List<BarEntry> getChartData(List<Double> arrArray){
        List<BarEntry> data = new ArrayList<BarEntry>();
        for (int i = 0; i < arrArray.size(); i++) {
            data.add((BarEntry) new BarEntry((float)i, arrArray.get(i).floatValue()));
        }
        return data;
    }

    void setChartOption(List<BarEntry> data,ArrayList<String> arrTimeData,boolean fit,float XRangeMax,boolean drag){
        BarDataSet dataSet = new BarDataSet(data, "I.H.R.");
        dataSet.setColor(Color.RED);
        dataSet.setDrawValues(true);
        dataSet.setValueFormatter(new CustomValueFormatter());
        BarData ArrChartData = new BarData(dataSet);
        arrChart.setData(ArrChartData);
        arrChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        arrChart.getXAxis().setDrawGridLines(false);
        arrChart.getXAxis().setGranularity(1f);
        arrChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(arrTimeData));  // hourlyArrTimeData는 String 배열로 준비해야 합니다.
        arrChart.getXAxis().setLabelCount(arrTimeData.size(), false);  // numbersOfHourlyArrData는 int형 변수여야 합니다.
        arrChart.getAxisRight().setEnabled(false);
        arrChart.setDragEnabled(drag);  // 드래그 기능
        arrChart.setPinchZoom(false);   // 줌 기능
        arrChart.setScaleEnabled(false); // 터치 비활성화
        arrChart.getAxisLeft().setGranularityEnabled(true);
        arrChart.getAxisLeft().setAxisMinimum(0);
        Legend legend = arrChart.getLegend();
        if(XRangeMax != 0) arrChart.setVisibleXRangeMaximum(XRangeMax);
        legend.setTextSize(15f);
        legend.setTypeface(Typeface.DEFAULT_BOLD);
        arrChart.getDescription().setEnabled(false);
        arrChart.setDoubleTapToZoomEnabled(false);
        arrChart.setHighlightPerTapEnabled(false);
        arrChart.moveViewToX(0);

        // 차트를 그릴 때 호출해야 합니다.
        if(fit) arrChart.fitScreen();
        arrChart.resetZoom();
        arrChart.zoomOut();
        arrChart.notifyDataSetChanged();
        arrChart.getViewPortHandler().refresh(new Matrix(), arrChart, true);
        arrChart.invalidate();
    }

    void setMonthYear(LocalDate resultDate){
        targetDate = String.valueOf(resultDate);
        targetYear = String.valueOf(resultDate.getYear());

        if (resultDate.getMonthValue() < 10) {
            targetMonth = "0" + String.valueOf(resultDate.getMonthValue());
        }
        else {
            targetMonth = String.valueOf(resultDate.getMonthValue());
        }

        if (resultDate.getDayOfMonth() < 10) {
            targetDay = "0" + String.valueOf(resultDate.getDayOfMonth());
        }
        else {
            targetDay = String.valueOf(resultDate.getDayOfMonth());
        }
    }

    void setDayButtonEvent(boolean check){
        if(dayCheck) {
            dateCalculate(1, check);
            todayArrChartGraph();
        }
        else if(weekCheck) {
            dateCalculate(7, check);
            weekArrChartGraph();;
        }
        else if(monthCheck) {
            monthDateCalculate(check);
            monthArrChartGraph();;
        }
        else {
            // year
            yearDateCalculate(check);
            yearArrChartGraph();
        }
    }

    void setOriginalTime(){
        targetYear = preWeekTargetYear;
        targetMonth = preWeekTargetMonth;
        targetDay = preWeekTargetDay;
        targetDate = preWeekTargetDate;
    }

    File getFileDirectory(String name){
        String directoryName = name;
        return new File(getActivity().getFilesDir(), directoryName);
    }

    void setFindView(){
        arrChart = view.findViewById(R.id.arrChart);
        dayButton = view.findViewById(R.id.summaryArrDayButton);
        weekButton = view.findViewById(R.id.summaryArrWeekButton);
        monthButton = view.findViewById(R.id.summaryArrMonthButton);
        yearButton = view.findViewById(R.id.summaryArrYearButton);

        yesterdayButton = view.findViewById(R.id.yesterdayButton);
        tomorrowButton = view.findViewById(R.id.tomorrowButton);

        dateDisplay = view.findViewById(R.id.dateDisplay);
        arrCnt = view.findViewById(R.id.summaryArrCnt);
        arrText = view.findViewById(R.id.myArrText);
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

    public class CustomValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            if (value == 0) {
                return ""; // 값이 0일 때 빈 문자열 반환
            } else {
                return String.valueOf(Integer.valueOf((int) value)); // 그렇지 않으면 기본 값을 반환
            }
        }
    }
}
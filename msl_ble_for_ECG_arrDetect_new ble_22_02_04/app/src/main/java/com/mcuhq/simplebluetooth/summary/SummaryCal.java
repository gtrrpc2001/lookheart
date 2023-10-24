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
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.SharedViewModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SummaryCal extends Fragment {

    private SharedViewModel viewModel;

    private BarChart calChart;

    String currentYear;
    String currentMonth;
    String currentDay;

    String currentDate;
    String currentTime;

    String targetYear;
    String targetMonth;
    String targetDay;
    String targetDate;

    String preWeekTargetYear;
    String preWeekTargetMonth;
    String preWeekTargetDay;

    String preWeekTargetDate;

    // Week
    ArrayList<Double> weekTCalArrayData = new ArrayList<>();
    ArrayList<Double> weekECalArrayData = new ArrayList<>();
    List<BarEntry> weekTCalEntries = new ArrayList<>();
    List<BarEntry> weekECalEntries = new ArrayList<>();

    ArrayList<String> weekCalTimeData = new ArrayList<>();
    Boolean weekDirCheck;

    // Month
    ArrayList<Double> monthTCalData = new ArrayList<>();
    ArrayList<Double> monthECalData = new ArrayList<>();
    List<BarEntry> monthTCalEntries = new ArrayList<>();
    List<BarEntry> monthECalEntries = new ArrayList<>();

    ArrayList<String> monthCalTimeData = new ArrayList<>();
    Boolean monthDirCheck;
    // Year
    ArrayList<Double> yearTCalData = new ArrayList<>();
    ArrayList<Double> yearECalData = new ArrayList<>();
    List<BarEntry> yearTCalEntries = new ArrayList<>();
    List<BarEntry> yearECalEntries = new ArrayList<>();

    ArrayList<String> yearCalTimeData = new ArrayList<>();
    Boolean yearDirCheck;
    int numbersOfHourlyCalorieData = 0;

    int targetTCal = 0;
    int targetECal = 0;

    int weektargetTCal = 0;
    int weektargetECal = 0;

    int monthtargetTCal = 0;
    int monthtargetECal = 0;

    int yeartargetTCal = 0;
    int yeartargetECal = 0;

    SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");

    SimpleDateFormat year = new SimpleDateFormat("yyyy");
    SimpleDateFormat month = new SimpleDateFormat("MM");
    SimpleDateFormat day = new SimpleDateFormat("dd");

    DateTimeFormatter yearFormat = DateTimeFormatter.ofPattern("yyyy");
    DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MM");
    DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("dd");

    Boolean dayCheck = true;
    Boolean weekCheck;
    Boolean monthCheck;
    Boolean yearCheck;

    View view;

    Button dayButton;
    Button weekButton;
    Button monthButton;
    Button yearButton;

    ImageButton yesterdayButton;
    ImageButton tomorrowButton;

    Button[] buttons;

    TextView dateDisplay;

    TextView calValue;
    TextView eCalValue;

    TextView tvTargetTCal;
    TextView tvTargetECal;

    ProgressBar tCalProgressBar;
    ProgressBar eCalProgressBar;

    private String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_summary_cal, container, false);

        SharedPreferences emailSharedPreferences = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        email = emailSharedPreferences.getString("email", "null");

        calChart = view.findViewById(R.id.calChart);
        dayButton = view.findViewById(R.id.summaryCalDayButton);
        weekButton = view.findViewById(R.id.summaryCalWeekButton);
        monthButton = view.findViewById(R.id.summaryCalMonthButton);
        yearButton = view.findViewById(R.id.summaryCalYearButton);

        yesterdayButton = view.findViewById(R.id.yesterdayButton);
        tomorrowButton = view.findViewById(R.id.tomorrowButton);

        dateDisplay = view.findViewById(R.id.dateDisplay);

        calValue = view.findViewById(R.id.summaryCalValue);
        eCalValue = view.findViewById(R.id.summaryECalValue);

        tvTargetTCal = view.findViewById(R.id.targetTCal);
        tvTargetECal = view.findViewById(R.id.targetECal);

        tCalProgressBar = view.findViewById(R.id.calProgress);
        eCalProgressBar = view.findViewById(R.id.eCalProgress);

        buttons = new Button[] {dayButton, weekButton, monthButton, yearButton};

        setTargetCal();

        currentTimeCheck();

        todayCalChartGraph();

        dayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setColor(dayButton);
                todayCalChartGraph();

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
                weekCalChartGraph();

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
                monthCalChartGraph();

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
                yearCalChartGraph();

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


        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getTCalText().observe(getViewLifecycleOwner(), tvTargetTCal::setText);
        viewModel.getECalText().observe(getViewLifecycleOwner(), tvTargetECal::setText);

        return view;

    }

    public void tomorrowButtonEvent() {

        for( int i = 0 ; 20 > i ; i++) {
            calChart.zoomOut();
        }

        if(dayCheck) {
            dateCalculate(1, true);
            todayCalChartGraph();
        }
        else if(weekCheck) {
            dateCalculate(7, true);
            weekCalChartGraph();;
        }
        else if(monthCheck) {
            monthDateCalculate(true);
            monthCalChartGraph();;
        }
        else {
            // year
            yearDateCalculate(true);
            yearCalChartGraph();
        }
    }

    public void yesterdayButtonEvent() {

        if(dayCheck) {
            dateCalculate(1, false);
            todayCalChartGraph();
        }
        else if(weekCheck) {
            dateCalculate(7, false);
            weekCalChartGraph();;
        }
        else if(monthCheck) {
            monthDateCalculate(false);
            monthCalChartGraph();;
        }
        else {
            // year
            yearDateCalculate(false);
            yearCalChartGraph();
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

    public void todayCalChartGraph(){

        calChart.clear();

        dateDisplay.setText(targetDate);

        int sumTCal = 0;
        int sumECal = 0;
        int resultTCal = 0;
        int resultECal = 0;
        numbersOfHourlyCalorieData = 0;

        // 경로
        String directoryName = "LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth + "/" + targetDay;
        File directory = new File(getActivity().getFilesDir(), directoryName);

        // 파일 경로와 이름
        File file = new File(directory, "CalAndDistanceData.csv");

        if (file.exists()) {
            // 파일이 있는 경우

            // arr data가 저장되는 배열 리스트
            ArrayList<Double> tCalArrayData = new ArrayList<>();
            ArrayList<Double> eCalArrayData = new ArrayList<>();
            // arr time data가 저장되는 배열 리스트
            ArrayList<String> timeData = new ArrayList<>();
            // 그래프의 x축(시간) y축(데이터)이 저장되는 Entry 리스트
            List<BarEntry> tCalEntries = new ArrayList<>();
            List<BarEntry> eCalEntries = new ArrayList<>();


            try {
                // file read
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    String[] columns = line.split(","); // 데이터 구분
                    Double tCal = Double.parseDouble(columns[4]);
                    Double eCal = Double.parseDouble(columns[5]);
                    sumTCal = 0;
                    sumECal = 0;

                    numbersOfHourlyCalorieData++;
                    String myArrTimeRow = columns[0];

                    sumTCal += tCal;
                    sumECal += eCal;
                    resultTCal += tCal;
                    resultECal += eCal;

                    // 데이터 저장
                    timeData.add(myArrTimeRow);
                    tCalArrayData.add(tCal);
                    eCalArrayData.add(eCal);
                }

                // 그래프에 들어갈 데이터 저장
                for (int i = 0; i < tCalArrayData.size(); i++) {
                    tCalEntries.add((BarEntry) new BarEntry((float)i, tCalArrayData.get(i).floatValue()));
                    eCalEntries.add((BarEntry) new BarEntry((float)i, eCalArrayData.get(i).floatValue()));
                }

                br.close();

            }catch (IOException e) {
                e.printStackTrace();
            }

            // 그래프 Set
            BarDataSet tCaldataSet = new BarDataSet(tCalEntries, getResources().getString(R.string.summaryTCal));
            tCaldataSet.setColor(Color.RED);
            tCaldataSet.setDrawValues(false);

            // 그래프 Set
            BarDataSet eCaldataSet = new BarDataSet(eCalEntries, getResources().getString(R.string.summaryECal));
            eCaldataSet.setColor(Color.BLUE);
            eCaldataSet.setDrawValues(false);

            float groupSpace = 0.3f;
            float barSpace = 0.05f;
            float barWidth = 0.3f;

            BarData todayCalChartData = new BarData(tCaldataSet, eCaldataSet);
            todayCalChartData.setBarWidth(barWidth);


            calChart.getXAxis().setAxisMinimum(0f);
            calChart.getXAxis().setAxisMaximum(0f + todayCalChartData.getGroupWidth(groupSpace, barSpace) * (numbersOfHourlyCalorieData));  // group count : 2
            todayCalChartData.groupBars(0f, groupSpace, barSpace);

            Legend legend = calChart.getLegend();
            legend.setFormSize(12f); // Font size
            legend.setTypeface(Typeface.DEFAULT_BOLD);


            calChart.setNoDataText("");
            calChart.setData(todayCalChartData);
            calChart.getXAxis().setEnabled(true);
            calChart.getXAxis().setCenterAxisLabels(true);
            calChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(timeData));
            calChart.getXAxis().setGranularity(1f);
            calChart.getXAxis().setLabelCount(timeData.size(), false);
            calChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            calChart.getXAxis().setDrawGridLines(false);
            calChart.getDescription().setEnabled(false);

            calChart.getAxisLeft().setGranularityEnabled(true);
            calChart.getAxisLeft().setGranularity(1.0f);
            calChart.getAxisLeft().setAxisMinimum(0f);
            calChart.getAxisRight().setEnabled(false);
            calChart.setDrawMarkers(false);
            calChart.setDragEnabled(true);
            calChart.setPinchZoom(false);
            calChart.setDoubleTapToZoomEnabled(false);
            calChart.setHighlightPerTapEnabled(false);
            calChart.moveViewToX(0);

            // 차트를 그릴 때 호출해야 합니다.
            calChart.fitScreen();
            calChart.resetZoom();
            calChart.zoomOut();
            calChart.notifyDataSetChanged();
            calChart.getViewPortHandler().refresh(new Matrix(), calChart, true);
            calChart.invalidate();
        }

        else {
            // 파일이 없는 경우
        }

        calValue.setText(resultTCal + " kcal");
        eCalValue.setText(resultECal + " kcal");
        tvTargetTCal.setText(targetTCal + " kcal");
        tvTargetECal.setText(targetECal + " kcal");

        int tCalProgress = (int) ((double) resultTCal / targetTCal * 100);
        int eCalProgress = (int) ((double) resultECal / targetECal * 100);

        tCalProgress = Math.min(tCalProgress, 100);
        eCalProgress = Math.min(eCalProgress, 100);

        tCalProgressBar.setProgress(tCalProgress);
        eCalProgressBar.setProgress(eCalProgress);

    }

    public void weekCalChartGraph(){

        numbersOfHourlyCalorieData = 0;

        calChart.clear();

        weekTCalArrayData.clear();
        weekECalArrayData.clear();

        weekTCalEntries.clear();
        weekECalEntries.clear();

        calcWeek();

        if (weekDirCheck) {
            // 파일 있음

            // 그래프에 들어갈 데이터 저장
            for (int i = 0; i < weekTCalArrayData.size(); i++) {
                weekTCalEntries.add((BarEntry) new BarEntry((float)i, weekTCalArrayData.get(i).floatValue()));
                weekECalEntries.add((BarEntry) new BarEntry((float)i, weekECalArrayData.get(i).floatValue()));
            }

            // 그래프 Set
            BarDataSet tCaldataSet = new BarDataSet(weekTCalEntries, getResources().getString(R.string.summaryTCal));
            tCaldataSet.setColor(Color.RED);
            tCaldataSet.setDrawValues(false);

            // 그래프 Set
            BarDataSet eCaldataSet = new BarDataSet(weekECalEntries, getResources().getString(R.string.summaryECal));
            eCaldataSet.setColor(Color.BLUE);
            eCaldataSet.setDrawValues(false);

            float groupSpace = 0.3f;
            float barSpace = 0.05f;
            float barWidth = 0.3f;

            BarData todayCalChartData = new BarData(tCaldataSet, eCaldataSet);
            todayCalChartData.setBarWidth(barWidth);


            calChart.getXAxis().setAxisMinimum(0f);
            calChart.getXAxis().setAxisMaximum(0f + todayCalChartData.getGroupWidth(groupSpace, barSpace) * (numbersOfHourlyCalorieData));  // group count : 2
            todayCalChartData.groupBars(0f, groupSpace, barSpace);

            Legend legend = calChart.getLegend();
            legend.setFormSize(12f); // Font size
            legend.setTypeface(Typeface.DEFAULT_BOLD);


            calChart.setNoDataText("");
            calChart.setData(todayCalChartData);
            calChart.getXAxis().setEnabled(true);
            calChart.getXAxis().setCenterAxisLabels(true);
            calChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(weekCalTimeData));
            calChart.getXAxis().setGranularity(1f);
            calChart.getXAxis().setLabelCount(weekCalTimeData.size(), false);
            calChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            calChart.getXAxis().setDrawGridLines(false);
            calChart.getDescription().setEnabled(false);

            calChart.getAxisLeft().setGranularityEnabled(true);
            calChart.getAxisLeft().setGranularity(1.0f);
            calChart.getAxisLeft().setAxisMinimum(0f);
            calChart.getAxisRight().setEnabled(false);
            calChart.setDrawMarkers(false);
            calChart.setDragEnabled(true);
            calChart.setPinchZoom(false);
            calChart.setDoubleTapToZoomEnabled(false);
            calChart.setHighlightPerTapEnabled(false);
            calChart.moveViewToX(0);

            // 차트를 그릴 때 호출해야 합니다.
            calChart.fitScreen();
            calChart.resetZoom();
            calChart.zoomOut();
            calChart.notifyDataSetChanged();
            calChart.getViewPortHandler().refresh(new Matrix(), calChart, true);
            calChart.invalidate();

        }
        else {
            // 파일 없음
        }
    }

    public void calcWeek(){

        // 화면에 보여주는 날짜 값
        String displayMonth;
        String displayDay;

        int sumTCal = 0;
        int sumECal = 0;
        int resultTCal = 0;
        int resultECal = 0;
        int dataCheck = 0;

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
        preWeekTargetDate = targetDate;
        preWeekTargetYear = targetYear;
        preWeekTargetMonth = targetMonth;
        preWeekTargetDay = targetDay;

        dateCalculate(searchMonday, false);

        // 화면에 보여줄 Date
        displayMonth = targetMonth;
        displayDay = targetDay;

        // 월 ~ 일
        for(int i = 0; 7 > i ; i++){
            // 경로
            numbersOfHourlyCalorieData++;

            sumTCal = 0;
            sumECal = 0;

            String directoryName = "LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth + "/" + targetDay;
            File directory = new File(getActivity().getFilesDir(), directoryName);

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
                        Double tCal = Double.parseDouble(columns[4]);
                        Double eCal = Double.parseDouble(columns[5]);

                        sumTCal += tCal;
                        sumECal += eCal;

                        resultTCal += tCal;
                        resultECal += eCal;

                    }
                    // 데이터 저장
                    weekTCalArrayData.add((double) sumTCal);
                    weekECalArrayData.add((double) sumECal);
                    weekCalTimeData.add(weekDays[i]);

                    br.close();

                }catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else {
                // 파일이 없는 경우

                // 데이터 저장
                weekTCalArrayData.add(0.0);
                weekECalArrayData.add(0.0);
                weekCalTimeData.add(weekDays[i]);
                dataCheck++;
            }
        }

        // 파일이 없음
        if ( dataCheck == 7 ) {
            weekDirCheck = false;
        }else{
            weekDirCheck = true;
        }

        dateDisplay.setText(displayMonth+"." + displayDay + " ~ " + targetMonth + "." + targetDay);
        calValue.setText(resultTCal + " kcal");
        eCalValue.setText(resultECal + " kcal");

        tvTargetTCal.setText(targetTCal + " kcal");
        tvTargetECal.setText(targetECal + " kcal");

        int tCalProgress = (int) ((double) resultTCal / weektargetTCal * 100);
        int eCalProgress = (int) ((double) resultECal / weektargetECal * 100);

        tCalProgress = Math.min(tCalProgress, 100);
        eCalProgress = Math.min(eCalProgress, 100);

        tCalProgressBar.setProgress(tCalProgress);
        eCalProgressBar.setProgress(eCalProgress);

        // 기존 날짜로 변경
        targetYear = preWeekTargetYear;
        targetMonth = preWeekTargetMonth;
        targetDay = preWeekTargetDay;

        targetDate = preWeekTargetDate;
    }

    public void monthCalChartGraph(){

        calChart.clear();

        numbersOfHourlyCalorieData = 0;

        calChart.clear();

        monthTCalData.clear();
        monthECalData.clear();

        monthTCalEntries.clear();
        monthECalEntries.clear();


        calcMonth();

        if(monthDirCheck) {
            // 디렉토리 있음
            // 그래프에 들어갈 데이터 저장
            for (int i = 0; i < monthTCalData.size(); i++) {
                monthTCalEntries.add((BarEntry) new BarEntry((float)i, monthTCalData.get(i).floatValue()));
                monthECalEntries.add((BarEntry) new BarEntry((float)i, monthECalData.get(i).floatValue()));
            }

            // 그래프 Set
            BarDataSet tCaldataSet = new BarDataSet(monthTCalEntries, getResources().getString(R.string.summaryTCal));
            tCaldataSet.setColor(Color.RED);
            tCaldataSet.setDrawValues(false);

            // 그래프 Set
            BarDataSet eCaldataSet = new BarDataSet(monthECalEntries, getResources().getString(R.string.summaryECal));
            eCaldataSet.setColor(Color.BLUE);
            eCaldataSet.setDrawValues(false);

            float groupSpace = 0.3f;
            float barSpace = 0.05f;
            float barWidth = 0.3f;

            BarData todayCalChartData = new BarData(tCaldataSet, eCaldataSet);
            todayCalChartData.setBarWidth(barWidth);


            calChart.getXAxis().setAxisMinimum(0f);
            calChart.getXAxis().setAxisMaximum(0f + todayCalChartData.getGroupWidth(groupSpace, barSpace) * (numbersOfHourlyCalorieData));  // group count : 2
            todayCalChartData.groupBars(0f, groupSpace, barSpace);

            Legend legend = calChart.getLegend();
            legend.setFormSize(12f); // Font size
            legend.setTypeface(Typeface.DEFAULT_BOLD);


            calChart.setNoDataText("");
            calChart.setData(todayCalChartData);
            calChart.getXAxis().setEnabled(true);
            calChart.getXAxis().setCenterAxisLabels(true);
            calChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(monthCalTimeData));
            calChart.getXAxis().setGranularity(1f);
            calChart.getXAxis().setLabelCount(monthCalTimeData.size(), false);
            calChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            calChart.getXAxis().setDrawGridLines(false);
            calChart.getDescription().setEnabled(false);

            calChart.getAxisLeft().setGranularityEnabled(true);
            calChart.getAxisLeft().setGranularity(1.0f);
            calChart.getAxisLeft().setAxisMinimum(0f);
            calChart.getAxisRight().setEnabled(false);
            calChart.setDrawMarkers(false);
            calChart.setDragEnabled(true);
            calChart.setPinchZoom(false);
            calChart.setDoubleTapToZoomEnabled(false);
            calChart.setHighlightPerTapEnabled(false);
            calChart.moveViewToX(0);

            // 차트를 그릴 때 호출해야 합니다.
            calChart.fitScreen();
            calChart.resetZoom();
            calChart.zoomOut();
            calChart.notifyDataSetChanged();
            calChart.getViewPortHandler().refresh(new Matrix(), calChart, true);
            calChart.invalidate();
        }else {
            // 디렉토리 없음

        }
    }

    public void calcMonth() {
        YearMonth yearMonth = YearMonth.of(Integer.parseInt(targetYear), Integer.parseInt(targetMonth));
        int daysInMonth = yearMonth.lengthOfMonth();

        int sumTCal = 0;
        int sumECal = 0;
        int resultTCal = 0;
        int resultECal = 0;

        int timeData = 0;
        int days = lastModifiedDirectory(); // 마지막으로 수정된 파일 넘버 찾기

        // 기존 Date
        preWeekTargetDate = targetDate;
        preWeekTargetYear = targetYear;
        preWeekTargetMonth = targetMonth;
        preWeekTargetDay = targetDay;

        String directoryName = "LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth;
        File directory = new File(getActivity().getFilesDir(), directoryName);

        if (directory.exists()) {
            // 디렉토리가 있는 경우
            monthDirCheck = true;
            // 1일까지 날짜 이동
            dateCalculate(days - 1, false);

            for( int i = 0;  days > i ; i++ ){

                sumTCal = 0;
                sumECal = 0;

                directoryName = "LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth + "/" + targetDay;
                directory = new File(getActivity().getFilesDir(), directoryName);

                // 파일 경로와 이름
                File file = new File(directory, "CalAndDistanceData.csv");

                dateCalculate(1, true);

                timeData = i + 1;

                numbersOfHourlyCalorieData ++;

                if(file.exists()) {
                    // 파일이 있는 경우

                    try {
                        // file read
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String line;

                        while ((line = br.readLine()) != null) {
                            String[] columns = line.split(","); // 데이터 구분
                            Double tCal = Double.parseDouble(columns[4]);
                            Double eCal = Double.parseDouble(columns[5]);

                            sumTCal += tCal;
                            sumECal += eCal;

                            resultTCal += tCal;
                            resultECal += eCal;

                        }
                        // 데이터 저장
                        monthTCalData.add((double) sumTCal);
                        monthECalData.add((double) sumECal);
                        monthCalTimeData.add(String.valueOf(timeData));

                        br.close();

                    }catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    // 파일이 없는 경우
                    // 데이터 저장
                    monthTCalData.add(0.0);
                    monthECalData.add(0.0);
                    monthCalTimeData.add(String.valueOf(timeData));
                }
            }
        }
        else {
            // 디렉토리가 없는 경우
            monthDirCheck = false;
        }

        dateDisplay.setText(preWeekTargetYear + "." + preWeekTargetMonth);
        calValue.setText(resultTCal + " kcal");
        eCalValue.setText(resultECal + " kcal");

        monthtargetTCal = targetTCal * daysInMonth;
        monthtargetECal = targetECal * daysInMonth;

        tvTargetTCal.setText(targetTCal + " kcal");
        tvTargetECal.setText(targetECal + " kcal");

        int tCalProgress = (int) ((double) resultTCal / monthtargetTCal * 100);
        int eCalProgress = (int) ((double) resultECal / monthtargetECal * 100);

        tCalProgress = Math.min(tCalProgress, 100);
        eCalProgress = Math.min(eCalProgress, 100);

        tCalProgressBar.setProgress(tCalProgress);
        eCalProgressBar.setProgress(eCalProgress);

        // 기존 날짜로 변경
        targetYear = preWeekTargetYear;
        targetMonth = preWeekTargetMonth;
        targetDay = preWeekTargetDay;

        targetDate = preWeekTargetDate;
    }

    public int lastModifiedDirectory(){
        String directoryName = "LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth;
        File directory = new File(getActivity().getFilesDir(), directoryName);
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

    public int lastModifiedYearDirectory(){
        String directoryName = "LOOKHEART/" + email + "/" + targetYear;
        File directory = new File(getActivity().getFilesDir(), directoryName);
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

    public void yearCalChartGraph(){

        calChart.clear();

        numbersOfHourlyCalorieData = 0;

        calChart.clear();

        yearTCalData.clear();
        yearECalData.clear();

        yearTCalEntries.clear();
        yearECalEntries.clear();

        calcYear();

        if(yearDirCheck) {
            // 디렉토리 있음
            // 그래프에 들어갈 데이터 저장
            for (int i = 0; i < yearTCalData.size(); i++) {
                yearTCalEntries.add((BarEntry) new BarEntry((float)i, yearTCalData.get(i).floatValue()));
                yearECalEntries.add((BarEntry) new BarEntry((float)i, yearECalData.get(i).floatValue()));
            }

            // 그래프 Set
            BarDataSet tCaldataSet = new BarDataSet(yearTCalEntries, getResources().getString(R.string.summaryTCal));
            tCaldataSet.setColor(Color.RED);
            tCaldataSet.setDrawValues(false);

            // 그래프 Set
            BarDataSet eCaldataSet = new BarDataSet(yearECalEntries, getResources().getString(R.string.summaryECal));
            eCaldataSet.setColor(Color.BLUE);
            eCaldataSet.setDrawValues(false);

            float groupSpace = 0.3f;
            float barSpace = 0.05f;
            float barWidth = 0.3f;

            BarData todayCalChartData = new BarData(tCaldataSet, eCaldataSet);
            todayCalChartData.setBarWidth(barWidth);


            calChart.getXAxis().setAxisMinimum(0f);
            calChart.getXAxis().setAxisMaximum(0f + todayCalChartData.getGroupWidth(groupSpace, barSpace) * (numbersOfHourlyCalorieData));  // group count : 2
            todayCalChartData.groupBars(0f, groupSpace, barSpace);

            Legend legend = calChart.getLegend();
            legend.setFormSize(12f); // Font size
            legend.setTypeface(Typeface.DEFAULT_BOLD);


            calChart.setNoDataText("");
            calChart.setData(todayCalChartData);
            calChart.getXAxis().setEnabled(true);
            calChart.getXAxis().setCenterAxisLabels(true);
            calChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(yearCalTimeData));
            calChart.getXAxis().setGranularity(1f);
            calChart.getXAxis().setLabelCount(yearCalTimeData.size(), false);
            calChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            calChart.getXAxis().setDrawGridLines(false);
            calChart.getDescription().setEnabled(false);

            calChart.getAxisLeft().setGranularityEnabled(true);
            calChart.getAxisLeft().setGranularity(1.0f);
            calChart.getAxisLeft().setAxisMinimum(0f);
            calChart.getAxisRight().setEnabled(false);
            calChart.setDrawMarkers(false);
            calChart.setDragEnabled(true);
            calChart.setPinchZoom(false);
            calChart.setDoubleTapToZoomEnabled(false);
            calChart.setHighlightPerTapEnabled(false);
            calChart.moveViewToX(0);

            // 차트를 그릴 때 호출해야 합니다.
            calChart.fitScreen();
            calChart.resetZoom();
            calChart.zoomOut();
            calChart.notifyDataSetChanged();
            calChart.getViewPortHandler().refresh(new Matrix(), calChart, true);
            calChart.invalidate();

        }else {
            // 디렉토리 없음

        }
    }

    public void calcYear() {

        int sumTCal = 0;
        int sumECal = 0;
        int resultTCal = 0;
        int resultECal = 0;
        int timeData = 0;

        // 기존 Date
        preWeekTargetDate = targetDate;
        preWeekTargetYear = targetYear;
        preWeekTargetMonth = targetMonth;
        preWeekTargetDay = targetDay;

        int month = lastModifiedYearDirectory();

        targetDate = targetYear + "-" + "01-01";
        targetMonth = "01";
        targetDay = "01";

        String directoryName = "LOOKHEART/" + email + "/" + targetYear;
        File directory = new File(getActivity().getFilesDir(), directoryName);

        if (directory.exists()) {
            // 디렉토리가 있는 경우
            yearDirCheck = true;

            // 1월부터 지정 월까지 반복
            // month
            for (int i = 0; month > i ; i++) {
                YearMonth yearMonth = YearMonth.of(Integer.parseInt(targetYear), Integer.parseInt(targetMonth));
                int daysInMonth = yearMonth.lengthOfMonth();
                numbersOfHourlyCalorieData++;
                sumTCal = 0;
                sumECal = 0;

                // day
                for ( int j = 0 ; daysInMonth > j ; j++) {

                    directoryName = "LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth + "/" + targetDay;
                    directory = new File(getActivity().getFilesDir(), directoryName);

                    // 파일 경로와 이름
                    File file = new File(directory, "CalAndDistanceData.csv");

                    dateCalculate(1, true);

                    timeData = i + 1;

                    if(file.exists()) {
                        // 파일이 있는 경우
                        try {
                            // file read
                            BufferedReader br = new BufferedReader(new FileReader(file));
                            String line;

                            while ((line = br.readLine()) != null) {
                                String[] columns = line.split(","); // 데이터 구분
                                Double tCal = Double.parseDouble(columns[4]);
                                Double eCal = Double.parseDouble(columns[5]);

                                sumTCal += tCal;
                                sumECal += eCal;

                                resultTCal += tCal;
                                resultECal += eCal;
                            }

                            br.close();

                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        // 파일이 없는 경우
                    }
                }
                // 데이터 저장
                yearTCalData.add((double) sumTCal);
                yearECalData.add((double) sumECal);
                yearCalTimeData.add(String.valueOf(timeData));
            }
        }
        else {
            // 디렉토리가 없는 경우
            yearDirCheck = false;
        }

        dateDisplay.setText(targetYear);
        calValue.setText(resultTCal + " kcal");
        eCalValue.setText(resultECal + " kcal");

        tvTargetTCal.setText(targetTCal + " kcal");
        tvTargetECal.setText(targetECal + " kcal");

        int tCalProgress = (int) ((double) resultTCal / yeartargetTCal * 100);
        int eCalProgress = (int) ((double) resultECal / yeartargetECal * 100);

        tCalProgress = Math.min(tCalProgress, 100);
        eCalProgress = Math.min(eCalProgress, 100);

        tCalProgressBar.setProgress(tCalProgress);
        eCalProgressBar.setProgress(eCalProgress);

        // 기존 날짜로 변경
        targetYear = preWeekTargetYear;
        targetMonth = preWeekTargetMonth;
        targetDay = preWeekTargetDay;

        targetDate = preWeekTargetDate;

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

    public void setTargetCal(){
        // o_cal 일일 목표 소비 총 칼로리
        // o_ecal 일일 목표 소비 활동 칼로리
        SharedPreferences sharedPref = getActivity().getSharedPreferences(email, Context.MODE_PRIVATE);

        targetTCal = Integer.parseInt(sharedPref.getString("o_cal", "3000")); // 총 칼로리
        targetECal = Integer.parseInt(sharedPref.getString("o_ecal", "500")); // 활동 칼로리

        weektargetTCal = targetTCal * 7;
        weektargetECal = targetECal * 7;

        yeartargetTCal = targetTCal * 365;
        yeartargetECal = targetECal * 365;
    }
}
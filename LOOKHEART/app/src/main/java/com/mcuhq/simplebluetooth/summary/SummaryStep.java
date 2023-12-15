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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.viewmodel.SharedViewModel;

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

@RequiresApi(api = Build.VERSION_CODES.O)
public class SummaryStep extends Fragment {

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

    /*preTargetTime*/
    //region
    String preWeekTargetYear;
    String preWeekTargetMonth;
    String preWeekTargetDay;
    String preWeekTargetDate;
    //endregion

    /*Week*/
    //region
    ArrayList<Double> weekStepArrayData = new ArrayList<>();
    ArrayList<Double> weekDistanceArrayData = new ArrayList<>();
    List<BarEntry> weekStepEntries = new ArrayList<>();
    List<BarEntry> weekDistanceEntries = new ArrayList<>();

    ArrayList<String> weekTimeData = new ArrayList<>();

    int weektargetStep = 0;
    int weektargetDistance = 0;
    Boolean weekDirCheck;
    //endregion

    /*Month*/
    //region
    ArrayList<Double> monthStepData = new ArrayList<>();
    ArrayList<Double> monthDistanceData = new ArrayList<>();
    List<BarEntry> monthStepEntries = new ArrayList<>();
    List<BarEntry> monthDistanceEntries = new ArrayList<>();

    ArrayList<String> monthTimeData = new ArrayList<>();

    int monthtargetStep = 0;
    int monthtargetDistance = 0;

    Boolean monthDirCheck;
    //endregion

    /*Year*/
    //region
    ArrayList<Double> yearStepData = new ArrayList<>();
    ArrayList<Double> yearDistanceData = new ArrayList<>();
    List<BarEntry> yearStepEntries = new ArrayList<>();
    List<BarEntry> yearDistanceEntries = new ArrayList<>();
    int yeartargetStep = 0;
    int yeartargetDistance = 0;
    ArrayList<String> yearTimeData = new ArrayList<>();
    //endregion

    /*target*/
    //region
    int targetStep = 0;
    int targetDistance = 0;
    int targetDistanceKm = 0;
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

    /*button*/
    //region
    Button dayButton;
    Button weekButton;
    Button monthButton;
    Button yearButton;
    Button[] buttons;
    //endregion

    /*imagebutton*/
    //region
    ImageButton yesterdayButton;
    ImageButton tomorrowButton;
    //endregion

    /*textView*/
    //region
    TextView dateDisplay;

    TextView stepValue;
    TextView distanceValue;

    TextView tvTargetStep;
    TextView tvTargetDistance;

    //endregion

    /*ProgressBar*/
    //region
    ProgressBar stepProgressBar;
    ProgressBar distanceProgressBar;
    //endregion

    private SharedViewModel viewModel;

    private BarChart stepChart;

    Boolean yearDirCheck;
    int numbersOfStepAndDistanceData = 0;
    View view;
    private String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_summary_step, container, false);

        SharedPreferences emailSharedPreferences = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        email = emailSharedPreferences.getString("email", "null");

        stepChart = view.findViewById(R.id.stepChart);

        dayButton = view.findViewById(R.id.summaryStepDayButton);
        weekButton = view.findViewById(R.id.summaryStepWeekButton);
        monthButton = view.findViewById(R.id.summaryStepMonthButton);
        yearButton = view.findViewById(R.id.summaryStepYearButton);

        yesterdayButton = view.findViewById(R.id.yesterdayButton);
        tomorrowButton = view.findViewById(R.id.tomorrowButton);

        dateDisplay = view.findViewById(R.id.dateDisplay);

        stepValue = view.findViewById(R.id.summaryStepValue);
        distanceValue = view.findViewById(R.id.summaryDistanceValue);

        tvTargetStep = view.findViewById(R.id.targetStep);
        tvTargetDistance = view.findViewById(R.id.targetDistance);

        stepProgressBar = view.findViewById(R.id.stepProgress);
        distanceProgressBar = view.findViewById(R.id.distanceProgress);

        buttons = new Button[] {dayButton, weekButton, monthButton, yearButton};

        setTargetStep();

        currentTimeCheck();

        todayStepChartGraph();

        dayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setColor(dayButton);
                todayStepChartGraph();

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
                weekStepChartGraph();

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
                monthStepChartGraph();

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
                yearStepChartGraph();

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

        viewModel.getStepText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String string) {
                targetStep = Integer.parseInt(string);
                tvTargetStep.setText(targetStep + getResources().getString(R.string.stepValue2));
            }

        });
        viewModel.getDistanceText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String string) {
                targetDistance = Integer.parseInt(string);
                tvTargetDistance.setText(targetDistance + getResources().getString(R.string.distanceValue2));
            }
        });

        return view;

    }

    public void tomorrowButtonEvent() {

        for( int i = 0 ; 20 > i ; i++) {
            stepChart.zoomOut();
        }

        setDateButtonEvent(true);
    }

    void setDateButtonEvent(boolean check){
        if(dayCheck) {
            dateCalculate(1, check);
            todayStepChartGraph();
        }
        else if(weekCheck) {
            dateCalculate(7, check);
            weekStepChartGraph();;
        }
        else if(monthCheck) {
            setTimeCalculate(check);
            monthStepChartGraph();;
        }
        else {
            // year
            setTimeCalculate(check);
            yearStepChartGraph();
        }
    }

    public void yesterdayButtonEvent() {
        setDateButtonEvent(false);
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
        date = LocalDate.parse(targetDate, formatter);

        targetYear = date.format(yearFormat);
        targetMonth = date.format(monthFormat);
        targetDay = date.format(dayFormat);

    }

    public void setTimeCalculate(boolean check) {
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

    public void todayStepChartGraph(){

        stepChart.clear();

        dateDisplay.setText(targetDate);

        int sumStep = 0;
        int sumDistance = 0;
        int resultStep = 0;
        int resultDistance = 0;
        numbersOfStepAndDistanceData = 0;

        // 경로
        File directory = getFileDirectory("LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth + "/" + targetDay);

        // 파일 경로와 이름
        File file = new File(directory, "CalAndDistanceData.csv");

        if (file.exists()) {
            // 파일이 있는 경우

            // arr data가 저장되는 배열 리스트
            ArrayList<Double> stepArrayData = new ArrayList<>();
            ArrayList<Double> distanceArrayData = new ArrayList<>();

            // 그래프의 x축(시간) y축(데이터)이 저장되는 Entry 리스트
            List<BarEntry> stepEntries = new ArrayList<>();
            List<BarEntry> distanceEntries = new ArrayList<>();

            // arr time data가 저장되는 배열 리스트
            ArrayList<String> timeData = new ArrayList<>();


            try {
                // file read
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    String[] columns = line.split(","); // 데이터 구분
                    Double step = Double.parseDouble(columns[2]);
                    Double distance = Double.parseDouble(columns[3]);
                    sumStep = 0;
                    sumDistance = 0;

                    numbersOfStepAndDistanceData++;
                    String myArrTimeRow = columns[0];

                    sumStep += step;
                    sumDistance += distance;

                    resultStep += step;
                    resultDistance += distance;

                    // 데이터 저장
                    timeData.add(myArrTimeRow);
                    stepArrayData.add(step);
                    distanceArrayData.add(distance);
                }

                // 그래프에 들어갈 데이터 저장
                for (int i = 0; i < stepArrayData.size(); i++) {
                    stepEntries.add((BarEntry) new BarEntry((float)i, stepArrayData.get(i).floatValue()));
                    distanceEntries.add((BarEntry) new BarEntry((float)i, distanceArrayData.get(i).floatValue()));
                }

                br.close();

            }catch (IOException e) {
                e.printStackTrace();
            }

            // 그래프 Set
            BarDataSet tCaldataSet = getBarDataSet(stepEntries,getResources().getString(R.string.step),Color.RED);

            // 그래프 Set
            BarDataSet eCaldataSet = getBarDataSet(distanceEntries,getResources().getString(R.string.distanceM),Color.BLUE);

            setStepChartOption(tCaldataSet,eCaldataSet,timeData);
        }

        else {
            // 파일이 없는 경우
        }

        stepValue.setText(resultStep + " " + getResources().getString(R.string.stepValue2));
        distanceValue.setText(resultDistance + " " + getResources().getString(R.string.distanceM2));
        tvTargetStep.setText(targetStep + " " + getResources().getString(R.string.stepValue2));
        tvTargetDistance.setText(targetDistance  + " " + getResources().getString(R.string.distanceValue2));

        int tCalProgress = (int) ((double) resultStep / targetStep * 100);
        int eCalProgress = (int) ((double) resultDistance / targetDistanceKm * 100);

        tCalProgress = Math.min(tCalProgress, 100);
        eCalProgress = Math.min(eCalProgress, 100);

        stepProgressBar.setProgress(tCalProgress);
        distanceProgressBar.setProgress(eCalProgress);

    }

    public void weekStepChartGraph(){

        numbersOfStepAndDistanceData = 0;

        stepChart.clear();

        weekStepArrayData.clear();
        weekDistanceArrayData.clear();

        weekStepEntries.clear();
        weekDistanceEntries.clear();

        calcWeek();

        if (weekDirCheck) {
            // 파일 있음

            // 그래프에 들어갈 데이터 저장
            for (int i = 0; i < weekStepArrayData.size(); i++) {
                weekStepEntries.add((BarEntry) new BarEntry((float)i, weekStepArrayData.get(i).floatValue()));
                weekDistanceEntries.add((BarEntry) new BarEntry((float)i, weekDistanceArrayData.get(i).floatValue()));
            }

            // 그래프 Set
            BarDataSet tCaldataSet = getBarDataSet(weekStepEntries,getResources().getString(R.string.step),Color.RED);

            // 그래프 Set
            BarDataSet eCaldataSet = getBarDataSet(weekDistanceEntries, getResources().getString(R.string.distanceM),Color.BLUE);

            setStepChartOption(tCaldataSet,eCaldataSet,weekTimeData);
        }
        else {
            // 파일 없음
        }
    }

    public void calcWeek(){

        // 화면에 보여주는 날짜 값
        String displayMonth;
        String displayDay;

        int sumStep = 0;
        int sumDistance = 0;
        int resultStep = 0;
        int resultDistance = 0;

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
        setPreTime();

        dateCalculate(searchMonday, false);

        // 화면에 보여줄 Date
        displayMonth = targetMonth;
        displayDay = targetDay;

        // 월 ~ 일
        for(int i = 0; 7 > i ; i++){
            // 경로
            numbersOfStepAndDistanceData++;

            sumStep = 0;
            sumDistance = 0;

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
                        Double step = Double.parseDouble(columns[2]);
                        Double distance = Double.parseDouble(columns[3]);

                        sumStep += step;
                        sumDistance += distance;

                        resultStep += step;
                        resultDistance += distance;

                    }
                    // 데이터 저장
                    weekStepArrayData.add((double) sumStep);
                    weekDistanceArrayData.add((double) sumDistance);
                    weekTimeData.add(weekDays[i]);

                    br.close();

                }catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else {
                // 파일이 없는 경우

                // 데이터 저장
                weekStepArrayData.add(0.0);
                weekDistanceArrayData.add(0.0);
                weekTimeData.add(weekDays[i]);
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
        stepValue.setText(resultStep + " " + getResources().getString(R.string.stepValue2));
        distanceValue.setText(resultDistance + " " + getResources().getString(R.string.distanceM2));

        tvTargetStep.setText(targetStep  + " " + getResources().getString(R.string.stepValue2));
        tvTargetDistance.setText(targetDistance + " " + getResources().getString(R.string.distanceValue2));

        int stepProgress = (int) ((double) resultStep / weektargetStep * 100);
        int distanceProgress = (int) ((double) resultDistance / weektargetDistance * 100);

        stepProgress = Math.min(stepProgress, 100);
        distanceProgress = Math.min(distanceProgress, 100);

        stepProgressBar.setProgress(stepProgress);
        distanceProgressBar.setProgress(distanceProgress);

        // 기존 날짜로 변경
        setTargetTime();
    }

    public void monthStepChartGraph(){

        stepChart.clear();

        numbersOfStepAndDistanceData = 0;

        stepChart.clear();

        monthStepData.clear();
        monthDistanceData.clear();

        monthStepEntries.clear();
        monthDistanceEntries.clear();


        calcMonth();

        if(monthDirCheck) {
            // 디렉토리 있음
            // 그래프에 들어갈 데이터 저장
            for (int i = 0; i < monthStepData.size(); i++) {
                monthStepEntries.add((BarEntry) new BarEntry((float)i, monthStepData.get(i).floatValue()));
                monthDistanceEntries.add((BarEntry) new BarEntry((float)i, monthDistanceData.get(i).floatValue()));
            }

            // 그래프 Set
            BarDataSet tCaldataSet = getBarDataSet(monthStepEntries, getResources().getString(R.string.step),Color.RED);
            BarDataSet eCaldataSet = getBarDataSet(monthDistanceEntries, getResources().getString(R.string.distanceM),Color.BLUE);

            setStepChartOption(tCaldataSet,eCaldataSet,monthTimeData);
        }else {
            // 디렉토리 없음

        }
    }

    public void calcMonth() {
        YearMonth yearMonth = YearMonth.of(Integer.parseInt(targetYear), Integer.parseInt(targetMonth));
        int daysInMonth = yearMonth.lengthOfMonth();

        int sumStep = 0;
        int sumDistance = 0;
        int resultStep = 0;
        int resultDistance = 0;

        int timeData = 0;
        int days = lastModifiedDirectory("LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth); // 마지막으로 수정된 파일 넘버 찾기

        // 기존 Date
        setPreTime();

        File directory = getFileDirectory("LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth);

        if (directory.exists()) {
            // 디렉토리가 있는 경우
            monthDirCheck = true;
            // 1일까지 날짜 이동
            dateCalculate(days - 1, false);

            for( int i = 0;  days > i ; i++ ){

                sumStep = 0;
                sumDistance = 0;

                directory = getFileDirectory("LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth + "/" + targetDay);

                // 파일 경로와 이름
                File file = new File(directory, "CalAndDistanceData.csv");

                dateCalculate(1, true);

                timeData = i + 1;
                numbersOfStepAndDistanceData ++;

                if(file.exists()) {
                    // 파일이 있는 경우

                    try {
                        // file read
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String line;

                        while ((line = br.readLine()) != null) {
                            String[] columns = line.split(","); // 데이터 구분
                            Double step = Double.parseDouble(columns[2]);
                            Double distance = Double.parseDouble(columns[3]);

                            sumStep += step;
                            sumDistance += distance;

                            resultStep += step;
                            resultDistance += distance;
                        }

                        // 데이터 저장
                        monthStepData.add((double) sumStep);
                        monthDistanceData.add((double) sumDistance);
                        monthTimeData.add(String.valueOf(timeData));

                        br.close();

                    }catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    // 파일이 없는 경우
                    // 데이터 저장
                    monthStepData.add(0.0);
                    monthDistanceData.add(0.0);
                    monthTimeData.add(String.valueOf(timeData));
                }
            }
        }
        else {
            // 디렉토리가 없는 경우
            monthDirCheck = false;
        }

        dateDisplay.setText(preWeekTargetYear + "." + preWeekTargetMonth);
        stepValue.setText(resultStep + " " + getResources().getString(R.string.stepValue2));
        distanceValue.setText(resultDistance + " " + getResources().getString(R.string.distanceM2));

        monthtargetStep = targetStep * daysInMonth;
        monthtargetDistance = targetDistanceKm * daysInMonth;

        tvTargetStep.setText(targetStep + " " + getResources().getString(R.string.stepValue2));
        tvTargetDistance.setText(targetDistance + " " + getResources().getString(R.string.distanceValue2));

        int stepProgress = (int) ((double) resultStep / monthtargetStep * 100);
        int distanceProgress = (int) ((double) resultDistance / monthtargetDistance * 100);

        stepProgress = Math.min(stepProgress, 100);
        distanceProgress = Math.min(distanceProgress, 100);

        stepProgressBar.setProgress(stepProgress);
        distanceProgressBar.setProgress(distanceProgress);

        // 기존 날짜로 변경
        setTargetTime();
    }

    public int lastModifiedDirectory(String name){
        File directory = new File(getActivity().getFilesDir(), name);
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

    public void yearStepChartGraph(){

        stepChart.clear();

        numbersOfStepAndDistanceData = 0;

        stepChart.clear();

        yearStepData.clear();
        yearDistanceData.clear();

        yearStepEntries.clear();
        yearDistanceEntries.clear();

        calcYear();

        if(yearDirCheck) {
            // 디렉토리 있음
            // 그래프에 들어갈 데이터 저장
            for (int i = 0; i < yearStepData.size(); i++) {
                yearStepEntries.add((BarEntry) new BarEntry((float)i, yearStepData.get(i).floatValue()));
                yearDistanceEntries.add((BarEntry) new BarEntry((float)i, yearDistanceData.get(i).floatValue()));
            }

            // 그래프 Set
            BarDataSet tCaldataSet = getBarDataSet(yearStepEntries, getResources().getString(R.string.step),Color.RED);

            // 그래프 Set
            BarDataSet eCaldataSet = getBarDataSet(yearDistanceEntries, getResources().getString(R.string.distanceM),Color.BLUE);

            setStepChartOption(tCaldataSet,eCaldataSet,yearTimeData);
        }else {
            // 디렉토리 없음

        }
    }

    public void calcYear() {

        int sumStep = 0;
        int sumDistance = 0;

        int resultStep = 0;
        int resultDistance = 0;

        int timeData = 0;

        // 기존 Date
        preWeekTargetDate = targetDate;
        preWeekTargetYear = targetYear;
        preWeekTargetMonth = targetMonth;
        preWeekTargetDay = targetDay;

        int month = lastModifiedDirectory("LOOKHEART/" + email + "/" + targetYear);

        targetDate = targetYear + "-" + "01-01";
        targetMonth = "01";
        targetDay = "01";

        File directory = getFileDirectory("LOOKHEART/" + email + "/" + targetYear);

        if (directory.exists()) {
            // 디렉토리가 있는 경우
            yearDirCheck = true;

            // 1월부터 지정 월까지 반복
            // month
            for (int i = 0; month > i ; i++) {
                YearMonth yearMonth = YearMonth.of(Integer.parseInt(targetYear), Integer.parseInt(targetMonth));
                int daysInMonth = yearMonth.lengthOfMonth();
                numbersOfStepAndDistanceData++;

                sumStep = 0;
                sumDistance = 0;

                // day
                for ( int j = 0 ; daysInMonth > j ; j++) {

                    directory = getFileDirectory("LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth + "/" + targetDay);

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
                                Double step = Double.parseDouble(columns[2]);
                                Double distance = Double.parseDouble(columns[3]);

                                sumStep += step;
                                sumDistance += distance;

                                resultStep += step;
                                resultDistance += distance;
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
                yearStepData.add((double) sumStep);
                yearDistanceData.add((double) sumDistance);
                yearTimeData.add(String.valueOf(timeData));
            }
        }
        else {
            // 디렉토리가 없는 경우
            yearDirCheck = false;
        }

        dateDisplay.setText(targetYear);
        stepValue.setText(resultStep + " " + getResources().getString(R.string.stepValue2));
        distanceValue.setText(resultDistance + " " + getResources().getString(R.string.distanceM2));

        tvTargetStep.setText(targetStep + " " + getResources().getString(R.string.stepValue2));
        tvTargetDistance.setText(targetDistance + " " + getResources().getString(R.string.distanceValue2));

        int stepProgress = (int) ((double) resultStep / yeartargetStep * 100);
        int distanceProgress = (int) ((double) resultDistance / yeartargetDistance * 100);

        stepProgress = Math.min(stepProgress, 100);
        distanceProgress = Math.min(distanceProgress, 100);

        stepProgressBar.setProgress(stepProgress);
        distanceProgressBar.setProgress(distanceProgress);

        // 기존 날짜로 변경
        setTargetTime();

    }

    void setPreTime(){
        preWeekTargetDate = targetDate;
        preWeekTargetYear = targetYear;
        preWeekTargetMonth = targetMonth;
        preWeekTargetDay = targetDay;
    }

    void setTargetTime(){
        targetYear = preWeekTargetYear;
        targetMonth = preWeekTargetMonth;
        targetDay = preWeekTargetDay;
        targetDate = preWeekTargetDate;
    }

    void setStepChartOption(BarDataSet tCaldataSet,BarDataSet eCaldataSet,ArrayList<String> timeData){
        float groupSpace = 0.3f;
        float barSpace = 0.05f;
        float barWidth = 0.3f;

        BarData todaystepChartData = new BarData(tCaldataSet, eCaldataSet);
        todaystepChartData.setBarWidth(barWidth);

        stepChart.getXAxis().setAxisMinimum(0f);
        stepChart.getXAxis().setAxisMaximum(0f + todaystepChartData.getGroupWidth(groupSpace, barSpace) * (numbersOfStepAndDistanceData));  // group count : 2
        todaystepChartData.groupBars(0f, groupSpace, barSpace);

        Legend legend = stepChart.getLegend();
        legend.setFormSize(12f); // Font size
        legend.setTypeface(Typeface.DEFAULT_BOLD);


        stepChart.setNoDataText("");
        stepChart.setData(todaystepChartData);
        stepChart.getXAxis().setEnabled(true);
        stepChart.getXAxis().setCenterAxisLabels(true);
        stepChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(timeData));
        stepChart.getXAxis().setGranularity(1f);
        stepChart.getXAxis().setLabelCount(timeData.size(), false);
        stepChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        stepChart.getXAxis().setDrawGridLines(false);
        stepChart.getDescription().setEnabled(false);

        stepChart.getAxisLeft().setGranularityEnabled(true);
        stepChart.getAxisLeft().setGranularity(1.0f);
        stepChart.getAxisLeft().setAxisMinimum(0f);
        stepChart.getAxisRight().setEnabled(false);
        stepChart.setDrawMarkers(false);
        stepChart.setDragEnabled(true);
        stepChart.setPinchZoom(false);
        stepChart.setDoubleTapToZoomEnabled(false);
        stepChart.setHighlightPerTapEnabled(false);
        stepChart.moveViewToX(0);

        // 차트를 그릴 때 호출해야 합니다.
        stepChart.fitScreen();
        stepChart.resetZoom();
        stepChart.zoomOut();
        stepChart.notifyDataSetChanged();
        stepChart.getViewPortHandler().refresh(new Matrix(), stepChart, true);
        stepChart.invalidate();
    }

    BarDataSet getBarDataSet(List<BarEntry> data,String label,int Color){
        BarDataSet dataSet = new BarDataSet(data, label);
        dataSet.setColor(Color);
        dataSet.setDrawValues(false);
        return dataSet;
    }

    File getFileDirectory(String name){
        String directoryName = name;
        return new File(getActivity().getFilesDir(), directoryName);
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

    public void setTargetStep(){
        // o_cal 일일 목표 소비 총 칼로리
        // o_ecal 일일 목표 소비 활동 칼로리
        SharedPreferences sharedPref = getActivity().getSharedPreferences(email, Context.MODE_PRIVATE);

        targetStep = Integer.parseInt(sharedPref.getString("o_step", "2000"));
        targetDistance = Integer.parseInt(sharedPref.getString("o_distance", "5"));

        targetDistanceKm = targetDistance * 1000;

        weektargetStep = targetStep * 7;
        weektargetDistance = targetDistanceKm * 7;

        yeartargetStep = targetStep * 365;
        yeartargetDistance = targetDistanceKm * 365;
    }
}
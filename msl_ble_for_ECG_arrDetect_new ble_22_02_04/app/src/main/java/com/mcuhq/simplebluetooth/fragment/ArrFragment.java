package com.mcuhq.simplebluetooth.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Matrix;
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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.SharedViewModel;
import com.mcuhq.simplebluetooth.summary.SummaryArr;

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
import java.util.Arrays;
import java.util.Comparator;
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

    ArrayList<String> arrList = new ArrayList<String>();
    ArrayList<String> arrFileNameList = new ArrayList<String>();
    int arrCnt;
    int preArrCnt;
    int updateArrCnt;
    Boolean startFlag = false;

    private String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_arr, container, false);

        SharedPreferences emailSharedPreferences = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        email = emailSharedPreferences.getString("email", "null");

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
        targetDateCheck();
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

        viewModel.removeAllArrList(); // 첫 시작 시 리스트가 있다면 제거
        viewModel.getArrList().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                arrList = strings;
                updateArrCnt = arrList.size();

                if (startFlag && updateArrCnt != 0){
                    Log.e("arrList", arrList.toString());

                    refreshTodayArrList();

                    // 최하단 포커스
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }

                startFlag = true;
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
        String directoryName = "LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth + "/" + targetDay + "/" + "arrEcgData";
        File directory = new File(getActivity().getFilesDir(), directoryName);

        if (directory.exists()){
            int number = 1;
            String fileName = null;

            // 디렉토리 존재
            File[] files = directory.listFiles();

            for (int i = 0 ; files.length > i ; i++){

                Button button = new Button(getActivity());
                Button text = new Button(getActivity());

                button.setText(""+number);
                button.setId(number+1000);
                text.setId(number);

                try {
                    String arrTime = searchArrDate(String.valueOf(number));
                    fileName = targetYear + "-" + targetMonth + "-" + targetDay + " " + arrTime;

                } catch (Exception e) {
                    e.printStackTrace();
                }

//                Log.e("fileName", fileName);

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

                number++;
                if (!startFlag)
                    arrCnt++;
            }

        }
        else {
            // 디렉토리 없음
        }
    }

    public void refreshTodayArrList() {

        // 경로
        String directoryName = "LOOKHEART/" + email + "/" + currentYear + "/" + currentMonth + "/" + currentDay + "/" + "arrEcgData";
        File directory = new File(getActivity().getFilesDir(), directoryName);

        if (directory.exists()){
            int number = 1;
            String fileName = null;

            // 디렉토리 존재
            File[] files = directory.listFiles();

            // 파일 이름에서 마지막 숫자를 추출하여 정렬
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    String name1 = o1.getName();
                    String name2 = o2.getName();
                    String[] split1 = name1.split("_");
                    String[] split2 = name2.split("_");

                    int num1 = Integer.parseInt(split1[split1.length - 1].replace(".csv", ""));
                    int num2 = Integer.parseInt(split2[split2.length - 1].replace(".csv", ""));

                    return Integer.compare(num1, num2);
                }
            });

            Log.e("arrList.size()", String.valueOf(arrList.size()));

            Button button = new Button(getActivity());
            Button text = new Button(getActivity());

            button.setText(String.valueOf(arrCnt+1));

            button.setId((arrCnt+1)+1000);
            text.setId((arrCnt+1));

            // fileName
            fileName = targetDate + " " + arrList.get(0);

            Log.e("fileName", fileName);
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

            // 컬러 설정
            button.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.arr_button_normal));
            button.setTextColor(Color.WHITE);
            button.setTextSize(14);
            button.setTypeface(null, Typeface.BOLD);

            text.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.home_bottom_button));
            text.setText(fileName);
            text.setClickable(false);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (Button otherButton : buttonList) {
                        otherButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.arr_button_normal));
                    }
                    Button clickedButton = (Button) v;
                    String buttonText = clickedButton.getText().toString();
                    clickedButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.arr_botton_press));

                    Button textButton = getActivity().findViewById(Integer.parseInt((String) clickedButton.getText()));
                    for (Button otherButton : textList) {
                        otherButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.home_bottom_button));
                    }
                    textButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bpm_border));

                    searchArrChart(buttonText);
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

            arrCnt++;

            // 버튼 추가 시 기존 리스트 삭제
            viewModel.removeArrList(0);

            // 같은 시간인 경우만 버튼 추가
            if (currentDate.equals(targetDate)) {
                buttonList.add(button);
                textList.add(text);
                arrButton.addView(button);
                arrText.addView(text);
            }

        }
        else {
            // 디렉토리 없음
        }
    }

    public void searchArrChart(String buttonNumber) {

//        Log.e("buttonNumber", buttonNumber);

        arrChart.clear();
        statusText.setText("");
        status.setText("");
        arrStatusText.setText("");
        arrStatus.setText("");


        // 경로
        String directoryName = "LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth + "/" + targetDay + "/" + "arrEcgData";
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

    }

    public void targetDateCheck() {

        targetYear = currentYear;
        targetMonth = currentMonth;
        targetDay = currentDay;

        targetDate = currentDate;
    }

    public void setStatus(String myStatus, String myArrStatus){

        statusText.setText(getResources().getString(R.string.arrState));
        arrStatusText.setText(getResources().getString(R.string.arrType));

        switch (myStatus){
            case "R":
                status.setText(getResources().getString(R.string.rest));
                break;
            case "E":
                status.setText(getResources().getString(R.string.exercise));
                break;
            case "S":
                status.setText(getResources().getString(R.string.sleep));
                break;
            default:
                status.setText(getResources().getString(R.string.rest));
                break;
        }

        switch (myArrStatus){
            case "arr":
                arrStatus.setText(getResources().getString(R.string.typeArr));
                break;
            case "fast":
                arrStatus.setText(getResources().getString(R.string.typeFastArr));
                break;
            case "slow":
                arrStatus.setText(getResources().getString(R.string.typeSlowArr));
                break;
            case "irregular":
                arrStatus.setText(getResources().getString(R.string.typeHeavyArr));
                break;
            default:
                arrStatus.setText(getResources().getString(R.string.typeArr));
                break;
        }
    }

    public String searchArrDate(String arrNumber){
        String arrDate = null;

        // 경로
        String directoryName = "LOOKHEART/" + email + "/" + targetYear + "/" + targetMonth + "/" + targetDay + "/" + "arrEcgData";
        File directory = new File(getActivity().getFilesDir(), directoryName);

        // 파일 경로와 이름
        File file = new File(directory, "arrEcgData_" + arrNumber + ".csv");

        if (file.exists()) {
            // 파일 있음
            try {
                // file read
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = br.readLine();
                String[] columns = line.split(",");
                arrDate = columns[0];
                br.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            // 파일 없음
        }
        return arrDate;
    }

}
package com.mcuhq.simplebluetooth.fragment.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.mcuhq.simplebluetooth.base.MyTimeZone;
import com.mcuhq.simplebluetooth.gps.GpsTracker;
import com.mcuhq.simplebluetooth.noti.NotificationManager;
import com.mcuhq.simplebluetooth.permission.PermissionManager;
import com.mcuhq.simplebluetooth.R;

import com.mcuhq.simplebluetooth.server.RetrofitServerManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;

import com.library.lookheartLibrary.controller.PeakController;
import com.library.lookheartLibrary.viewmodel.SharedViewModel;
import com.library.lookheartLibrary.controller.dateController;
import com.library.lookheartLibrary.server.UserProfile;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment implements PermissionManager.PermissionCallback {

    /*final*/
    //region
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static UUID UUID_SERVICE =
            UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    public final static UUID UUID_RECIEVE =
            UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    public final static UUID UUID_TX =
            UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    public final static UUID UUID_CONFIG =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private final static int ANDROID_O_MR1 = 33;
    private final static long SCAN_PERIOD = 10000;
    private final static int STATE_DISCONNECTED = 0;
    private final static int STATE_CONNECTED = 2;
    private final static int MAX_RETRY_COUNT = 6;
    private final static int _MAX_CH = 6;
    private final static int GRAPH_MAX = 500;

    private static final int NOTIFICATION_ID = 1610;
    private static final int ARR_NOTIFICATION_ID = 1611;
    //endregion

    /*ARR*/
    //region
    private static final String ARR = "arr";
    private static final String BRADYCARDIA = "bradycardia";
    private static final String TACHYCARDIA = "tachycardia";
    private static final String ATRIAL_FIBRILLATION = "atrialFibrillation";
    private static final String MYO = "myo";
    private static final String NON_CONTACT = "nonContact";
    private static final String ARR_50 = "50";
    private static final String ARR_100 = "100";
    private static final String ARR_200 = "200";
    private static final String ARR_300 = "300";
    private static final String STATUS_TACHYCARDIA = "fast";
    private static final String STATUS_BRADYCARDIA = "slow";
    private static final String STATUS_IRREGULAR = "irregular";

    private static final int EVENT_ARR = 1;
    private static final int EVENT_HEARTATTACK = 2;
    private static final int EVENT_NON_CONTACT = 3;
    private static final int EVENT_MYO = 4;
    private static final int EVENT_FAST_ARR = 5;
    private static final int EVENT_SLOW_ARR = 6;
    private static final int EVENT_IRREGULAR_ARR = 7;


    private Thread thread;
    private long tickLast = 0;
    //endregion

    /*localDataSave Flag variables*/
    //region
    private Boolean localDataLoop;
    //endregion

    /*RetrofitServerManager variables*/
    //region
    public RetrofitServerManager retrofitServerManager;
    //endregion

    /*SharedPreferences variables*/
    //region
    private SharedPreferences userDetailsSharedPref;
    private SharedPreferences.Editor userDetailsEditor;
    //endregion

    /*guardian variables*/
    //region
    private String firstGuardian = "", secondGuardian = "";
    private boolean firstCheck = false, secondCheck = false, guardianCheck = false;
    private AlertDialog guardianAlertDialog;
    //endregion

    /*FragmentActivity activity*/
    //region
    FragmentActivity activity;
    //endregion

    /*GpsTracker variables*/
    //region
    private GpsTracker gpsTracker;
    //endregion

    /*viewModel variables*/
    //region
    private SharedViewModel viewModel;
    //endregion

    /*profileData variables*/
    //region
    private String userName;
    private String email;
    private int iAge;
    private int iGender;
    private double dWeight;
    private int disheight;
    private int eCalBPM;
    private int sleep;
    private int wakeup;
    //endregion

    /*notification variables*/
    //region
    private NotificationManager notificationManager;
    //endregion

    /*사용자 설정 FLAG*/
    //region
    private boolean nonContactCheck = true;
    private boolean myoCheck = true;
    private boolean setHeartAttackNotiFlag = false;
    private boolean setArrNotiFlag = false;
    private boolean setMyoNotiFlag = false;
    private boolean setNonContactNotiFlag = false;
    private boolean setTachycardiaNotiFlag = false;
    private boolean setBradycardiaNotiFlag = false;
    private boolean setAtrialFibrillationNotiFlag = false;
    private boolean hourlyArrNotificationFlag = false;
    private boolean totalArrNotificationFlag = false;
    private boolean HeartAttackCheck = false;
    //endregion

    /*onBackPressed variables*/
    //region
    private AlertDialog onBackPressedDialog;
    //endregion

    /*ForegroundService variables*/
    //region
    private Intent serviceIntent;
    //endregion

    /*Permissions variables*/
    //region
    private PermissionManager permissionManager;
    //endregion

    /*tenSecondData variables*/
    //region
    // Home 왼쪽 상단에 표시되는 데이터의 변수
    // BPM
    private int tenSecondAvgBPM;
    private int tenSecondAvgBpmCnt;
    private int tenSecondBpmSum;
    private int tenMinuteAvgBPM;
    private int tenMinuteAvgBpmCnt;
    private int tenMinuteBpmSum;
    private int diffAvgBPM;
    private Boolean diffAvgFlag;

    // Min BPM
    private int minBPM;
    private int diffMinBPM;
    private Boolean diffMinFlag;

    // Max BPM
    private int maxBPM;
    private int diffMaxBPM;
    private Boolean diffMaxFlag;

    // HRV
    private int tenSecondHRVSum;
    private int tenSecondAvgHRV;

    // TEMP
    private double tenSecondTempSum;
    private double tenSecondAvgTemp;

    // 10초 동안 쌓이는 데이터
    private int tenSecondStep = 0;
    private int tenSecondArrCnt = 0;
    private double tenSecondCal = 0;
    private double tenSecondECal = 0;
    private double tenSecondDistance = 0;
    private double tenSecondDistanceKM = 0;
    private double tenSecondDistanceM = 0;
    //endregion

    /*HourlyData variables*/
    //region
    // 매시간 저장되는 변수
    private int hourlyAllstep = 0;
    private double hourlyDistance = 0;
    private double hourlyTotalCal = 0;
    private double hourlyExeCal = 0;
    private int hourlyArrCnt = 0;

    // 10초 마다 서버로 보내는 시간대 별 데이터 (sendCalcDataAsCsv)
    private String sendHourlyStep;
    private String sendHourlyDistance;
    private String sendHourlyTCal;
    private String sendHourlyECal;
    private String sendHourlyArrCnt;
    //endregion

    /*dailyData variables*/
    //region
    private int allstep;
    private double distance;
    private double wdistance;
    private int arrCnt = 0;
    private double dCal;
    private double dExeCal;

    // doLoop()
    private volatile boolean isRun = false;
    private double dCalMinU = 0;
    private int iCalTimer;
    private int iCalTimeCount;
    private double pBPM;
    private double cBPM;
    private double realBPM;
    private int dpscnt = 0;
    private float dps = 0;
    private double realDistanceKM;
    private double realDistanceM;
    private double avgsize;
    private int nowstep;
    private String bodyStatus = ""; // 휴식, 활동, 수면
    //endregion

    /*ecgData variables*/
    //region
    // 14개 씩 들어오는 ecg 데이터를 10개 씩 모아서 서버로 보내기 위한 변수 ( 140개 )
    private double[] ecgPacket = new double[14];
    private double[] peackPacket = new double[14];
    private StringBuilder ecgPacketList = new StringBuilder();
    private int ecgPacketCnt = 0;
    private int ecgCnt;
    //endregion

    /*doBufProcess() variables*/
    //region
    // BLE Data 저장
    private final double[] bluetoothData = new double[_MAX_CH];
    private String[] lVName = new String[_MAX_CH]; // 확인 후 삭제 필요
    private int intHRV;
    private int intBPM;
    private double doubleBAT;
    private double doubleTEMP;
    private String stringHRV;
    private String stringBPM;
    private String stringArr;
    private String stringArrCnt;
    private int intRealBPM;
    private String stringRealBPM;
    private String stringRealHRV;

    // BLE ECG DATA VAR
    private final double[] dRecv = new double[500];
    private final double[] dRecvLast = new double[500];
    private int iRecvCnt = 0;
    private int iRecvLastCnt = 0;

    //endregion

    /*Arr variables*/
    //region
    private int arr;
    private Map<String, Integer> retryCounts = new HashMap<>();
    private String arrStatus = "";
    private int currentArrCnt = 0;
    private int previousArrCnt = 0;
    //endregion

    /*DateAndTime variables*/
    //region
    private String currentYear;
    private String currentMonth;
    private String currentDay;
    private String currentHour;
    private String currentDate;
    private String currentTime;
    private String currentMinute;
    private String currentSecond;
    private String pre_time_Hour = "";
    private String pre_date_pause = "";
    private Date mDate;
    private Time mTime;

    // utc and countryCode
    private String currentCountry;
    private String utcOffset;
    private String utcOffsetAndCountry;
    //endregion

    /*BLE variables*/
    //region
    private IntentFilter stateFilter = new IntentFilter(); // BLE STATE FILTER
    private BluetoothGattService mBluetoothGattService;
    private BluetoothGatt bluetoothGatt;
    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private BluetoothLeScanner btScanner;
    private String deviceMacAddress;
    private ArrayList<BluetoothDevice> devicesDiscovered = new ArrayList<BluetoothDevice>();
    private List<String> lTx = new ArrayList<>();

    private int isBTType = 1; // 0=BTClassic 1=BLE
    private Handler bleHandler;
    private Handler handler2 = new Handler();
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private Boolean BTConnected = false;
    private Boolean firstBleConnect = false;
    private int mConnectionState = 0;
    private boolean useBleFlag = false; // 장치 사용 플래그
    //endregion

    /*layout variables*/
    //region
    private View view;
    private LineChart chart;

    // 왼쪽 상단 view
    private TextView bpm_value;
    private TextView bpm_maxValue;
    private TextView bpm_diffMaxValue;
    private TextView bpm_avgValue;
    private TextView bpm_diffAvgValue;
    private TextView bpm_minValue;
    private TextView bpm_diffMinValue;
    private TextView hrv_value;
    private TextView arr_value;
    private TextView eCal_value;
    private TextView step_value;
    private TextView temp_value;
    private TextView distance_value;

    // body state var
    private TextView restText;
    private TextView sleepText;
    private TextView exerciseText;
    private ImageView restImg;
    private ImageView sleepImg;
    private ImageView exerciseImg;
    private LinearLayout restBackground;
    private LinearLayout sleepBackground;
    private LinearLayout exerciseBackground;


    // TEST
    private FrameLayout testButton;
    private int ecgCntTest = 0;
    private Boolean ecgCheckTest = false;
    //endregion

    PeakController peakCtrl = new PeakController();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        // init And setup
        initializeMemberVariables();
        setupSharedPreferences();
        initializeVariables();

        setupBluetooth();
        initializeUIComponents();
        permissionCheck();
        initializeServicesAndUtilities();

        startContinuousTimeUpdates(); // current date And time update, localData Save func
        loadUserProfile(); // Server Task : getProfile
        chartInit();
        timeZone();
        notiCheck();

        setupBackPressHandler();
        setViewModel();


//        setupTestButton(); // TEST

        return view;
    }

    private void setupTestButton() {
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( ecgCntTest == 3) {
                    ecgChange(ecgCheckTest);
                    ecgCntTest = 0;
                }
                ecgCntTest++;
                ecgCheckTest = !ecgCheckTest;
                hourlyArrEvent(20);
            }
        });

    }

    private void initializeMemberVariables() {
        activity = getActivity();
    }

    private void setupSharedPreferences() {
        SharedPreferences emailSharedPreferences = getActivityOrThrow().getSharedPreferences("User", Context.MODE_PRIVATE);
        email = emailSharedPreferences.getString("email", "null");

        userDetailsSharedPref = getActivityOrThrow().getSharedPreferences(email, Context.MODE_PRIVATE);
        userDetailsEditor = userDetailsSharedPref.edit();

        guardianCheck = userDetailsSharedPref.getBoolean("guardian", false);
    }

    private void setupBluetooth() {
        useBleFlag = false;

        // BLE 상태 요소
        stateFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        stateFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);            // 연결 확인
        stateFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);         // 연결 끊김 확인

        stateFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        stateFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        stateFilter.addAction(BluetoothDevice.ACTION_FOUND);                    // 기기 검색됨
        stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);       // 기기 검색 시작
        stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);      // 기기 검색 종료
        stateFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);

        // BLE 상태 변화 탐지
        getActivityOrThrow().registerReceiver(mBluetoothStateReceiver, stateFilter);

        // BLE Service 제어, 관리
        btManager = (BluetoothManager) getActivityOrThrow().getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
    }

    private void initializeUIComponents() {
        bpm_value = view.findViewById(R.id.bpm_Value);

        bpm_maxValue = view.findViewById(R.id.bpm_maxValue);
        bpm_diffMaxValue = view.findViewById(R.id.diffMaxBPM);

        bpm_avgValue = view.findViewById(R.id.bpm_avgValue);
        bpm_diffAvgValue = view.findViewById(R.id.diffAvg);

        bpm_minValue = view.findViewById(R.id.bpm_minValue);
        bpm_diffMinValue = view.findViewById(R.id.diffMinBPM);

        hrv_value = view.findViewById(R.id.HRV_Value);
        arr_value = view.findViewById(R.id.arr_value);
        eCal_value = view.findViewById(R.id.eCal_Value);
        step_value = view.findViewById(R.id.step_Value);
        temp_value = view.findViewById(R.id.temp_Value);
        distance_value = view.findViewById(R.id.distance_Value);

        exerciseImg = view.findViewById(R.id.exerciseImg);
        exerciseText = view.findViewById(R.id.exerciseText);
        exerciseBackground = view.findViewById(R.id.exercise);

        restImg = view.findViewById(R.id.restImg);
        restText = view.findViewById(R.id.restText);
        restBackground = view.findViewById(R.id.rest);

        sleepImg = view.findViewById(R.id.sleepImg);
        sleepText = view.findViewById(R.id.sleepText);
        sleepBackground = view.findViewById(R.id.sleep);

        testButton = view.findViewById(R.id.testButton);
    }

    private void initializeServicesAndUtilities() {
//        ecgPacketList = new StringBuilder();
        retrofitServerManager = RetrofitServerManager.getInstance();
        gpsTracker = new GpsTracker(safeGetActivity());
    }

    public void setViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // userData
        viewModel.getGender().observe(getViewLifecycleOwner(), genderValue -> {
            iGender = genderValue.equals ("남자") ? 1 : 2;
        });

        viewModel.getAge().observe(getViewLifecycleOwner(), string -> iAge = Integer.parseInt(string));
        viewModel.getHeight().observe(getViewLifecycleOwner(), string -> disheight = Integer.parseInt(string));
        viewModel.getWeight().observe(getViewLifecycleOwner(), string -> dWeight = Integer.parseInt(string));
        viewModel.getSleep().observe(getViewLifecycleOwner(), string -> sleep = Integer.parseInt(string));
        viewModel.getWakeup().observe(getViewLifecycleOwner(), string -> wakeup = Integer.parseInt(string));
        viewModel.getBpm().observe(getViewLifecycleOwner(), string -> eCalBPM = Integer.parseInt(string));

        // notification
        viewModel.getEmergency().observe(getViewLifecycleOwner(), check -> setHeartAttackNotiFlag = check);
        viewModel.getArr().observe(getViewLifecycleOwner(), check -> setArrNotiFlag = check);
        viewModel.getMyo().observe(getViewLifecycleOwner(), check -> setMyoNotiFlag = check);
        viewModel.getNonContact().observe(getViewLifecycleOwner(), check -> setNonContactNotiFlag = check);
        viewModel.getFastArr().observe(getViewLifecycleOwner(), check -> setTachycardiaNotiFlag = check);
        viewModel.getSlowarr().observe(getViewLifecycleOwner(), check -> setBradycardiaNotiFlag = check);
        viewModel.getIrregular().observe(getViewLifecycleOwner(), check -> setAtrialFibrillationNotiFlag = check);
        viewModel.getHourlyArr().observe(getViewLifecycleOwner(), check -> hourlyArrNotificationFlag = check);
        viewModel.getTotalArr().observe(getViewLifecycleOwner(), check -> totalArrNotificationFlag = check);

    }

    public void setupBackPressHandler() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Activity activity = getActivity(); // null 가능성이 있는 참조
                if (activity != null) { // 액티비티가 여전히 유효한지 확인
                    new AlertDialog.Builder(activity)
                            .setTitle(getResources().getString(R.string.noti))
                            .setMessage(getResources().getString(R.string.exit))
                            .setNegativeButton(getResources().getString(R.string.rejectLogout), null)
                            .setPositiveButton(getResources().getString(R.string.exit2), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    activity.finish(); // 액티비티 종료
                                }
                            })
                            .create()
                            .show();
                }
            }
        };

        if (safeGetActivity() != null) {
            getActivityOrThrow().getOnBackPressedDispatcher().addCallback(this, callback);
        }
    }

    public void startForegroundService() {
        getActivityOrThrow().startService(serviceIntent);
    }

    public void stopService() {
        getActivityOrThrow().stopService(serviceIntent);
    }

    private void localDataSave() {
        // 1초 마다 내부에 저장

        // 현재 시간
        long now = System.currentTimeMillis();
        Date current_Date = new Date(now);

        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");

        String stringCurrentDate = date.format(current_Date);

        pre_date_pause = userDetailsSharedPref.getString("preDate", "2023-01-01");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate checkCurrentDate = LocalDate.parse(stringCurrentDate, formatter);
        LocalDate checkPreDate = LocalDate.parse(pre_date_pause, formatter);

        // 날짜 비교
        if (checkCurrentDate.isEqual(checkPreDate)) {
            // 날짜가 같음 (기존 데이터 유지)
            userDetailsEditor.putString("distance", String.valueOf(distance));
            userDetailsEditor.putString("cal", String.valueOf(dCal));
            userDetailsEditor.putString("execal", String.valueOf(dExeCal));
            userDetailsEditor.putString("allstep", String.valueOf(allstep));
            userDetailsEditor.putString("arr", String.valueOf(arrCnt));

            userDetailsEditor.putString("currentDate", currentDate);
            userDetailsEditor.putString("hour", currentHour);

            // 시간대 별 저장 데이터(1시간)
            if (currentMinute.equals("00") && currentSecond.equals("00")){
                // 초기화
                userDetailsEditor.putInt("hourlyArrCnt", arrCnt);
                userDetailsEditor.putInt("hourlyAllStep", allstep);
                userDetailsEditor.putFloat("hourlyDistance", (float) (distance / 100));
                userDetailsEditor.putFloat("hourlyTotalCal", (float) dCal);
                userDetailsEditor.putFloat("hourlyExeCal", (float) dExeCal);
                userDetailsEditor.putString("preHour", currentHour);
                userDetailsEditor.apply();

                hourlyArrCnt = userDetailsSharedPref.getInt("hourlyArrCnt", 0);
                hourlyAllstep = userDetailsSharedPref.getInt("hourlyAllStep", 0);
                hourlyDistance = userDetailsSharedPref.getFloat("hourlyDistance", 0);
                hourlyTotalCal = userDetailsSharedPref.getFloat("hourlyTotalCal", 0);
                hourlyExeCal = userDetailsSharedPref.getFloat("hourlyExeCal", 0);
            }

        } else {
            // 날짜가 다른 경우(초기화)
            userDetailsEditor.putString("distance", "0");
            userDetailsEditor.putString("cal", "0");
            userDetailsEditor.putString("execal", "0");
            userDetailsEditor.putString("allstep", "0");
            userDetailsEditor.putString("arr", "0");

            // 시간대 별 저장 데이터(1시간)
            userDetailsEditor.putInt("hourlyArrCnt", 0);
            userDetailsEditor.putInt("hourlyAllStep", 0);
            userDetailsEditor.putFloat("hourlyDistance", 0);
            userDetailsEditor.putFloat("hourlyTotalCal", 0);
            userDetailsEditor.putFloat("hourlyExeCal", 0);

            // date And time
            userDetailsEditor.putString("preDate", currentDate);
            userDetailsEditor.putString("preHour", currentHour);
            userDetailsEditor.putString("currentDate", currentDate);

            userDetailsEditor.apply();

            hourlyArrCnt = userDetailsSharedPref.getInt("hourlyArrCnt", 0);
            hourlyAllstep = userDetailsSharedPref.getInt("hourlyAllStep", 0);
            hourlyDistance = userDetailsSharedPref.getFloat("hourlyDistance", 0);
            hourlyTotalCal = userDetailsSharedPref.getFloat("hourlyTotalCal", 0);
            hourlyExeCal = userDetailsSharedPref.getFloat("hourlyExeCal", 0);

            distance = 0;
            dCal = 0;
            dExeCal = 0;
            allstep = 0;
            arrCnt = 0;
        }

        userDetailsEditor.apply();
    }

    // BPM 데이터 저장
    public void saveBpmDataAsCsv() {

        if (tenSecondAvgBPM == 0) {
            tenSecondAvgBPM = 70;
        }

        try {
            File directory = getFileDirectory("LOOKHEART/" + email + "/" + currentYear + "/" + currentMonth + "/" + currentDay);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 소수점 제거
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            tenSecondAvgTemp = Double.parseDouble(decimalFormat.format(tenSecondAvgTemp));

            File file = new File(directory, "BpmData.csv");

            // 시간, bpm, temp, hrv
            FileOutputStream fos = new FileOutputStream(file, true); // 'true' to append
            String csvData = currentTime + "," + utcOffsetAndCountry + "," + tenSecondAvgBPM + "," + tenSecondAvgTemp + "," + tenSecondAvgHRV + "\n";
            fos.write(csvData.getBytes());
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 시간 별 데이터 저장( 걸음수, 걸음거리, 활동 칼로리, 전체 칼로리, 부정맥 횟수)
    public void saveCalAndDistanceAsCsv() {
        try {

            // 현재 hour 값(정수)
            int intCurrentHour = Integer.parseInt(currentHour);
            // 이전 hour 값(정수)
            int intPreHour;

            // 경로
            File directory = getFileDirectory("LOOKHEART/" + email + "/" + currentYear + "/" + currentMonth + "/" + currentDay);

            // 디렉토리가 없는 경우 생성
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 파일 경로와 이름
            File file = new File(directory, "CalAndDistanceData.csv");

            // array : 시간, 걸음, 거리, 총칼로리, 활동칼로리, 비정상 맥박 횟수
            if (file.exists()) {
                // 파일이 있는 경우

                // 기존 데이터를 저장하는 변수와 배열
                String line = "";
                List<String> lines = new ArrayList<>();
                // 파일 읽기
                BufferedReader reader = new BufferedReader(new FileReader(file));

                // 기존 데이터 저장
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }

                reader.close();

                String lastLine = lines.get(lines.size() - 1); // 마지막 행
                String[] values = lastLine.split(","); // 행 구분

                // 정수로 변환
                intPreHour = Integer.parseInt(values[0]);

                if (intPreHour == intCurrentHour) {
                    // 같은 시간인 경우 ( 덮어쓰기 )
                    // 파일 쓰기
                    FileOutputStream fos = new FileOutputStream(file, false);

                    int intDailyTotalCal = (int) (dCal - Math.round(hourlyTotalCal*10) / 10.0);
                    int intDailyExeCal = (int) (dExeCal - Math.round(hourlyExeCal*10) / 10.0);
//                    int intDailyExeCal = (int) dCal;
                    int intdailyDistanceM = (int) Math.round((distance / 100) - hourlyDistance);

                    values[0] = String.valueOf(intCurrentHour); // 시간
                    values[1] = utcOffsetAndCountry; // utc Offset / 국가 코드
                    values[2] = String.valueOf(allstep - hourlyAllstep); // 걸음
                    values[3] = String.valueOf(intdailyDistanceM); // 거리
                    values[4] = String.valueOf(intDailyTotalCal); // 총 칼로리
                    values[5] = String.valueOf(intDailyExeCal); // 활동 칼로리
                    values[6] = String.valueOf(arrCnt - hourlyArrCnt); // 비정상맥박 횟수

                    sendHourlyStep = values[2];
                    sendHourlyDistance = values[3];
                    sendHourlyTCal = values[4];
                    sendHourlyECal = values[5];
                    sendHourlyArrCnt = values[6];

                    currentArrCnt = Integer.parseInt(sendHourlyArrCnt);
                    if(     shouldNotify(previousArrCnt, currentArrCnt, 10) ||
                            shouldNotify(previousArrCnt, currentArrCnt, 20) ||
                            shouldNotify(previousArrCnt, currentArrCnt, 30) ||
                            shouldNotify(previousArrCnt, currentArrCnt, 50) ){

                        runOnActivityUiThread(() -> hourlyArrEvent(currentArrCnt));
                    }
                    previousArrCnt = currentArrCnt; // 현재 값을 이전 값으로 업데이트

                    lines.set(lines.size() - 1, String.join(",", values)); // 저장

                    // 파일에 다시 쓰기
                    for (String writeLine : lines) {
                        fos.write((writeLine + "\n").getBytes());
                    }

                    fos.close();

                } else {
                    // 파일 쓰기 ( 줄바꿈 )
                    FileOutputStream fos = new FileOutputStream(file, true);

                    int preHour = Integer.parseInt(values[0]) + 1;

                    // 비어있는 시간(행) 채우기
                    // 이전 시간 값(+1)부터 채우기
                    for (int i = preHour; intCurrentHour >= i; i++) {
                        String csvData = i + "," + utcOffsetAndCountry + "," + "0" + "," + "0" + "," + "0" + "," + "0" + "," + "0" + "\n";
                        fos.write(csvData.getBytes());
                    }

                    fos.close();

                }
            } else {
                // 파일이 없는 경우 ( 이전 시간 데이터 값 채우기 )
                FileOutputStream fos = new FileOutputStream(file, false);

                for (int i = 0; intCurrentHour >= i; i++) {
                    String csvData = i + "," + utcOffsetAndCountry + "," + "0" + "," + "0" + "," + "0" + "," + "0" + "," + "0" + "\n";
                    fos.write(csvData.getBytes());
                }

                fos.close();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveArrEcgDataAsCsv() {
        String filename = "";

        try {

            File directory = getFileDirectory("LOOKHEART/" + email + "/" + currentYear + "/" + currentMonth + "/" + currentDay + "/" + "arrEcgData");

            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, "arrEcgData_" + arrCnt + ".csv");

            // 앞 뒤 "[" "]" 제거
            String strArrEcgData = Arrays.toString(dRecvLast);
            strArrEcgData = strArrEcgData.replace("[", "").replace("]", "");

            // arr Fragment update
            viewModel.addArrList(currentTime);

//            Log.d("strEcgData", strArrEcgData);
            FileOutputStream fos = new FileOutputStream(file, true); // 'true' to append
            String csvData = currentTime + "," + utcOffsetAndCountry + "," + bodyStatus + "," + arrStatus + "," + strArrEcgData + "\n";
            fos.write(csvData.getBytes());
            fos.close();

            // serverTask
            sendArrData(currentDate, currentTime, String.valueOf(arrCnt), csvData, arrStatus);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void arrAction(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                saveArrEcgDataAsCsv();
            }
        }).start();
    }

//    public void ecgAction(int bpm, StringBuilder ecgList) {
//        sendEcgData(bpm, ecgList);
//        ecgPacketList.clear();
//        ecgPacketCnt = 0;
//    }
    public void ecgAction(int bpm, StringBuilder ecgList) {
        sendEcgData(bpm, ecgList);
        ecgPacketList = new StringBuilder();
        leng = 0;
        ecgPacketCnt = 0;
    }

    public void initializeVariables() {

        // init
        lVName[0] = "ECG";
        lVName[1] = "BPM"; // lV[1];
        lVName[2] = "TEMP";
        lVName[3] = "STEP";
        lVName[4] = "HRV";
        lVName[5] = "Arr";

        stringRealBPM = "70";
        stringRealHRV = "0";
        intRealBPM = 70;
        minBPM = 70;
        maxBPM = 70;

        // csv 파일 저장 bpm
        tenSecondAvgBPM = 70;
        tenSecondAvgBpmCnt = 0;
        tenSecondBpmSum = 0;

        // csv 파일 저장 HRV
        tenSecondAvgHRV = 0;
        tenSecondHRVSum = 0;

        // csv 파일 저장 temp
        tenSecondAvgTemp = 0;
        tenSecondTempSum = 0;

        // ui 표현 bpm
        tenMinuteAvgBPM = 70;
        tenMinuteAvgBpmCnt = 0;
        tenMinuteBpmSum = 0;

        intHRV = 0;

        Arrays.fill(bluetoothData, 0);

        // 현재 시간
        long now = System.currentTimeMillis();
        Date current_Date = new Date(now);

        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat hour = new SimpleDateFormat("HH");

        String stringCurrentDate = date.format(current_Date);
        String stringCurrentHour = hour.format(current_Date);

        // 이전 시간
        pre_date_pause = userDetailsSharedPref.getString("preDate", "2023-01-01");
        pre_time_Hour = userDetailsSharedPref.getString("preHour", "0");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate currentDate = LocalDate.parse(stringCurrentDate, formatter);
        LocalDate preDate = LocalDate.parse(pre_date_pause, formatter);

        Log.i("currentDate", "currentDate : " + currentDate);
        Log.i("preDate", "preDate : " + preDate);

        // 날짜 비교
        if (currentDate.isEqual(preDate)) {
            Log.i("DateCompare", "-------------- same --------------");
            Log.i("DateCompare", "CurrentDate : " + currentDate + " PreDate : " + preDate);

            // 날짜가 같은 경우(기존 데이터 유지)
            String sdistance = userDetailsSharedPref.getString("distance", "0");
            String sdCal = userDetailsSharedPref.getString("cal", "0");
            String sdExeCal = userDetailsSharedPref.getString("execal", "0");
            String sallstep = userDetailsSharedPref.getString("allstep", "0");
            String sarrCnt = userDetailsSharedPref.getString("arr", "0");

            distance = Double.parseDouble(sdistance);
            dCal = Double.parseDouble(sdCal);
            dExeCal = Double.parseDouble(sdExeCal);
            allstep = Integer.parseInt(sallstep);
            arrCnt = Integer.parseInt(sarrCnt);

            hourlyArrCnt = userDetailsSharedPref.getInt("hourlyArrCnt", 0);
            hourlyAllstep = userDetailsSharedPref.getInt("hourlyAllStep", 0);
            hourlyDistance = userDetailsSharedPref.getFloat("hourlyDistance", 0);
            hourlyTotalCal = userDetailsSharedPref.getFloat("hourlyTotalCal", 0);
            hourlyExeCal = userDetailsSharedPref.getFloat("hourlyExeCal", 0);

            if(!stringCurrentHour.equals(pre_time_Hour)){
                Log.i("DateCompare", "-------------- different --------------");
                Log.i("HourCompare", "CurrentHour : " + stringCurrentHour + " PreHour : " + pre_time_Hour);

                // hour 변경 시 기존 데이터 저장 및 초기화
                userDetailsEditor.putInt("hourlyArrCnt", arrCnt);
                userDetailsEditor.putInt("hourlyAllStep", allstep);
                userDetailsEditor.putFloat("hourlyDistance", (float) (distance / 100));
                userDetailsEditor.putFloat("hourlyTotalCal", (float) dCal);
                userDetailsEditor.putFloat("hourlyExeCal", (float) dExeCal);

                userDetailsEditor.commit();

                hourlyArrCnt = userDetailsSharedPref.getInt("hourlyArrCnt", 0);
                hourlyAllstep = userDetailsSharedPref.getInt("hourlyAllStep", 0);
                hourlyDistance = userDetailsSharedPref.getFloat("hourlyDistance", 0);
                hourlyTotalCal = userDetailsSharedPref.getFloat("hourlyTotalCal", 0);
                hourlyExeCal = userDetailsSharedPref.getFloat("hourlyExeCal", 0);
            }

        } else {
            Log.i("DateCompare", "different -> CurrentDate : " + currentDate + " PreDate : " + preDate);

            // 날짜가 다른 경우(초기화)
            userDetailsEditor.putString("distance", "0");
            userDetailsEditor.putString("cal", "0");
            userDetailsEditor.putString("execal", "0");
            userDetailsEditor.putString("allstep", "0");
            userDetailsEditor.putString("arr", "0");

            userDetailsEditor.putInt("hourlyArrCnt", 0);
            userDetailsEditor.putInt("hourlyAllStep", 0);
            userDetailsEditor.putFloat("hourlyDistance", 0);
            userDetailsEditor.putFloat("hourlyTotalCal", 0);
            userDetailsEditor.putFloat("hourlyExeCal", 0);

            userDetailsEditor.apply();

            // daily Data
            distance = 0;
            dCal = 0;
            dExeCal = 0;
            allstep = 0;
            arrCnt = 0;

            // hourly Data
            hourlyArrCnt = 0;
            hourlyAllstep = 0;
            hourlyDistance = 0;
            hourlyTotalCal = 0;
            hourlyExeCal = 0;
        }

        Log.i("LocalData", "-------------- LocalData --------------");
        Log.i("LocalData", "allstep : " + allstep );
        Log.i("LocalData", "distance : " + distance );
        Log.i("LocalData", "dCal : " + dCal );
        Log.i("LocalData", "dExeCal : " + dExeCal );
        Log.i("LocalData", "arrCnt : " + arrCnt );

        Log.i("HourlyData", "-------------- HourlyData --------------");
        Log.i("HourlyData", "hourlyAllstep : " + hourlyAllstep );
        Log.i("HourlyData", "hourlyDistance : " + hourlyDistance );
        Log.i("HourlyData", "hourlyTotalCal : " + hourlyTotalCal );
        Log.i("HourlyData", "hourlyExeCal : " + hourlyExeCal );
        Log.i("HourlyData", "hourlyArrCnt : " + hourlyArrCnt );

        // 시간 저장
        userDetailsEditor.putString("preDate", stringCurrentDate);
        userDetailsEditor.putString("preHour", stringCurrentHour);
        userDetailsEditor.apply();
    }

    // 데이터 처리
    int leng = 0;
    public void doBufProcess(byte[] buf) {
        try {
            if (buf.length >= 19) {

                if (buf[0] == 1) {

                    // battery = buf[1]
                    bluetoothData[0] = (int) buf[1] & 0xFF;
                    doubleBAT = bluetoothData[0];

                    // bpm = buf[2]
                    bluetoothData[1] = (int) buf[2] & 0xFF;

                    // temp = buf[3]
                    bluetoothData[2] = (int) buf[3] & 0xFF;
                    bluetoothData[2] = (bluetoothData[2] + 186) / 10.0;
                    bluetoothData[2] = bluetoothData[2] + 0.5;

                    // ecg data = buf[6] ~ buf[19]
                    for (int i = 6; i <= 19; i++) {

                        int ecgP = buf[i] & 0xFF;
                        ecgP = ecgP * 4;

                        // graph And Arr
                        if (peakCtrl.getEcgToPeakDataFlag()) // peak
                            dRecv[iRecvCnt] = peakCtrl.changeEcgData((double) ecgP);
                        else    // ecg
                            dRecv[iRecvCnt] = ecgP;

                        iRecvCnt++;

                        dRecvLast[iRecvLastCnt] = ecgP;
                        iRecvLastCnt++;

                        int xmax = 500;
                        if (iRecvLastCnt == xmax) {
                            for (int j = 1; j < xmax; j++) {
                                dRecvLast[j - 1] = dRecvLast[j];
                            }
                            iRecvLastCnt = xmax - 1;
                        }

                        // ecg
                        int exmax = 14;

                        if (ecgCnt == exmax) {
                            for (int j = 1; j < exmax; j++) {
                                ecgPacket[j - 1] = ecgPacket[j];
                            }
                            ecgCnt = exmax - 1;
                        }

                        ecgPacket[ecgCnt] = (double) ecgP;
                        ecgCnt++;

                    } // for

                    ecgPacketList.append(Arrays.toString(ecgPacket));
                    ecgPacketCnt++;

                    // step = buf[4]
                    int step = (int) buf[4] & 0xFF;

                    if (step >= 14)
                        step -= 14;

                    if (step >= 10) {
                        String notificationString = null;
                        String arrTypeString = null;

                        if ((step / 10) == EVENT_ARR) {
                            arr = EVENT_ARR;
                            arrCnt++;
                            tenSecondArrCnt++;
                            arrStatus = ARR;

                            arrAction();

                            if (setArrNotiFlag)
                                arrTypeString = ARR;
                        }
                        if ((step / 10) == EVENT_HEARTATTACK) {
                            if (setHeartAttackNotiFlag && notificationManager.getNotiPermissionCheck())
                                runOnActivityUiThread(this::heartAttackEvent);
                        }
                        if ((step / 10) == EVENT_NON_CONTACT) {
                            if (nonContactCheck && setNonContactNotiFlag) {
                                notificationString = NON_CONTACT;
                                nonContactCheck = false; // 10초
                            }
                        }

                        if ((step / 10) == EVENT_MYO) {
                            if (myoCheck && setMyoNotiFlag) {
                                notificationString = MYO;
                                myoCheck = false; // 10초
                            }
                        }

                        if ((step / 10) == EVENT_FAST_ARR) {
                            arr = EVENT_ARR;
                            arrCnt++;
                            tenSecondArrCnt++;
                            arrStatus = STATUS_TACHYCARDIA;

                            arrAction();


                            if (setTachycardiaNotiFlag)
                                arrTypeString = TACHYCARDIA;

                        }
                        if ((step / 10) == EVENT_SLOW_ARR) {

                            if (bluetoothData[1] >= 3) { // bpm >= 3
                                arr = EVENT_ARR;
                                arrCnt++;
                                tenSecondArrCnt++;
                                arrStatus = STATUS_BRADYCARDIA;

                                arrAction();

                                if (setBradycardiaNotiFlag)
                                    arrTypeString = BRADYCARDIA;

                            }
                        }
                        if ((step / 10) == EVENT_IRREGULAR_ARR) {
                            arr = EVENT_ARR;
                            arrCnt++;
                            tenSecondArrCnt++;
                            arrStatus = STATUS_IRREGULAR;

                            arrAction();

                            if (setAtrialFibrillationNotiFlag)
                                arrTypeString = ATRIAL_FIBRILLATION;

                        }

                        if (notificationManager.getNotiPermissionCheck() && notificationString != null) {
                            notificationManager.sendNotification(notificationString, NOTIFICATION_ID, currentTime);
                        } else if (notificationManager.getNotiPermissionCheck() && arrTypeString != null) {
                            switch (arrCnt) {
                                case 50:
                                    if (totalArrNotificationFlag)
                                        notificationManager.sendArrNotification(ARR_50, ARR_NOTIFICATION_ID, currentTime);
                                    break;
                                case 100:
                                    if (totalArrNotificationFlag)
                                        notificationManager.sendArrNotification(ARR_100, ARR_NOTIFICATION_ID, currentTime);
                                    break;
                                case 200:
                                    if (totalArrNotificationFlag)
                                        notificationManager.sendArrNotification(ARR_200, ARR_NOTIFICATION_ID, currentTime);
                                    break;
                                case 300:
                                    if (totalArrNotificationFlag)
                                        notificationManager.sendArrNotification(ARR_300, ARR_NOTIFICATION_ID, currentTime);
                                    break;
                                default:
                                    if (setArrNotiFlag)
                                        notificationManager.sendArrNotification(arrTypeString, ARR_NOTIFICATION_ID, currentTime);
                                    break;
                            }
                        } // notifications

                    } else {
                        arr = 0;
                    }

                    step = step % 10;

                    // step = buf[4]
                    bluetoothData[3] += step;
                    nowstep += step;
                    allstep += step;
                    tenSecondStep += step;

                    bluetoothData[4] = (int) buf[5] & 0xFF;
                    bluetoothData[4] = bluetoothData[4] * 10.0;

                }

                dpscnt += 1;

                if (intBPM > 3) {
                    pBPM = cBPM;
                    cBPM = intBPM;
                    if (pBPM != cBPM) {
                        realBPM = cBPM;
                    }
                }

                doubleBAT = bluetoothData[0];
                intBPM = (int) bluetoothData[1];
                doubleTEMP = bluetoothData[2];
                intHRV = (int) bluetoothData[4];

                stringHRV = Integer.toString(intHRV);
                stringBPM = Integer.toString(intBPM);
                stringArr = String.valueOf(arr);
                stringArrCnt = String.valueOf(arrCnt);

                if (intBPM > 3) {
                    stringRealHRV = stringHRV;
                    stringRealBPM = stringBPM;
                    intRealBPM = intBPM;

                    // avgBPM
                    tenSecondBpmSum += intRealBPM;
                    tenMinuteBpmSum += intRealBPM;

                    tenSecondAvgBpmCnt++;
                    tenMinuteAvgBpmCnt++;

                    tenSecondAvgBPM = tenSecondBpmSum / tenSecondAvgBpmCnt; // csv 파일 저장 변수
                    tenMinuteAvgBPM = tenMinuteBpmSum / tenMinuteAvgBpmCnt; // home 화면에 보여주는 변수

                    // avgHRV
                    tenSecondHRVSum += intHRV;
                    tenSecondAvgHRV = tenSecondHRVSum / tenSecondAvgBpmCnt;

                    // avgTemp
                    tenSecondTempSum += doubleTEMP;
                    tenSecondAvgTemp = tenSecondTempSum / tenSecondAvgBpmCnt;

                    // maxBPM
                    if (intRealBPM > maxBPM) {
                        maxBPM = intRealBPM;
                    }
                    // minBPM
                    if (intRealBPM < minBPM) {
                        minBPM = intRealBPM;
                    }

                    // 평균 BPM 차이
                    if (intRealBPM - tenMinuteAvgBPM > 0) {
                        diffAvgFlag = true;
                        diffAvgBPM = intRealBPM - tenMinuteAvgBPM;
                    } else {
                        diffAvgFlag = false;
                        diffAvgBPM = intRealBPM - tenMinuteAvgBPM;
                    }
                    // 최대 BPM 차이
                    if (intRealBPM - maxBPM > 0) {
                        diffMaxFlag = true;
                        diffMaxBPM = intRealBPM - maxBPM;
                    } else {
                        diffMaxFlag = false;
                        diffMaxBPM = intRealBPM - maxBPM;
                    }
                    // 최소 BPM 차이
                    if (intRealBPM - minBPM > 0) {
                        diffMinFlag = true;
                        diffMinBPM = intRealBPM - minBPM;
                    } else {
                        diffMinFlag = false;
                        diffMinBPM = intRealBPM - minBPM;
                    }
                } // if

                // sendECG
                if (ecgPacketCnt == 10){
                    ecgAction(intRealBPM, ecgPacketList);
                }

                handler2.post(this::addEntry);

            } // if
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addEntry() {
        try {
            LineData data = chart.getData();

            for (int k = 0; k < 1; k++) {
                LineDataSet ds = (LineDataSet) data.getDataSetByIndex(k);

                for (int j = 0; j < iRecvCnt - 1; j++) {
                    float ff = (float) dRecv[j];

                    data.addEntry(new Entry(ds.getEntryCount(), ff), k);

                    while (ds.getEntryCount() > GRAPH_MAX) {
                        ds.removeEntry(0);

                        for (int i = 0; i < ds.getEntryCount(); i++) {
                            Entry e = ds.getEntryForIndex(i);
                            if (e == null) continue;

                            e.setX(e.getX() - 1);
                        }
                    }
                }
                iRecvCnt = 0;
            }

            chart.invalidate();

        } catch (Exception ignored) {
        }
    }

    public void doLoop() {
        Log.v("doLoop", "doLoop() Start");

        if (thread == null || !thread.isAlive()) {
            thread = new Thread(new Runnable() {

                long lTick = System.currentTimeMillis();
                long lTick500ms = System.currentTimeMillis();
                long lTick1s = System.currentTimeMillis();
                long lTick10s = System.currentTimeMillis();
                long lTick10m = System.currentTimeMillis();

                @SuppressLint("MissingPermission")
                @Override
                public void run() {
                    isRun = true;

                    while ((isRun)) {
                        long lNow = System.currentTimeMillis();

                        if (lNow - lTick >= 1000) {
                            lTick = lNow;

                            dps = dpscnt;
                            dpscnt = 0;

                            if (realBPM > 50) {
                                iCalTimer++;
                            } else {
                                iCalTimer = 0;
                            }
                            if (iCalTimer >= 2) {
                                iCalTimeCount++;

                                dCalMinU = iCalTimeCount / 60.0;
                                iCalTimeCount = 0;

                                if (iGender == 1) { // male
                                    dCal = dCal + ((iAge * 0.2017) + (dWeight * 0.1988) + (realBPM * 0.6309) - 55.0969) * dCalMinU / 4.184;
                                    tenSecondCal = tenSecondCal + ((iAge * 0.2017) + (dWeight * 0.1988) + (realBPM * 0.6309) - 55.0969) * dCalMinU / 4.184;
                                    if (realBPM > eCalBPM) {
                                        dExeCal = dExeCal + ((iAge * 0.2017) + (dWeight * 0.1988) + (realBPM * 0.6309) - 55.0969) * dCalMinU / 4.184;
                                        tenSecondECal = tenSecondECal + ((iAge * 0.2017) + (dWeight * 0.1988) + (realBPM * 0.6309) - 55.0969) * dCalMinU / 4.184;
                                    }
                                }
                                else { // female
                                    dCal = dCal + ((iAge * 0.074) + (dWeight * 0.1263) + (realBPM * 0.4472) - 20.4022) * dCalMinU / 4.184;
                                    tenSecondCal = tenSecondCal + ((iAge * 0.2017) + (dWeight * 0.1988) + (realBPM * 0.6309) - 55.0969) * dCalMinU / 4.184;
                                    if (realBPM > eCalBPM) {
                                        dExeCal = dExeCal + ((iAge * 0.074) + (dWeight * 0.1263) + (realBPM * 0.4472) - 20.4022) * dCalMinU / 4.184;
                                        tenSecondECal = tenSecondECal + ((iAge * 0.2017) + (dWeight * 0.1988) + (realBPM * 0.6309) - 55.0969) * dCalMinU / 4.184;
                                    }
                                }

                                dCal = Math.round(dCal * 10) / 10.0;
                                dExeCal = Math.round(dExeCal * 10) / 10.0;

                                tenSecondCal = Math.round(tenSecondCal * 10) / 10.0;
                                tenSecondECal = Math.round(tenSecondECal * 10) / 10.0;
                            }

                            // calc cal
                            double dh = disheight;
                            avgsize = ((dh * 0.37) + (dh - 100)) / 2.0;   //  dh 사용자의 키
                            if (avgsize < 0) {
                                avgsize = 10;
                            }

                            if (realBPM < eCalBPM) {
                                distance = distance + (avgsize * nowstep);   // nowstep는 걸음수  계산 값의 단위는 cm
                                tenSecondDistance = tenSecondDistance + (avgsize * nowstep);
                            } else if ((realBPM >= eCalBPM) && (realBPM < eCalBPM + 20)) {
                                distance = distance + ((avgsize + 1) * nowstep);
                                tenSecondDistance = tenSecondDistance + ((avgsize + 1) * nowstep);
                            } else if ((realBPM >= eCalBPM + 20) && (realBPM < eCalBPM + 40)) {
                                distance = distance + ((avgsize + 2) * nowstep);
                                tenSecondDistance = tenSecondDistance + ((avgsize + 2) * nowstep);
                            } else if ((realBPM >= eCalBPM + 40) && (realBPM < 250)) {
                                distance = distance + ((avgsize + 3) * nowstep);
                                tenSecondDistance = tenSecondDistance + ((avgsize + 3) * nowstep);
                            }

                            //운동중
                            if (realBPM < eCalBPM)     //BPMchk는 맥박
                            {
                                wdistance = wdistance + (avgsize * nowstep);   // nowstep는 걸음수  계산 값의 단위는 cm
                            } else if ((realBPM >= eCalBPM) && (realBPM < eCalBPM + 15)) {
                                wdistance = wdistance + ((avgsize + 1) * nowstep);
                            } else if ((realBPM >= eCalBPM + 15) && (realBPM < eCalBPM + 30)) {
                                wdistance = wdistance + ((avgsize + 3) * nowstep);
                            } else if ((realBPM >= eCalBPM + 30) && (realBPM < 250)) {
                                wdistance = wdistance + ((avgsize + 4) * nowstep);
                            }

                            distance = Math.round(distance * 1000) / 1000.0;
                            wdistance = Math.round(wdistance * 1000) / 1000.0;

                            tenSecondDistance = Math.round(tenSecondDistance * 1000) / 1000.0;
                            tenSecondDistanceKM = tenSecondDistance / 100 / 1000;
                            tenSecondDistanceM = tenSecondDistanceKM * 1000;

                            realDistanceKM = distance / 100 / 1000;
                            realDistanceM = realDistanceKM * 1000;

                            nowstep = 0;

                        }

                        // 그래프
//                        if (iRecvCnt > 100 || tickTime() > 200) {
//                            handler2.post(() -> addEntry());
//                        }

                        if (lNow - lTick500ms >= 500) { // 0.5
                            handler2.post(() -> uiTick());
                            lTick500ms = lNow;
                            tickReset();
                        }

                        if (lNow - lTick1s >= 1000) { // 1초
                            lTick1s = lNow;

                            if(safeGetActivity() != null)
                                runOnActivityUiThread(() -> statusCheck());

                        }

                        List<String> lTxNow = null;

                        synchronized (lTx) {
                            if (lTx.size() > 0) {
                                lTxNow = lTx;
                                lTx = new ArrayList<>();
                            }
                        }

                        if (lTxNow != null) {
                            if (lTxNow.size() > 0) {
                                while (lTxNow.size() > 0) {
                                    String sTx = lTxNow.get(0);

                                    if (BTConnected) {
                                        if (isBTType == 0) {
                                            if (mConnectedThread != null) {
                                                mConnectedThread.write(sTx);
                                            }
                                        } else if (isBTType == 1) {
                                            boolean status = true;
                                            try {
                                                //check mBluetoothGatt is available
                                                if (bluetoothGatt == null) {
                                                    Log.v("BLE", "lost connection");
                                                    status = false;
                                                }
                                                BluetoothGattService Service = bluetoothGatt.getService(UUID_SERVICE);
                                                if (Service == null) {
                                                    Log.v("BLE", "service not found!");
                                                    status = false;
                                                }
                                                BluetoothGattCharacteristic charac = Service
                                                        .getCharacteristic(UUID_TX);
                                                if (charac == null) {
                                                    Log.v("BLE", "char not found!");
                                                    status = false;
                                                }

                                                if (status) {
                                                    charac.setValue(sTx);//, 20, 0);
                                                    status = bluetoothGatt.writeCharacteristic(charac);
                                                    if (status) {
//                                                    safeGetActivity()().runOnUiThread(() -> toast("work"));
                                                    }
                                                }
                                            } catch (Exception ignored) {
                                            }
                                        }
                                    }
                                    lTxNow.remove(0);
                                }
                            }
                        }

                        // 10초
                        if (lNow - lTick10s >= 10000) {
                            lTick10s = lNow; // 타이머 리셋
                            nonContactCheck = true; // 10초에 한 번만 울리게끔
                            myoCheck = true;

                            tenSecondAction();

                            if (safeGetActivity() != null) {
                                runOnActivityUiThread(() -> batReceived((int) doubleBAT));
                            }

                        }

                        // 10분
                        if (lNow - lTick10m >= 600000) {
                            lTick10m = lNow; // 타이머 리셋
                            tenMinuteAction();
                        }

                        try {
                            Thread.sleep(1);
                        } catch (Exception ignored) {

                        }
                    }
                }
            });
            thread.start();
        }
    }

    public void sendCalcDataAsCsv(String hour, String step, String distance, String cal, String eCal, String arrcnt) {

        retrofitServerManager.sendHourlyData(email, currentYear, currentMonth, currentDay, hour, utcOffsetAndCountry, step, distance, cal, eCal, arrcnt, new RetrofitServerManager.ServerTaskCallback() {
            @Override
            public void onSuccess(String result) {
                Log.i("sendCalcDataAsCsv", result);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("sendCalcDataAsCsv", "send Err");
                e.printStackTrace();
            }
        });
    }

    public void sendArrData(String currentDate, String currentTime, String sendArrCnt, String sendArrData, String arrStatus) {

        String arrTime = currentDate + " " + currentTime;

        retrofitServerManager.sendArrData(email, utcOffsetAndCountry, arrTime, sendArrData, arrStatus, new RetrofitServerManager.ServerTaskCallback() {

            @Override
            public void onSuccess(String result) {
                Log.i("sendArrData", result);
                retryCounts.remove(sendArrCnt);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("sendArrData", "send Err");
                e.printStackTrace();

                if (retryCounts.containsKey(sendArrCnt)) {
                    int currentRetryCount = retryCounts.get(sendArrCnt); // 재전송 횟수 체크
                    if (currentRetryCount < MAX_RETRY_COUNT) {
                        retryCounts.put(sendArrCnt, currentRetryCount + 1);
                        retransmitAfterDelay(currentDate, currentTime, sendArrCnt, sendArrData, arrStatus);
                    } else {
                        Log.e("sendArrData", "ARR DATA Server Task Fail (6 times)");
                        retryCounts.remove(sendArrCnt);
                    }
                } else {
                    retryCounts.put(sendArrCnt, 1);
                    retransmitAfterDelay(currentDate, currentTime, sendArrCnt, sendArrData, arrStatus);
                }
            }
        });
    }

    // ARR DATA Server 재전송 함수
    private void retransmitAfterDelay(String currentDate, String currentTime,String sendArrCnt, String sendArrData, String arrStatus) {
        Handler handler = new Handler(Looper.getMainLooper()); // 메인 스레드의 Looper를 사용
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendArrData(currentDate, currentTime, sendArrCnt, sendArrData, arrStatus);
            }
        }, 60000); // 1분 후 재전송
    }

    public void sendEcgData(int bpm, StringBuilder ecgPacketList) {

        String email = userDetailsSharedPref.getString("email", "NULL");
        String writeTime = currentUtcTime();

        retrofitServerManager.sendEcgData(email, writeTime, utcOffsetAndCountry, bpm, ecgPacketList, new RetrofitServerManager.ServerTaskCallback() {

            @Override
            public void onSuccess(String result) {
                Log.i("sendECGData", result);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("sendECGData", " send Err");
                e.printStackTrace();
            }
        });
    }

//    public void sendEcgData(int bpm, StringBuilder ecgPacketList) {
//
//        String email = userDetailsSharedPref.getString("email", "NULL");
//        String writeTime = currentUtcTime();
//
//        retrofitServerManager.sendEcgData(email, writeTime, utcOffsetAndCountry, bpm, ecgPacketList, new RetrofitServerManager.ServerTaskCallback() {
//
//            @Override
//            public void onSuccess(String result) {
//                Log.i("sendECGData", result);
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                Log.e("sendECGData", " send Err");
//                e.printStackTrace();
//            }
//        });
//    }

    private void tenSecondAction() {

        timeZone();

        // csv 파일 저장
        saveBpmDataAsCsv();
        saveCalAndDistanceAsCsv();

        tenSecondServerTask(tenSecondAvgBPM, tenSecondAvgTemp, tenSecondAvgHRV, tenSecondStep, tenSecondDistanceKM, tenSecondCal, tenSecondECal, tenSecondArrCnt);
        sendCalcDataAsCsv(currentHour, sendHourlyStep, sendHourlyDistance, sendHourlyTCal, sendHourlyECal, sendHourlyArrCnt);

        // 변수 초기화
        tenSecondBpmSum = 0;
        tenSecondAvgBPM = 0;
        tenSecondAvgBpmCnt = 0;
        tenSecondAvgHRV = 0;
        tenSecondHRVSum = 0;
        tenSecondAvgTemp = 0;
        tenSecondTempSum = 0;

        tenSecondArrCnt = 0;
        tenSecondCal = 0;
        tenSecondECal = 0;
        tenSecondDistance = 0;
        tenSecondDistanceKM = 0;
        tenSecondDistanceM = 0;
        tenSecondStep = 0;
    }

    private void tenMinuteAction() {
        // 변수 초기화
        tenMinuteBpmSum = 0;
        tenMinuteAvgBPM = 0;
        tenMinuteAvgBpmCnt = 0;
    }

    private void tenSecondServerTask(int bpm, double temp, int hrv, int step, double distance, double tCal, double eCal, int arrCnt){

        // bpm, temp, hrv, step, distance, tCal, eCal, arrCnt
        String email = userDetailsSharedPref.getString("email", "NULL");
        String writeTime = currentUtcTime();

        retrofitServerManager.sendTenSecondData(email, utcOffsetAndCountry, writeTime, bpm, temp, hrv, step, distance, tCal, eCal, arrCnt, new RetrofitServerManager.ServerTaskCallback() {

            @Override
            public void onSuccess(String result) {
                Log.i("tenSecondServerTask", result);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("tenSecondServerTask", "send Err");
                e.printStackTrace();
            }
        });
    }

    public void startContinuousTimeUpdates(){
        localDataLoop = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (localDataLoop) {

                    currentTimeCheck();
                    localDataSave();

                    try {
                        Thread.sleep(1000);  // 1초 대기
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void currentTimeCheck() {

        // 시간 갱신 메서드
        long mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        mTime = new Time(mNow);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat th = new SimpleDateFormat("HH");
        SimpleDateFormat minutes = new SimpleDateFormat("mm");
        SimpleDateFormat second = new SimpleDateFormat("ss");

        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        SimpleDateFormat month = new SimpleDateFormat("MM");
        SimpleDateFormat day = new SimpleDateFormat("dd");

        currentYear = year.format(mDate);
        currentMonth = month.format(mDate);
        currentDay = day.format(mDate);

        currentDate = df.format(mDate);
        currentTime = stf.format(mTime);
        currentHour = th.format(mTime);
        currentMinute = minutes.format(mTime);
        currentSecond = second.format(mTime);

    }

    public void tickReset() {
        tickLast = SystemClock.uptimeMillis();
    }

    public long tickTime() {
        return SystemClock.uptimeMillis() - tickLast;
    }

    public void loadUserProfile() {
        String getEmail = userDetailsSharedPref.getString("email", "NULL");
        try {
            retrofitServerManager.getProfile(getEmail, new RetrofitServerManager.UserDataCallback() {
                @Override
                public void userData(UserProfile user) {

                    iGender = user.getGender().equals("남자") ? 1 : 2;
                    iAge = Integer.parseInt(user.getAge());
                    userName = user.getName();
                    dWeight = Integer.parseInt(user.getWeight());
                    disheight = Integer.parseInt(user.getHeight());
                    sleep = Integer.parseInt(user.getSleepStart());
                    wakeup = Integer.parseInt(user.getSleepEnd());

                    eCalBPM = Integer.parseInt(user.getActivityBPM()); // activity BPM

                    peakCtrl.setEcgToPeakDataFlag(user.getEcgFlag().equals("0"));  // peak(0) : true, ecg(1) : false

                    getArrCnt();
                }

                @Override
                public void onFailure(Exception e) {
                    toast(getResources().getString(R.string.serverErr));
                    e.printStackTrace();

                    iGender = 1;
                    eCalBPM = 90;
                    iAge= 40;
                    userName = "admin";
                    dWeight = 60;
                    disheight = 170;
                    sleep = 23;
                    wakeup = 7;
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("uiProfileLoad", "Error loading profile", e);
        }
    }

    public void chartInit() {
        chart = (LineChart) view.findViewById(R.id.myChart);

        chart.setDrawGridBackground(true);
        chart.setBackgroundColor(Color.WHITE);
        chart.setGridBackgroundColor(Color.WHITE);
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setAutoScaleMinMaxEnabled(true);
        chart.setPinchZoom(false);
        chart.getXAxis().setDrawGridLines(true);
        chart.getXAxis().setDrawAxisLine(false);
        chart.getXAxis().setEnabled(false);//x축 표시에 대한 함수
        chart.getXAxis().setDrawGridLines(false);
        Legend l = chart.getLegend();
        l.setEnabled(true);
        l.setFormSize(10f); // set the size of the legend forms/shapes
        l.setTextSize(12f);
        l.setTextColor(Color.DKGRAY);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setTextColor(getResources().getColor(R.color.colorDKGRAY));
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(getResources().getColor(R.color.colorGainsbro));

        YAxis rightAxis = chart.getAxisRight();
        leftAxis.setAxisMaximum(1024f);
        leftAxis.setAxisMinimum(0f);
        rightAxis.setEnabled(false);

        LineData data = chart.getData();

        if (data == null) {
            data = new LineData();
            chart.setData(data);
        }

        for (int k = 0; k < 1; k++) {
            LineDataSet ds = (LineDataSet) data.getDataSetByIndex(k);

            if (ds == null) {
                ds = createSet(lVName[k]);
                data.addDataSet(ds);
                Log.v("ds", String.valueOf(data));
                if (k == 0) ds.setColor(Color.BLUE);
                else if (k == 1) ds.setColor(Color.RED);
            }
        }
        chart.invalidate();
    }



    private LineDataSet createSet(String title) {

        LineDataSet set = new LineDataSet(null, title);
        Log.v("set", String.valueOf(set));
        set.setLineWidth(1f);
        set.setDrawValues(false);
        set.setValueTextColor(getResources().getColor(R.color.white));
        set.setColor(getResources().getColor(R.color.white));
        set.setMode(LineDataSet.Mode.LINEAR);
        set.setDrawCircles(false);
        set.setHighLightColor(Color.rgb(190, 190, 190));

        return set;
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public void uiTick() {

        try {
            LineData data = chart.getData();
            data.notifyDataChanged();

            if (diffAvgFlag != null) {
                // avg bpm 차이
                if (diffAvgFlag) {
                    bpm_diffAvgValue.setText(String.valueOf("+" + diffAvgBPM));
                } else {
                    bpm_diffAvgValue.setText(String.valueOf(diffAvgBPM));
                }
                // max bpm 차이
                if (diffMaxFlag) {
                    bpm_diffMaxValue.setText(String.valueOf("+" + diffMaxBPM));
                } else {
                    bpm_diffMaxValue.setText(String.valueOf(diffMaxBPM));
                }
                // avg bpm 차이
                if (diffMinFlag) {
                    bpm_diffMinValue.setText(String.valueOf("+" + diffMinBPM));
                } else {
                    bpm_diffMinValue.setText(String.valueOf(diffMinBPM));
                }
            }

            bpm_value.setText(stringRealBPM);
            bpm_avgValue.setText(Integer.toString(tenMinuteAvgBPM));
            bpm_maxValue.setText(Integer.toString(maxBPM));
            bpm_minValue.setText(Integer.toString(minBPM));

            hrv_value.setText(stringRealHRV);
            arr_value.setText(Integer.toString(arrCnt));
            eCal_value.setText((int) dExeCal + " " + getResources().getString(R.string.eCalValue2));
            step_value.setText(allstep + " " + getResources().getString(R.string.stepValue2));
            distance_value.setText((String.format("%.3f", realDistanceKM)) + " " + getResources().getString(R.string.distanceValue2));
            temp_value.setText(String.format("%.1f", bluetoothData[2]) + " " + getResources().getString(R.string.temperatureValue2));

        } catch (Exception E) {
            Log.d("ERR", "" + E.getMessage());
        }
    }



    // -------------------------------- BLE -------------------------------- //
    public void BleConnectCheck() {

        if (btAdapter == null) {
            // 장치가 블루투스를 지원하지 않는 경우.
//            FragmentActivity activity = this;
            toast(getResources().getString(R.string.notSupportBle));
        } else {
            btScanner = btAdapter.getBluetoothLeScanner();

            // 장치가 블루투스를 지원하는 경우
            if (btAdapter.isEnabled()) {
                if (getActivityOrThrow().checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                        getActivityOrThrow().checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    startScanning();
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    public void startScanning() {
        System.out.println("start Scanning");
        devicesDiscovered.clear();

        stopScanning();

        // 블루투스 연결 활성화 시 연결 해제
        try {
            if (bluetoothGatt != null) {
                System.out.println("bluetoothGatt.disconnect");
                bluetoothGatt.disconnect(); // gatt 클라이언트에서 gatt 서버 연결 해제
                bluetoothGatt.close(); // 클라이언트, 리소스 해제
            }
        } catch (Exception ignored) {
        }

        // 비동기 블루투스 스캔 시작
        AsyncTask.execute(() -> btScanner.startScan(leScanCallback));

        // 10초 후 스캔 종료
        if (bleHandler != null) {
            bleHandler.postDelayed(() -> stopScanning(), SCAN_PERIOD);
        }
    }

    @SuppressLint("MissingPermission")
    public void stopScanning() {
        if (btScanner != null)
            AsyncTask.execute(() -> btScanner.stopScan(leScanCallback));
    }

    // 블루투스 스캔
    // BLE 디바이스 스캔의 결과를 처리하는 콜백 메소드를 포함하는 추상 클래스
    private ScanCallback leScanCallback = new ScanCallback() {

        // onScanResult 메소드는 Bluetooth 스캐너가 새로운 BLE 디바이스를 발견할 때마다 호출
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            devicesDiscovered.add(result.getDevice()); // 발견 디바이스 추가

            // 발견 디바이스의 이름을 가지고 비교
            @SuppressLint("MissingPermission") String name = result.getDevice().getName();
            if (name != null) {
                if (name.equals("ECG")) {
                    // 연결 시도
                    if(safeGetActivity() != null && !firstBleConnect) {
                        bluetoothGatt = result.getDevice().connectGatt(getActivityOrThrow().getApplicationContext(), true, btleGattCallback);
                        BluetoothDevice device = bluetoothGatt.getDevice();
                        deviceMacAddress = device.getAddress();
                        firstBleConnect = true;
                        Log.v("macAddress", deviceMacAddress);

                        // 연결 우선순위 설정
                        if (bluetoothGatt != null) {
                            bluetoothGatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
                        }

                        stopScanning(); // 발견 시 스캔 종료

                    }
                }
            }
        }
    };

    // 블루투스 이벤트 콜백 제공 인스턴스
    private final BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {

        // GATT characteristic 변경 시 호출되는 메서드
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {

            try {
                if (BTConnected == false)
                    BTConnected = true;

                // 변경된 값을 버퍼에 저장
                byte[] buf = characteristic.getValue();
                doBufProcess(buf);

            } catch (Exception E) {
                Log.d("BLE", "onCharacteristicChanged: " + E.getMessage());
            }
        }

        // GATT 연결 상태가 변경될 때 호출
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.v("BLE_STATE", "STATE_CONNECTED");

                mConnectionState = STATE_CONNECTED;
                bluetoothGatt.discoverServices();

                stopScanning();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.v("BLE_STATE", "STATE_DISCONNECTED");

                mConnectionState = STATE_DISCONNECTED;

                try {
                    synchronized (lTx) {
                        lTx.clear();
                    }
//                    bleHandler.postDelayed(() -> startScanning(), 1000);

                } catch (Exception e) {
                    Log.e("onConnectionStateChange", "synchronized Err");
                    e.printStackTrace();
                }
            }
        }

        // GATT 서비스가 발견될 때 호출
        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

                try {
                    mBluetoothGattService = gatt.getService(UUID_SERVICE);

                    BluetoothGattCharacteristic receiveCharacteristic =
                            mBluetoothGattService.getCharacteristic(UUID_RECIEVE);

                    if (receiveCharacteristic != null) {
                        BTConnected = true;

                        BluetoothGattDescriptor receiveConfigDescriptor =
                                receiveCharacteristic.getDescriptor(UUID_CONFIG);
                        if (receiveConfigDescriptor != null) {
                            gatt.setCharacteristicNotification(receiveCharacteristic, true);

                            receiveConfigDescriptor.setValue(
                                    BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(receiveConfigDescriptor);
                        } else {
                            Log.e("BLE", "RFduino receive config descriptor not found!");
                        }
//                        mHandler.postDelayed(() -> sendPOTConst(), 1000);//11111_POT 시간 조절

                    } else {
                        Log.e("BLE", "RFduino receive characteristic not found!");
                    }
                } catch (Exception ignored) {
                }
            } else {
                Log.w("BLE", "onServicesDiscovered received: " + status);
            }
        }

        // GATT characteristic을 읽을 때 호출
        @Override
        // Result of a characteristic read operation
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            Log.v("status", String.valueOf(status));
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }
    };


    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {

        System.out.println(characteristic.getUuid());
    }

    // 블루투스 상태 확인
    BroadcastReceiver mBluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();   //입력된 action
            final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String macAddress = null;
            if (device != null)
                macAddress = device.getAddress();

            switch (action) {

                case BluetoothAdapter.ACTION_STATE_CHANGED: //블루투스의 연결 상태 변경
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            Log.v("BLE_STATE", "STATE_OFF");

                            if (deviceMacAddress.equals(macAddress)) {
                                getActivityOrThrow().unregisterReceiver(mBluetoothStateReceiver);
                                useBleFlag = false;
                                firstBleConnect = false;
                                stopThread();
                            }

                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Log.v("BLE_STATE", "STATE_TURNING_OFF");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Log.v("BLE_STATE", "STATE_ON");
                            if (deviceMacAddress.equals(macAddress)) {
                                getActivityOrThrow().registerReceiver(mBluetoothStateReceiver, stateFilter);

                                if (getActivityOrThrow().checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                                        getActivityOrThrow().checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                                    BleConnectCheck();
                                    useBleFlag = true;
                                }
                            }

                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.v("BLE_STATE", "STATE_TURNING_ON");
                            break;
                    }
                    break;

                case BluetoothDevice.ACTION_ACL_CONNECTED:  //블루투스 기기 연결
                    Log.v("ACTION_ACL_CONNECTED", "ACTION_ACL_CONNECTED");

                    if (deviceMacAddress.equals(macAddress)) {
                        useBleFlag = true;

                        initializeVariables();

                        if (thread == null)
                            doLoop();
                        else
                            restartThread();
                    }

                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    Log.v("ACTION_BOND_STATE_CHANGED", "ACTION_BOND_STATE_CHANGED");
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:   // 블루투스 기기 끊어짐
                    Log.v("ACTION_ACL_DISCONNECTED", "ACTION_ACL_DISCONNECTED");

                    if (deviceMacAddress.equals(macAddress)) {
                        useBleFlag = false;
                        stopThread();
                    }

                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED: //블루투스 기기 검색 시작
                    Log.v("ACTION_DISCOVERY_STARTED", "ACTION_DISCOVERY_STARTED");
                    break;
                case BluetoothDevice.ACTION_FOUND:  //블루투스 기기 검색 됨, 블루투스 기기가 근처에서 검색될 때마다 수행됨
                    Log.v("ACTION_FOUND", "ACTION_FOUND");
                    break;
            }
        }
    };

    File getFileDirectory(String name){
        String directoryName = name;
        return new File(getActivityOrThrow().getFilesDir(), directoryName);
    }

    private void hourlyArrEvent(int arrCnt) {
        if (!hourlyArrNotificationFlag) return;

        String title = null;
        String message = null;

        int hourlyArrFlag = 0;
        int[] thresholds = { 10, 20, 30, 50 };

        for (int i = 0; i < thresholds.length; i++) {
            if (arrCnt >= thresholds[i])
                hourlyArrFlag = i + 1;
            else
                break; // 현재 임계값 보다 arrCnt 값이 작으면 반복문 탈출
        }

        switch (hourlyArrFlag) {
            case 1:
                title = getResources().getString(R.string.notiHourlyArr10);
                message = getResources().getString(R.string.notiHourlyArr10Text);
                break;
            case 2:
                title = getResources().getString(R.string.notiHourlyArr20);
                message = getResources().getString(R.string.notiHourlyArr20Text);
                break;
            case 3:
                title = getResources().getString(R.string.notiHourlyArr30);
                message = getResources().getString(R.string.notiHourlyArr30Text);
                break;
            case 4:
                title = getResources().getString(R.string.notiHourlyArr50);
                message = getResources().getString(R.string.notiHourlyArr50Text);
                break;
        }

        // 알림 대화상자 표시
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityOrThrow(), R.style.AlertDialogTheme);

        View v = LayoutInflater.from(getActivityOrThrow()).inflate(R.layout.heartattack_dialog, (LinearLayout) view.findViewById(R.id.layoutDialog));

        builder.setView(v);

        ((TextView) v.findViewById(R.id.heartattack_title)).setText(title);
        ((TextView) v.findViewById(R.id.textMessage)).setText(message);
        ((TextView) v.findViewById(R.id.btnOk)).setText(getResources().getString(R.string.ok));

        AlertDialog alertDialog = builder.create();

        v.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        // 다이얼로그 형태 지우기
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    private boolean shouldNotify(int previous, int current, int threshold) {
        return previous < threshold && current >= threshold;
    }

    private void heartAttackEvent() {

        if(!HeartAttackCheck) {

            HeartAttackCheck = true; // 중첩 알림이 안뜨게 하는 flag

            // 시스템 사운드 재생
            MediaPlayer mediaPlayer = MediaPlayer.create(getActivityOrThrow(), R.raw.heartattacksound);
            mediaPlayer.setLooping(true); // 반복
            mediaPlayer.start();

            // 알림 대화상자 표시
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivityOrThrow(), R.style.AlertDialogTheme);
            View v = LayoutInflater.from(getActivityOrThrow()).inflate(R.layout.heartattack_dialog, (LinearLayout)view.findViewById(R.id.layoutDialog));
            builder.setView(v);

            TextView title = v.findViewById(R.id.heartattack_title);
            TextView message = v.findViewById(R.id.textMessage);
            Button btnOk = v.findViewById(R.id.btnOk);

            title.setText(getResources().getString(R.string.emergency));
            message.setText(getResources().getString(R.string.emergencyTxt));
            btnOk.setText(getResources().getString(R.string.ok) + "(10)");

            AlertDialog alertDialog = builder.create();

            // ECG Packet
            String strArrEcgData = Arrays.toString(dRecvLast);
            strArrEcgData = strArrEcgData.replace("[", "").replace("]", "");
            String finalStrArrEcgData = strArrEcgData;

            CountDownTimer countDownTimer = new CountDownTimer(11000, 1000) {
                public void onTick(long millisUntilFinished) {
                    btnOk.setText(getResources().getString(R.string.ok) + "(" + millisUntilFinished / 1000 + ")");
                }

                @Override
                public void onFinish() {
                    btnOk.setText(getResources().getString(R.string.sendEmergencyAlert));
                    String writeTime = currentUtcTime();
                    String address = getAddress();
                    retrofitServerManager.sendEmergencyData(email, utcOffsetAndCountry, writeTime, address, finalStrArrEcgData, new RetrofitServerManager.ServerTaskCallback() {
                        @Override
                        public void onSuccess(String result) {
                            Log.i("sendEmergencyData", result);
                            if (result.toLowerCase().trim().contains("true"))
                                toast(getResources().getString(R.string.sendEmergencyAlert));
                            else
                                toast(getResources().getString(R.string.noGuardianSet));
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("sendEmergencyData", "send err");
                            e.printStackTrace();
                            toast(getResources().getString(R.string.guardianSeverErr));
                        }
                    });

                }
            }.start();


            v.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    countDownTimer.cancel();
                    mediaPlayer.release();
                    HeartAttackCheck = false;

                    if (btnOk.getText() != getResources().getString(R.string.sendEmergencyAlert))
                        sendTx("c\n");

                    alertDialog.dismiss();
                }
            });

            if (alertDialog.getWindow() != null ) { // 다이얼로그 형태 지우기
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false); // 다른 곳이 터치되어도 사라지지 않음

            alertDialog.show();
        }
    }

    public void guardianEvent() {

        final String phoneNumberPattern = "^[0-9]{9,11}$";

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityOrThrow(), R.style.AlertDialogTheme);

        View v = LayoutInflater.from(getActivityOrThrow()).inflate(R.layout.guardian_dialog, (LinearLayout)view.findViewById(R.id.layoutDialog));

        builder.setView(v);

        EditText firstGuardianET = v.findViewById(R.id.guardian_First_EditText);
        EditText secondGuardianET = v.findViewById(R.id.guardian_Second_EditText);
        ((TextView)v.findViewById(R.id.guardian_title)).setText(getResources().getString(R.string.setupGuardian));
        ((TextView)v.findViewById(R.id.guardian_Button)).setText(getResources().getString(R.string.ok));

        TextWatcher guardianTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (firstGuardianET.hasFocus()) {
                    firstGuardian = editable.toString();
                    firstCheck = firstGuardian.trim().matches(phoneNumberPattern);
                } else {
                    secondGuardian = editable.toString();
                    secondCheck = secondGuardian.trim().matches(phoneNumberPattern);
                }
            }
        };

        firstGuardianET.addTextChangedListener(guardianTextWatcher);
        secondGuardianET.addTextChangedListener(guardianTextWatcher);

        guardianAlertDialog = builder.create();

        // button event
        v.findViewById(R.id.guardian_Button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                boolean isFirstValid = firstGuardian.isEmpty() || firstCheck;
                boolean isSecondValid = secondGuardian.isEmpty() || secondCheck;

                if (isFirstValid && isSecondValid) {
                    String email = userDetailsSharedPref.getString("email", "NULL");
                    String writeTime = currentUtcTime();
                    ArrayList<String> phoneArrList = new ArrayList<>();

                    if (!firstGuardian.isEmpty())
                        phoneArrList.add(firstGuardian);
                    if (!secondGuardian.isEmpty())
                        phoneArrList.add(secondGuardian);;

                    saveGuardianToServer(email, writeTime, phoneArrList);

                    userDetailsEditor.putString("s_guard_num1", firstGuardian);
                    userDetailsEditor.putString("s_guard_num2", secondGuardian);
                    userDetailsEditor.putBoolean("guardian", true);
                    userDetailsEditor.apply();

                } else {
                    toast(getResources().getString(R.string.setupGuardianTxt));
                }
            }
        });

        // 다이얼로그 형태 지우기
        if (guardianAlertDialog.getWindow() != null ) {
            guardianAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        guardianAlertDialog.setCancelable(false);
        guardianAlertDialog.setCanceledOnTouchOutside(false); // 다른 곳이 터치되어도 사라지지 않음

        guardianAlertDialog.show();
    }

    private void getArrCnt() {

        String endDate = dateController.dateCalculate(currentDate, 1, true);
        retrofitServerManager.getArrCnt(email, currentDate, endDate, new RetrofitServerManager.ServerTaskCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
//                    arrCnt = Integer.parseInt(object.getString("arrCnt")) + arrCnt;
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Exception e) {
//                arrCnt = 0;
                e.printStackTrace();
            }
        });
    }

    private void saveGuardianToServer(String email, String writeTime, ArrayList<String> guardian) {

        retrofitServerManager.setGuardian(email, utcOffsetAndCountry, writeTime, guardian, new RetrofitServerManager.ServerTaskCallback() {
            @Override
            public void onSuccess(String result) {
                Log.i("saveGuardianToServer", result);
                if (result.toLowerCase().contains("true")) {
                    toast(getResources().getString(R.string.setGuardianComp));
                    guardianAlertDialog.dismiss();
                } else {
                    toast(getResources().getString(R.string.setGuardianFail));
                    guardianAlertDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Exception e) {
                toast(getResources().getString(R.string.setGuardianFail));
                guardianAlertDialog.dismiss();
                Log.e("saveGuardianToServer", "send Err");
                e.printStackTrace();
            }
        });
    }

    public void batReceived(int bat){

        Activity activity = safeGetActivity();

        if(activity != null) {
            ProgressBar progressBar = activity.findViewById(R.id.batProgress);
            progressBar.setProgress(bat);

            TextView batValue = activity.findViewById(R.id.myBatValue);
            batValue.setText(""+bat);
        }
    }

    // send BLE
    public void ecgChange(boolean check) {

        if(check) {
            // peak
            sendTx("e\n");
        }else{
            // ecg
            sendTx("p\n");
        }
    }

    public void sendTx(String s) {
        try {
            synchronized (lTx) {
                lTx.add(s);
            }
        } catch (Exception ignored) {
        }
    }

    public void notiCheck() {
        setHeartAttackNotiFlag = userDetailsSharedPref.getBoolean("HeartAttackFlag", true);
        setArrNotiFlag = userDetailsSharedPref.getBoolean("ArrFlag", false);
        setMyoNotiFlag = userDetailsSharedPref.getBoolean("MyoFlag", false);
        setNonContactNotiFlag = userDetailsSharedPref.getBoolean("NonContactFlag", true);
        setTachycardiaNotiFlag = userDetailsSharedPref.getBoolean("FastArrFlag", false);
        setBradycardiaNotiFlag = userDetailsSharedPref.getBoolean("SlowArrFlag", false);
        setAtrialFibrillationNotiFlag = userDetailsSharedPref.getBoolean("HeavyArrFlag", false);
        hourlyArrNotificationFlag = userDetailsSharedPref.getBoolean("HourlyArrFlag", false);
        totalArrNotificationFlag = userDetailsSharedPref.getBoolean("TotalArrFlag", false);

        System.out.println("totalArrNotificationFlag : " + totalArrNotificationFlag);
        System.out.println("hourlyArrNotificationFlag : " + hourlyArrNotificationFlag);

    }

    public void statusCheck(){
        int intCurrentHour = Integer.parseInt(currentHour);

        if(eCalBPM <= intRealBPM || tenSecondStep > 6){
            // 활동중
            bodyStatus = "E";

            exerciseBackground.setBackground(ContextCompat.getDrawable(getActivityOrThrow(), R.drawable.rest_round_press));
            exerciseText.setTextColor(Color.WHITE);
            exerciseImg.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

            restBackground.setBackgroundColor(Color.TRANSPARENT);
            restText.setTextColor(ContextCompat.getColor(getActivityOrThrow(), R.color.lightGray));
            restImg.setColorFilter(ContextCompat.getColor(getActivityOrThrow(), R.color.lightGray));

            sleepBackground.setBackgroundColor(Color.TRANSPARENT);
            sleepText.setTextColor(ContextCompat.getColor(getActivityOrThrow(), R.color.lightGray));
            sleepImg.setColorFilter(ContextCompat.getColor(getActivityOrThrow(), R.color.lightGray));

        }
        else if( ( sleep < intCurrentHour || wakeup > intCurrentHour  ) && tenSecondStep < 6){
            // 수면중
            bodyStatus = "S";

            exerciseBackground.setBackgroundColor(Color.TRANSPARENT);
            exerciseText.setTextColor(ContextCompat.getColor(getActivityOrThrow(), R.color.lightGray));
            exerciseImg.setColorFilter(ContextCompat.getColor(getActivityOrThrow(), R.color.lightGray));

            restBackground.setBackgroundColor(Color.TRANSPARENT);
            restText.setTextColor(ContextCompat.getColor(getActivityOrThrow(), R.color.lightGray));
            restImg.setColorFilter(ContextCompat.getColor(getActivityOrThrow(), R.color.lightGray));

            sleepBackground.setBackground(ContextCompat.getDrawable(getActivityOrThrow(), R.drawable.rest_round_press));
            sleepText.setTextColor(Color.WHITE);
            sleepImg.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        }
        else{
            // 휴식중
            bodyStatus = "R";

            exerciseBackground.setBackgroundColor(Color.TRANSPARENT);
            exerciseText.setTextColor(ContextCompat.getColor(getActivityOrThrow(), R.color.lightGray));
            exerciseImg.setColorFilter(ContextCompat.getColor(getActivityOrThrow(), R.color.lightGray));

            restBackground.setBackground(ContextCompat.getDrawable(getActivityOrThrow(), R.drawable.rest_round_press));
            restText.setTextColor(Color.WHITE);
            restImg.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

            sleepBackground.setBackgroundColor(Color.TRANSPARENT);
            sleepText.setTextColor(ContextCompat.getColor(getActivityOrThrow(), R.color.lightGray));
            sleepImg.setColorFilter(ContextCompat.getColor(getActivityOrThrow(), R.color.lightGray));

        }
    }

    public void stopThread() {
        if (thread != null)
            thread.interrupt();
        isRun = false;
    }

    public void restartThread() {
        Log.v("doLoop", "restartThread");
        stopThread();
        isRun = true;
        doLoop();
    }

    public void timeZone(){
        utcOffsetAndCountry = MyTimeZone.getInstance().getTimeZone(getActivityOrThrow());
    }

    public String currentUtcTime(){
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        TimeZone currentTimeZone = TimeZone.getDefault();
        String timeZoneId = currentTimeZone.getID();
        ZonedDateTime currentTimezone = now.withZoneSameInstant(ZoneId.of(timeZoneId));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

        return formatter.format(currentTimezone);
    }

    private FragmentActivity safeGetActivity() {
        if (activity == null) {
            Log.e("HomeFragment", "Fragment is not attached to an activity.");
            return null;
        }
        return activity;
    }

    private FragmentActivity getActivityOrThrow() {
        return Objects.requireNonNull(safeGetActivity());
    }

    public void runOnActivityUiThread(Runnable action) {
        if (activity != null) {
            getActivityOrThrow().runOnUiThread(action);
        } else {
            Log.e("runOnActivityUiThread", "Activity is null. Cannot run on UI thread.");
        }
    }

    public void toast(String string) {
        runOnActivityUiThread(() -> Toast.makeText(safeGetActivity(), string, Toast.LENGTH_SHORT).show());
    }

    private String getAddress(){
        double latitude = gpsTracker.getLatitude(); // 위도
        double longitude = gpsTracker.getLongitude(); // 경도

        return getCurrentAddress(latitude, longitude);
    }

    public String getCurrentAddress( double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(safeGetActivity(), Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            toast("geocoder Service unavailable");
            return "geocoder Service unavailable";
        } catch (IllegalArgumentException illegalArgumentException) {
            toast("Invalid GPS coordinates");
            return "Invalid GPS coordinates";
        }

        if (addresses == null || addresses.size() == 0) {
            toast("Address not found");
            return "Address not found";
        }

        Address address = addresses.get(0);
        return address.getAddressLine(0) +"\n";
    }

    /*PERMISSION*/
    //region
    private void permissionCheck() { // 권한 확인
        permissionManager = PermissionManager.getInstance(this, getActivity(), this, getContext(), email);
        if (permissionManager.checkAndRequestPermissions(email)) // permission all granted
            onAllPermissionsGranted();
        else
            permissionDeniedCheck();
    }

    @Override    // 모든 권한 승인 callback
    public void onPermissionGranted() {
        onAllPermissionsGranted();
    }

    @Override   // 하나 이상의 권한 거부 callback
    public void onPermissionDenied(List<String> grantedPermissions, List<String> deniedPermissions) {
        permissionDeniedCheck();
    }

    public void setGuardianFlag() {
        setGuardian();
    }

    private void onAllPermissionsGranted() {
        notificationManager = NotificationManager.getInstance(safeGetActivity());
        serviceIntent = new Intent(safeGetActivity(), ForegroundService.class);

        startForegroundService();
        BleConnectCheck();
    }

    private void permissionDeniedCheck() {
        if (hasBlePermissions(getContext())) { // BLE
            BleConnectCheck();
            permissionManager.showPermissionDialog();
        }

        if (Build.VERSION.SDK_INT >= ANDROID_O_MR1) { // NOTIFICATION
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
                notificationManager = NotificationManager.getInstance(safeGetActivity());
        } else {
            notificationManager = NotificationManager.getInstance(safeGetActivity());
        }
    }

    private boolean hasBlePermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void setGuardian() {
        if (!guardianCheck)     // first time check
            guardianEvent();    // set guardian
    }
    //endregion


    /*LIFECYCLE*/
    //region
    @SuppressLint("MissingPermission")
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Fragment", "onDestroy called");

        localDataLoop = false;
        PermissionManager.reset();
        getActivityOrThrow().unregisterReceiver(mBluetoothStateReceiver);

        stopScanning();
        stopThread();

        if (serviceIntent != null)
            stopService();

        // 블루투스 연결 활성화 시 연결 해제
        try {
            if (bluetoothGatt != null) {
                Log.v("BLE_STATE", "DISCONNECT");
                sendCalcDataAsCsv(currentHour, sendHourlyStep, sendHourlyDistance, sendHourlyTCal, sendHourlyECal, sendHourlyArrCnt);
                bluetoothGatt.disconnect(); // gatt 클라이언트에서 gatt 서버 연결 해제
                bluetoothGatt.close(); // 클라이언트, 리소스 해제
            }
        } catch (Exception e) {
            Log.e("BLE BLE_STATE", "DISCONNECT FAIL");
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (permissionManager.getFlag()) {// ACCESS_BACKGROUND_LOCATION_REQUEST_CODE 설정 후 다시 돌아올 때 발생
            setGuardian();
            permissionManager.setFlag();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (onBackPressedDialog != null && onBackPressedDialog.isShowing()) {
            onBackPressedDialog.dismiss();
        }
    }
    //endregion


}
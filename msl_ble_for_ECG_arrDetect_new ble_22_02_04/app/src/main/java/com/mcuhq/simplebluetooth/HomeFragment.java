package com.mcuhq.simplebluetooth;

//import static com.mcuhq.simplebluetooth.RealService.serviceIntent;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
// Server
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class HomeFragment extends Fragment {

    Intent serviceIntent;
    private SharedViewModel viewModel;

    private Thread thread;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";

    public final static UUID uuid_service =
            UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    public final static UUID uuid_recieve =
            UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    public final static UUID uuid_tx =
            UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    public final static UUID uuid_config =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");


    // ------------------------ BLE VAR (START) ------------------------
    private static final int PERMISSION_REQUEST_CODE_S = 1001;
    private static final int PERMISSION_REQUEST_CODE = 1002;
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    private static final long SCAN_PERIOD = 10000;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    int isBTType = 1; // 0=BTClassic 1=BLE
    private Handler mHandler;
    Handler handler2 = new Handler();
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data

    public Boolean BTConnected = false;
    public Boolean ECGFound = false;

    int mConnectionState = 0;

    private BluetoothGattService mBluetoothGattService;
    BluetoothGatt bluetoothGatt;
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    Boolean btScanning = false;
    int deviceIndex = 0;
    ArrayList<BluetoothDevice> devicesDiscovered = new ArrayList<BluetoothDevice>();
    List<String> lTx = new ArrayList<>();

    // ------------------------ BLE VAR (END) ------------------------

    // ------------------------ doLoop VAR (START) ------------------------

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

    int intHRV;
    int intBPM;
    double doubleBAT;
    double doubleTEMP;

    String stringHRV;
    String stringBPM;
    String stringArr;
    String stringArrCnt;

    int intRealBPM;
    String stringRealBPM;
    String stringRealHRV;

    int minBPM;
    int diffMinBPM;
    Boolean diffMinFlag;
    int maxBPM;
    int diffMaxBPM;
    Boolean diffMaxFlag;
    int tenSecondAvgBPM; // csv 파일 저장 변수
    int tenMinuteAvgBPM;
    int diffAvgBPM;
    Boolean diffAvgFlag;
    int tenSecondAvgBpmCnt;
    int tenMinuteAvgBpmCnt;
    int tenSecondBpmSum;
    int tenMinuteBpmSum;

    int tenSecondHRVSum;
    int tenSecondAvgHRV;

    double tenSecondTempSum;
    double tenSecondAvgTemp;

    double pBPM;
    double cBPM;
    double realBPM;

    public int dpscnt = 0;
    public float dps = 0;

    int iCalTimer;
    int iCalTimeCount;
    double dCal;
    double dCalMinU = 0;
    int disheight;

    int HeartAttack = 0;
    int HeartAttackFlag = 0;
    boolean HeartAttackCheck = false;
    int noncontact = 0;
    int noncontactCount = 0;
    boolean noncontactFlag = false;
    int myo = 0;
    int myoFlag = 0;

    int tarchycardia = 0;
    int bradycardia = 0;
    int atrialFibrillaion = 0;

    String arrStatus = "";
    String bodyStatus = "";

    String currentCountry;
    String utcOffset;
    String utcOffsetAndCountry;
    // 장치 사용 플래그
    boolean useBleFlag = false;
    private volatile boolean isRun = false;

    ScheduledExecutorService executorService;
    public int iGender;
    public double dWeight;
    public int iAge;
    public String userName;

    int sleep;
    int wakeup;

    public double dExeCal;
    double distance;
    double wdistance;
    double realDistanceKM;
    double realDistanceM;

    double wdCal;
    double wdExeCal;

    private int eCalBPM;

    double avgsize;
    int nowstep;
    int allstep;
    int allstep10s = 0;
    int wallstep;

    // 매시간 저장되는 변수
    int hourlyAllstep = 0;
    double hourlyDistance = 0;
    double hourlyDistanceM = 0;
    double hourlyDistanceKM = 0;
    double hourlyTotalCal = 0;
    double hourlyExeCal = 0;
    int hourlyArrCnt = 0;

    int monthlyAllStep = 0;
    double monthlyDistance = 0;
    double monthlyTotalCal = 0;
    double monthlyExeCal = 0;
    int monthlyArrCnt = 0;

    int preMonthlyAllStep = 0;
    double preMonthlyDistance = 0;
    double preMonthlyDistanceM = 0;
    double preMonthlyDistanceKM = 0;
    double preMonthlyTotalCal = 0;
    double preMonthlyExeCal = 0;
    int preMonthlyArrCnt = 0;

    int iRecvCnt = 0;


    // Ecg var
    double[] ecgPacket = new double[14];
    StringBuilder ecgPacketList;
    int ecgPacketCnt = 0;
    int ecgCnt;


    // ------------------------ doLoop VAR (END) ------------------------

    // ------------------------ doBufProcess VAR (START) ------------------------
    public final int _MAX_CH = 6;
    double[] bluetoothData = new double[_MAX_CH];
    Date[] lVTime = new Date[_MAX_CH];
    int arrCnt;
    int arr;

    String arrTime;

    double[] dRecv = new double[500];
    double[] dRecvLast = new double[500];
    int iRecvLastCnt = 0;

    // ------------------------ doBufProcess VAR (END) ------------------------
    // 권한 사용 요청
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.e("Activity result", "OK");
                    Intent data = result.getData();
                    // 블루투스 승인
                    BleConnectCheck();
                }
            });

    private LineChart chart;

    public long tickLast = 0;

    public int chartxcnt = 200;

    String deviceMacAddress;

    // ----------------------------- doLoop var -----------------------------
    public String empid = "";

    // ----------------------------- chart var -----------------------------
    String[] lVName = new String[_MAX_CH];

    ArrayList<String> permissions = new ArrayList<String>();
    String[] PERMISSIONS;

    private ActivityResultContracts.RequestMultiplePermissions multiplePermissionsContract;
    private ActivityResultLauncher<String[]> multiplePermissionLauncher;

    IntentFilter stateFilter = new IntentFilter();

    View view;

    TextView bpm_value;
    TextView bpm_maxValue;
    TextView bpm_diffMaxValue;
    TextView bpm_avgValue;
    TextView bpm_diffAvgValue;
    TextView bpm_minValue;
    TextView bpm_diffMinValue;
    TextView hrv_value;
    TextView arr_value;
    TextView eCal_value;
    TextView step_value;
    TextView temp_value;
    TextView distance_value;

    TextView restText;
    TextView sleepText;
    TextView exerciseText;

    ImageView restImg;
    ImageView sleepImg;
    ImageView exerciseImg;

    LinearLayout restBackground;
    LinearLayout sleepBackground;
    LinearLayout exerciseBackground;

    FrameLayout testButton;

    // notification
    boolean notificationsPermissionCheck;
    boolean nonContactCheck = true;
    boolean myoCheck = true;
    int notificationId = 0;
    private static final String PRIMARY_CHANNEL_ID = "notification";
    private NotificationManager notificationManager;

    boolean setHeartAttackNotiFlag = false;
    boolean setArrNotiFlag = false;
    boolean setMyoNotiFlag = false;
    boolean setNonContactNotiFlag = false;
    boolean setTarchycardiaNotiFlag = false;
    boolean setBradycardiaNotiFlag = false;
    boolean setAtrialFibrillaionNotiFlag = false;

    boolean infoCheck = false;
    boolean targetCheck = false;
    boolean setCheck = false;

    // 서버 전송 핸들러
    private Handler handler = new Handler(Looper.getMainLooper());
    boolean sendCheck = true; // 서버 전송 체크 플래그

    SharedPreferences userDetailsSharedPref;
    SharedPreferences.Editor userDetailsEditor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        userDetailsSharedPref = safeGetActivity().getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        userDetailsEditor = userDetailsSharedPref.edit();

        useBleFlag = false;

        // BLE 상태 요소
        stateFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); //BluetoothAdapter.ACTION_STATE_CHANGED : 블루투스 상태변화 액션
        stateFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        stateFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED); //연결 확인
        stateFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED); //연결 끊김 확인
        stateFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        stateFilter.addAction(BluetoothDevice.ACTION_FOUND);    //기기 검색됨
        stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);   //기기 검색 시작
        stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);  //기기 검색 종료
        stateFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);

        // BLE 상태 변화 탐지
        safeGetActivity().registerReceiver(mBluetoothStateReceiver, stateFilter);

        // UI와 매핑
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

        // 블루투스 서비스 제어, 블루투스 서비스 관리
        btManager = (BluetoothManager) safeGetActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        // 현재 시스템에서 사용 가능한 BluetoothAdapter 인스턴스를 반환
        btAdapter = btManager.getAdapter();

        ecgPacketList = new StringBuilder();

        currentTimeCheckTread();

        initVar();

        chartInit();

        timeZone();

        uiSettingLoad();
        uiProfileLoad();

        notiCheck();
        targetCheck();

        permissionsCheck();

        startService();

        setOnBackPressed();
        setViewModel();

        return view;
    }


    public void setViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        String myGender = "";
        viewModel.getGender().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String genderValue) {
                String myGender = genderValue;
                if (myGender == "남자")
                    iGender = 1;
                else
                    iGender = 2;
            }
        });

        viewModel.getAge().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String string) {
                iAge = Integer.parseInt(string);
            }
        });

        viewModel.getHeight().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String string) {
                disheight = Integer.parseInt(string);
            }
        });

        viewModel.getWeight().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String string) {
                dWeight = Integer.parseInt(string);
            }
        });

        viewModel.getSleep().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String string) {
                sleep = Integer.parseInt(string);
            }
        });

        viewModel.getWakeup().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String string) {
                wakeup = Integer.parseInt(string);
            }
        });

        viewModel.getBpm().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String string) {
                eCalBPM = Integer.parseInt(string);
            }
        });

        viewModel.getEmergency().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean check) {
                setHeartAttackNotiFlag = check;
            }
        });

        viewModel.getArr().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean check) {
                setArrNotiFlag = check;
            }
        });

        viewModel.getMyo().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean check) {
                setMyoNotiFlag = check;
            }
        });

        viewModel.getNonContact().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean check) {
                setNonContactNotiFlag = check;
            }
        });

        viewModel.getFastArr().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean check) {
                setTarchycardiaNotiFlag = check;
            }
        });

        viewModel.getSlowarr().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean check) {
                setBradycardiaNotiFlag = check;
            }
        });

        viewModel.getIrregular().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean check) {
                setAtrialFibrillaionNotiFlag = check;
            }
        });

    }

    public void setOnBackPressed() {
        // Callback을 등록
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // 뒤로가기 버튼이 눌렸을 때 여기에 코드를 작성

                new AlertDialog.Builder(safeGetActivity())
                        .setTitle("알림")
                        .setMessage("종료하시겠습니까?")
                        .setNegativeButton("취소", null)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                HomeFragment.super.getActivity().onBackPressed();
                                if (serviceIntent != null) {
                                    stopService();

                                    android.os.Process.killProcess(android.os.Process.myPid());
                                    System.exit(1);
                                }
                            }
                        }).create().show();

                requireActivity().getSupportFragmentManager().popBackStack();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public void startService() {
        serviceIntent = new Intent(safeGetActivity(), ForegroundService.class);
        safeGetActivity().startService(serviceIntent);
    }

    public void stopService() {
        serviceIntent = new Intent(safeGetActivity(), ForegroundService.class);
        safeGetActivity().stopService(serviceIntent);
    }

    private void permissionsCheck() {

        // Noti
        if (safeGetActivity().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // 권한 있음
            notificationsPermissionCheck = true;
            userDetailsEditor.putBoolean("noti", notificationsPermissionCheck);
            // 알람
            createNotificationChannel();
        }

        // 배터리 최적화 기능 무시 : 백그라운드에서도 지속적으로 작동해야 하므로 배터리 최적화 기능을 무시하도록 권한 요청
        PowerManager pm = (PowerManager) safeGetActivity().getApplicationContext().getSystemService(safeGetActivity().POWER_SERVICE);
        boolean isWhiteListing = false;
        isWhiteListing = pm.isIgnoringBatteryOptimizations(safeGetActivity().getApplicationContext().getPackageName());
        if (!isWhiteListing) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + safeGetActivity().getApplicationContext().getPackageName()));
            startActivity(intent);
        }

        // 모든 버전에 공통으로 필요한 권한들
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN);
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
        }


        if (Build.VERSION.SDK_INT >= 33) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS);
        }

        // array로 변경
        PERMISSIONS = permissions.toArray(new String[0]);
        Log.e("PERMISSIONS", Arrays.toString(PERMISSIONS));

        // 권한 요청
        multiplePermissionsContract = new ActivityResultContracts.RequestMultiplePermissions();
        multiplePermissionLauncher = registerForActivityResult(multiplePermissionsContract, isGranted -> {
            Log.d("PERMISSIONS", "Launcher result: " + isGranted.toString());

            // 권한 응답 처리
            if (isGranted.containsValue(false)) {
                // 권한 거부
                Log.d("PERMISSIONS", "At least one of the permissions was not granted, launching again...");
                Toast.makeText(safeGetActivity(), "권한을 전부 허용하지 않아\n일부 기능이 제한됩니다", Toast.LENGTH_SHORT).show();

                multiplePermissionLauncher.launch(PERMISSIONS);

                // BLE
                if (safeGetActivity().checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                        safeGetActivity().checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    BleConnectCheck();

                    notificationsPermissionCheck = false;
                    userDetailsEditor.putBoolean("noti", notificationsPermissionCheck);
                }

                // Noti
                if (safeGetActivity().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    // 권한 있음
                    notificationsPermissionCheck = true;
                    userDetailsEditor.putBoolean("noti", notificationsPermissionCheck);
                    // 알람
                    createNotificationChannel();
                }

            } else {
                // 권한 승인
                showPermissionDialog(safeGetActivity());
                createNotificationChannel();
                BleConnectCheck();

                notificationsPermissionCheck = true;
                userDetailsEditor.putBoolean("noti", notificationsPermissionCheck);
            }
        });

        // 필요 권한 확인 후 권한 요청
        askPermissions(multiplePermissionLauncher);

    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // 여기서 사용자에게 권한이 왜 필요한지 설명할 수 있습니다.
        }

        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }
    private void askPermissions(ActivityResultLauncher<String[]> multiplePermissionLauncher) {
        if (!hasPermissions(PERMISSIONS)) {
            Log.d("PERMISSIONS", "Launching multiple contract permission launcher for ALL required permissions");
            multiplePermissionLauncher.launch(PERMISSIONS);
        } else {
            Log.d("PERMISSIONS", "All permissions are already granted");
            BleConnectCheck();
        }
    }

    private boolean hasPermissions(String[] permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(safeGetActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSIONS", "Permission is not granted: " + permission);
                    return false;
                }
                Log.d("PERMISSIONS", "Permission already granted: " + permission);
            }
            return true;
        }
        return false;
    }

    public void localDataSave() {
        /*
        - 1초 마다 내부에 저장
         */
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(safeGetActivity()) /* Activity context */);
        SharedPreferences.Editor editor = sharedPref.edit();

        // 현재 시간
        long now = System.currentTimeMillis();
        Date current_Date = new Date(now);

        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");

        String stringCurrentDate = date.format(current_Date);

        pre_date_pause = sharedPref.getString("preDate", "2023-01-01");
//        pre_date_pause = sharedPref.getString("test", "2023-01-01"); // test

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate checkCurrentDate = LocalDate.parse(stringCurrentDate, formatter);
        LocalDate checkPreDate = LocalDate.parse(pre_date_pause, formatter);

        // 날짜 비교
        if (checkCurrentDate.isEqual(checkPreDate)) {
            // 날짜가 같음 (기존 데이터 유지)
            editor.putString("distance", String.valueOf(distance));
            editor.putString("cal", String.valueOf(dCal));
            editor.putString("execal", String.valueOf(dExeCal));
            editor.putString("allstep", String.valueOf(allstep));
            editor.putString("arr", String.valueOf(arrCnt));

            editor.putString("currentDate", currentDate);
            editor.putString("hour", currentHour);

            monthlyAllStep = preMonthlyAllStep + allstep;
            monthlyDistance = preMonthlyDistance + realDistanceM;
            monthlyTotalCal = preMonthlyTotalCal + dCal;
            monthlyExeCal = preMonthlyExeCal + dExeCal;
            monthlyArrCnt = preMonthlyArrCnt + arrCnt;

//            Log.e("currentMinute", currentMinute);
//            Log.e("currentSecond", currentSecond);

            // 시간대 별 저장 데이터(1시간)
            if (currentMinute.equals("00") && currentSecond.equals("00")){
                // 초기화
                Log.e("initCheck", "initCheck");

                editor.putInt("hourlyArrCnt", arrCnt);
                editor.putInt("hourlyAllStep", allstep);
                editor.putFloat("hourlyDistance", (float) (distance / 100));
                editor.putFloat("hourlyTotalCal", (float) dCal);
                editor.putFloat("hourlyExeCal", (float) dExeCal);
                editor.putString("preHour", currentHour);

                editor.apply();

                hourlyArrCnt = sharedPref.getInt("hourlyArrCnt", 0);
                hourlyAllstep = sharedPref.getInt("hourlyAllStep", 0);
                hourlyDistance = sharedPref.getFloat("hourlyDistance", 0);
                hourlyTotalCal = sharedPref.getFloat("hourlyTotalCal", 0);
                hourlyExeCal = sharedPref.getFloat("hourlyExeCal", 0);

                Log.e("hourlyArrCnt", String.valueOf(hourlyArrCnt));
                Log.e("hourlyAllstep", String.valueOf(hourlyAllstep));
                Log.e("hourlyDistance", String.valueOf(hourlyDistance));
                Log.e("hourlyTotalCal", String.valueOf(hourlyTotalCal));
                Log.e("hourlyExeCal", String.valueOf(hourlyExeCal));

            }

        } else {
            // 날짜가 다른 경우(초기화)
            editor.putString("distance", "0");
            editor.putString("cal", "0");
            editor.putString("execal", "0");
            editor.putString("allstep", "0");
            editor.putString("arr", "0");

            // 시간대 별 저장 데이터(1시간)
            editor.putInt("hourlyArrCnt", 0);
            editor.putInt("hourlyAllStep", 0);
            editor.putFloat("hourlyDistance", 0);
            editor.putFloat("hourlyTotalCal", 0);
            editor.putFloat("hourlyExeCal", 0);

            // 월별 데이터 저장
            preMonthlyAllStep = sharedPref.getInt("preMonthlyAllStep", 0);
            preMonthlyDistance = sharedPref.getFloat("preMonthlyDistance", 0);
            preMonthlyTotalCal = sharedPref.getFloat("preMonthlyTotalCal", 0);
            preMonthlyExeCal = sharedPref.getFloat("preMonthlyExeCal", 0);
            preMonthlyArrCnt = sharedPref.getInt("preMonthlyArrCnt", 0);

            monthlyAllStep = preMonthlyAllStep + allstep;
            monthlyDistance = preMonthlyDistance + realDistanceM;
            monthlyTotalCal = preMonthlyTotalCal + dCal;
            monthlyExeCal = preMonthlyExeCal + dExeCal;
            monthlyArrCnt = preMonthlyArrCnt + arrCnt;

            // 기존 값 저장
            editor.putInt("preMonthlyAllStep", monthlyAllStep);
            editor.putFloat("preMonthlyDistance", (float) monthlyDistance);
            editor.putFloat("preMonthlyTotalCal", (float) monthlyTotalCal);
            editor.putFloat("preMonthlyExeCal", (float) monthlyExeCal);
            editor.putInt("preMonthlyArrCnt", monthlyArrCnt);

            editor.putString("preDate", currentDate);
            editor.putString("preHour", currentHour);
            editor.putString("currentDate", currentDate);

            editor.apply();

            hourlyArrCnt = sharedPref.getInt("hourlyArrCnt", 0);
            hourlyAllstep = sharedPref.getInt("hourlyAllStep", 0);
            hourlyDistance = sharedPref.getFloat("hourlyDistance", 0);
            hourlyTotalCal = sharedPref.getFloat("hourlyTotalCal", 0);
            hourlyExeCal = sharedPref.getFloat("hourlyExeCal", 0);

            distance = 0;
            dCal = 0;
            dExeCal = 0;
            allstep = 0;
            arrCnt = 0;
        }

//        Log.e("hourlyData", "hourlyArrCnt : " + String.valueOf(hourlyArrCnt) + " hourlyAllstep : " + String.valueOf(hourlyAllstep) + " hourlyDistance : " + String.valueOf(hourlyDistance) + " hourlyTotalCal : " + String.valueOf(hourlyTotalCal) + " hourlyExeCal : " + String.valueOf(hourlyExeCal));

        editor.apply();
    }

    // BPM 데이터 저장
    public void saveBpmDataAsCsv() {

        if (tenSecondAvgBPM == 0) {
            tenSecondAvgBPM = 70;
        }

        try {
            String directoryName = "LOOKHEART/" + currentYear + "/" + currentMonth + "/" + currentDay;
            File directory = new File(safeGetActivity().getFilesDir(), directoryName);

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

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(safeGetActivity() /* Activity context */);
            SharedPreferences.Editor editor = sharedPref.edit();

            // 현재 hour 값(정수)
            int intCurrentHour = Integer.parseInt(currentHour);
            // 이전 hour 값(정수)
            int intPreHour;

            // 경로
            String directoryName = "LOOKHEART/" + currentYear + "/" + currentMonth + "/" + currentDay;
            File directory = new File(safeGetActivity().getFilesDir(), directoryName);

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

//                    Log.e("dExeCal", String.valueOf(dExeCal));
//                    Log.e("hourlyExeCal", String.valueOf(Math.round(hourlyExeCal*10) / 10.0));
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

    // ecg 데이터 csv 파일 저장
    public void saveEcgDataAsCsv() {
        try {

            String directoryName = "LOOKHEART/" + currentYear + "/" + currentMonth + "/" + currentDay;
            File directory = new File(safeGetActivity().getFilesDir(), directoryName);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, "EcgData.csv");

            // 앞 뒤 "[" "]" 제거
            String strEcgData = Arrays.toString(ecgPacket);
            strEcgData = strEcgData.replace("[", "").replace("]", "");


            FileOutputStream fos = new FileOutputStream(file, true); // 'true' to append
            String csvData = currentTime + "," + utcOffsetAndCountry + "," + intRealBPM + "," + strEcgData + "\n";
            fos.write(csvData.getBytes());
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveArrEcgDataAsCsv() {
        String filename = "";

        try {

            String directoryName = "LOOKHEART/" + currentYear + "/" + currentMonth + "/" + currentDay + "/" + "arrEcgData";
            File directory = new File(safeGetActivity().getFilesDir(), directoryName);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, "arrEcgData_" + arrCnt + ".csv");

            // 앞 뒤 "[" "]" 제거
            String strArrEcgData = Arrays.toString(dRecvLast);
            strArrEcgData = strArrEcgData.replace("[", "").replace("]", "");

//            Log.d("strEcgData", strArrEcgData);
            FileOutputStream fos = new FileOutputStream(file, true); // 'true' to append
            String csvData = currentTime + "," + utcOffsetAndCountry + "," + bodyStatus + "," + arrStatus + "," + strArrEcgData + "\n";
            fos.write(csvData.getBytes());
            fos.close();

            sendArrDataAsCsv();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDailyDataAsCsv(){
        int month = 1;
        Boolean flag = false;

        try {

            // 현재 hour 값(정수)
            int intCurrentYear = Integer.parseInt(currentYear);
            int intCurrentMonth = Integer.parseInt(currentMonth);
            int intCurrentDay = Integer.parseInt(currentDay);
            // 이전 hour 값(정수)
            int intPreDay;

            LocalDate specificDate;
            DayOfWeek dayOfWeek;

            String directoryName = "LOOKHEART/" + "dailyData";
            File directory = new File(safeGetActivity().getFilesDir(), directoryName);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, "dailyCalandDistanceData.csv");
            FileOutputStream fos;

            // 년, 월, 일, 요일, 걸음, 거리, 칼로리, 활동칼로리, 비정상맥박

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

                // 정수로 변환(이전 시간)
                intPreDay = Integer.parseInt(values[2]);
//                intPreDay = 30; // test

//                Log.d("intPreDay", String.valueOf(intPreDay));
//                Log.d("intCurrentDay", String.valueOf(intCurrentDay));
                if (intPreDay == intCurrentDay) {
                    // 같은 날인 경우 ( 덮어쓰기 )
                    // 파일 쓰기
                    fos = new FileOutputStream(file, false);

                    int intDailyTotalCal = (int) dCal;
                    int intDailyExeCal = (int) dExeCal;
                    int intDailyDistanceM = (int) realDistanceM;

                    // 년, 월, 일, 요일, 걸음, 거리, 칼로리, 활동칼로리, 비정상맥박
                    values[0] = String.valueOf(Integer.parseInt(currentYear));
                    values[1] = String.valueOf(Integer.parseInt(currentMonth));
                    values[2] = String.valueOf(Integer.parseInt(currentDay));

                    values[3] = searchDay(Integer.parseInt(currentYear), Integer.parseInt(currentMonth), Integer.parseInt(currentDay));

                    values[4] = utcOffsetAndCountry; // utc Offset, country

                    values[5] = String.valueOf(allstep); // 걸음
                    values[6] = String.valueOf(intDailyDistanceM); // 거리
                    values[7] = String.valueOf(intDailyTotalCal); // 총 칼로리
                    values[8] = String.valueOf(intDailyExeCal); // 활동 칼로리
                    values[9] = String.valueOf(arrCnt); // 비정상맥박 횟수

                    lines.set(lines.size() - 1, String.join(",", values)); // 저장

                    // 파일에 다시 쓰기
                    for (String writeLine : lines) {
                        fos.write((writeLine + "\n").getBytes());
                    }

                    fos.close();
                }
                else {
                    // 다른 시간인 경우
                    fos = new FileOutputStream(file, true);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    DateTimeFormatter checkFormatter = DateTimeFormatter.ofPattern("yyyy-M-d");
                    LocalDate date;
                    String stringPreDate;

                    LocalDate currentDate = LocalDate.of(intCurrentYear, intCurrentMonth, intCurrentDay);
                    LocalDate preDate = LocalDate.of(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]));
//                    LocalDate currentDate = LocalDate.of(2023, 8, 19); // test
//                    LocalDate preDate = LocalDate.of(2023, 9, 1); // test

                    // 날짜 차이 구하기
                    long daysBetween = ChronoUnit.DAYS.between(preDate, currentDate);
                    // 변환 String -> LocalDate -> String
                    stringPreDate = values[0] + "-" + values[1] + "-" + values[2]; // 월과 일이 한 자리 수로 나와서 변환 필요
                    LocalDate localDate = LocalDate.parse(stringPreDate, checkFormatter);
                    stringPreDate = String.valueOf(localDate);

//                    Log.d("daysBetween", String.valueOf(daysBetween));
                    for (int i = 0; daysBetween > i; i++) {
                        date = LocalDate.parse(stringPreDate, formatter);
                        date = date.plusDays(1);

                        stringPreDate = date.format(formatter);
                        String[] splitPreDate = stringPreDate.split("-");

                        String dayOfTheWeek = searchDay(Integer.parseInt(splitPreDate[0]), Integer.parseInt(splitPreDate[1]), Integer.parseInt(splitPreDate[2]));

                        String csvData = Integer.parseInt(splitPreDate[0]) + "," + Integer.parseInt(splitPreDate[1]) + "," + Integer.parseInt(splitPreDate[2]) + "," + dayOfTheWeek + "," + utcOffsetAndCountry + "," + "0" + ","  + "0" + "," + "0" + "," + "0" + "," + "0" + "\n";

                        fos.write(csvData.getBytes());
                    }

                    fos.close();
                }

                fos.close();
            }
            else{
                // 파일이 없는 경우
                fos = new FileOutputStream(file, true);

                while(true){
                    YearMonth yearMonth = YearMonth.of(Integer.parseInt(currentYear), month);
                    int daysInMonth = yearMonth.lengthOfMonth();

                    for(int i = 1; daysInMonth >= i ; i++){

                        String dayOfTheWeek = searchDay(Integer.parseInt(currentYear), month, i);

                        String csvData = currentYear + "," + month + "," + i + "," + dayOfTheWeek + "," + utcOffsetAndCountry + "," + "0" + ","  + "0" + "," + "0" + "," + "0" + "," + "0" + "\n";
                        fos.write(csvData.getBytes());

                        if (intCurrentMonth == month && intCurrentDay == i){
                            flag = true;
                            break;
                        }
                    }

                    month++;

                    if (flag || month == 13)
                        break;
                }

                fos.close();
            }

            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMonthlyDataAsCsv(){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(safeGetActivity());
        SharedPreferences.Editor editor = sharedPref.edit();

        int month = 1;
        int intPreYear;
        int intPreMonth;

        // 현재 hour 값(정수)
        int intCurrentYear = Integer.parseInt(currentYear);
        int intCurrentMonth = Integer.parseInt(currentMonth);
        int intCurrentDay = Integer.parseInt(currentDay);

        try{
            String directoryName = "LOOKHEART/" + "monthlyData";
            File directory = new File(safeGetActivity().getFilesDir(), directoryName);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, "monthlyCalAndDistanceData.csv");
            FileOutputStream fos;

            // 년, 월, 걸음, 거리, 칼로리, 활동칼로리, 비정상맥박
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

                // 정수로 변환(이전 시간)
                intPreYear = Integer.parseInt(values[0]);
                intPreMonth = Integer.parseInt(values[1]);

//                intCurrentYear = 2024;
//                intCurrentMonth = 12;

                if (intPreMonth == intCurrentMonth) {
                    // 같은 달인 경우 (덮어쓰기)
                    fos = new FileOutputStream(file, false);

                    int intMonthlyTotalCal = (int) monthlyTotalCal;
                    int intMonthlyExeCal = (int) monthlyExeCal;
                    int intMonthlyDistanceM = (int) monthlyDistance;

                    values[0] = currentYear;
                    values[1] = String.valueOf(intCurrentMonth);
                    values[2] = utcOffsetAndCountry;
                    values[3] = String.valueOf(monthlyAllStep);
                    values[4] = String.valueOf(intMonthlyDistanceM);
                    values[5] = String.valueOf(intMonthlyTotalCal);
                    values[6] = String.valueOf(intMonthlyExeCal);
                    values[7] = String.valueOf(monthlyArrCnt);

                    lines.set(lines.size() - 1, String.join(",", values)); // 저장

                    // 파일에 다시 쓰기
                    for (String writeLine : lines) {
                        fos.write((writeLine + "\n").getBytes());
                    }

                    fos.close();
                }
                else {
                    // 다른 달인 경우 (초기화)
                    fos = new FileOutputStream(file, true);
                    LocalDate today = LocalDate.of(Integer.parseInt(currentYear), Integer.parseInt(currentMonth), Integer.parseInt(currentDay)); // Here you can specify the date
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate resultDate;

                    editor.putInt("preMonthlyAllStep", 0);
                    editor.putFloat("preMonthlyDistance", 0);
                    editor.putFloat("preMonthlyTotalCal", 0);
                    editor.putFloat("preMonthlyExeCal", 0);
                    editor.putInt("preMonthlyArrCnt", 0);
                    editor.apply();

                    YearMonth preYearMonth = YearMonth.of(intPreYear, intPreMonth);
                    YearMonth currentYearMonth = YearMonth.of(intCurrentYear, intCurrentMonth);
                    long monthsBetween = ChronoUnit.MONTHS.between(preYearMonth, currentYearMonth); // 달 차이

                    for(int i = 0; monthsBetween >= i ; i++ ){
                        resultDate = today.plusMonths(1);
                        today = LocalDate.of(resultDate.getYear(), resultDate.getMonthValue(), resultDate.getDayOfMonth()); // Here you can specify the date

                        String csvData = resultDate.getYear() + "," + resultDate.getMonthValue() +"," + utcOffsetAndCountry + "," + "0" + ","  + "0" + "," + "0" + "," + "0" + "," + "0" + "\n";
                        fos.write(csvData.getBytes());
                    }

                    fos.close();

                }
            }
            else {
                // 파일이 없는 경우
                fos = new FileOutputStream(file, true);
                while(true){
                    String csvData = currentYear + "," + month + "," + utcOffsetAndCountry + "," + "0" + ","  + "0" + "," + "0" + "," + "0" + "," + "0" + "\n";
                    fos.write(csvData.getBytes());
                    month++;

                    if (month == intCurrentMonth +1)
                        break;
                }
                fos.close();
            }

            fos.close();

        }catch (IOException e) {
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

    public void sendArrAction(){
        new Thread(new Runnable() {
            @Override
            public void run() {
//                sendArrDataAsCsv();
            }
        }).start();
    }

    public void ecgAction() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                saveEcgDataAsCsv();
                sendEcgDataAsCsv();
                ecgPacketList.delete(0, ecgPacketList.length());
                ecgPacketCnt = 0;
            }
        }).start();
    }

    public void ecgList(String ecg){
        ecgPacketList.append(ecg);
//        Log.e("ecgPacketList", String.valueOf(ecgPacketList));
    }

    public void initVar() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(safeGetActivity());
        SharedPreferences.Editor editor = sharedPref.edit();

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

        for (int i = 0; i < bluetoothData.length; i++) {
            bluetoothData[i] = 0;
//            lVMin = 0;
//            lVMax = 0;
//            lVCnt = 0;
//            lVSum = 0;
//            lVAvg = 0;
        }

        // 현재 시간
        long now = System.currentTimeMillis();
        Date current_Date = new Date(now);

        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat hour = new SimpleDateFormat("HH");

        String stringCurrentDate = date.format(current_Date);
        String stringCurrentHour = hour.format(current_Date);

        // 이전 시간
        pre_date_pause = sharedPref.getString("preDate", "2023-01-01");
        pre_time_Hour = sharedPref.getString("preHour", "0");

//        String test = sharedPref.getString("testDate", "2023-07-28");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate currentDate = LocalDate.parse(stringCurrentDate, formatter);
        LocalDate preDate = LocalDate.parse(pre_date_pause, formatter);

        Log.e("stringCurrentHour", stringCurrentHour);
        Log.e("pre_time_Hour", pre_time_Hour);

        // home 화면에 보여지는 변수
        // 날짜 비교
        if (currentDate.isEqual(preDate)) {
            // 날짜가 같은 경우(기존 데이터 유지)
            Log.d("같음", "같음");
            Log.d("pre_date_pause", pre_date_pause);

            String sdistance = sharedPref.getString("distance", "0");
            String sdCal = sharedPref.getString("cal", "0");
            String sdExeCal = sharedPref.getString("execal", "0");
            String sallstep = sharedPref.getString("allstep", "0");
            String sarrCnt = sharedPref.getString("arr", "0");

            distance = Double.parseDouble(sdistance);
            dCal = Double.parseDouble(sdCal);
            dExeCal = Double.parseDouble(sdExeCal);
            allstep = Integer.parseInt(sallstep);
            arrCnt = Integer.parseInt(sarrCnt);

            hourlyArrCnt = sharedPref.getInt("hourlyArrCnt", 0);
            hourlyAllstep = sharedPref.getInt("hourlyAllStep", 0);
            hourlyDistance = sharedPref.getFloat("hourlyDistance", 0);
            hourlyTotalCal = sharedPref.getFloat("hourlyTotalCal", 0);
            hourlyExeCal = sharedPref.getFloat("hourlyExeCal", 0);

            preMonthlyAllStep = sharedPref.getInt("preMonthlyAllStep", 0);
            preMonthlyDistance = sharedPref.getFloat("preMonthlyDistance", 0);
            preMonthlyTotalCal = sharedPref.getFloat("preMonthlyTotalCal", 0);
            preMonthlyExeCal = sharedPref.getFloat("preMonthlyExeCal", 0);
            preMonthlyArrCnt = sharedPref.getInt("preMonthlyArrCnt", 0);

            if(!stringCurrentHour.equals(pre_time_Hour)){
                Log.e("시간 다름", "!stringCurrentHour.equals(pre_time_Hour)");

                editor.putInt("hourlyArrCnt", arrCnt);
                editor.putInt("hourlyAllStep", allstep);
                editor.putFloat("hourlyDistance", (float) (distance / 100));
                editor.putFloat("hourlyTotalCal", (float) dCal);
                editor.putFloat("hourlyExeCal", (float) dExeCal);

                editor.apply();

                hourlyArrCnt = sharedPref.getInt("hourlyArrCnt", 0);
                hourlyAllstep = sharedPref.getInt("hourlyAllStep", 0);
                hourlyDistance = sharedPref.getFloat("hourlyDistance", 0);
                hourlyTotalCal = sharedPref.getFloat("hourlyTotalCal", 0);
                hourlyExeCal = sharedPref.getFloat("hourlyExeCal", 0);
            }
            else{
                Log.e("시간 같음", "stringCurrentHour.equals(pre_time_Hour)");
            }

        } else {
            Log.d("pre_date_pause", pre_date_pause);

            /* Monthly 저장 */
            String sdistance = sharedPref.getString("distance", "0");
            String sdCal = sharedPref.getString("cal", "0");
            String sdExeCal = sharedPref.getString("execal", "0");
            String sallstep = sharedPref.getString("allstep", "0");
            String sarrCnt = sharedPref.getString("arr", "0");

            distance = Double.parseDouble(sdistance);
            dCal = Double.parseDouble(sdCal);
            dExeCal = Double.parseDouble(sdExeCal);
            allstep = Integer.parseInt(sallstep);
            arrCnt = Integer.parseInt(sarrCnt);

            preMonthlyAllStep = sharedPref.getInt("preMonthlyAllStep", 0);
            preMonthlyDistance = sharedPref.getFloat("preMonthlyDistance", 0);
            preMonthlyTotalCal = sharedPref.getFloat("preMonthlyTotalCal", 0);
            preMonthlyExeCal = sharedPref.getFloat("preMonthlyExeCal", 0);
            preMonthlyArrCnt = sharedPref.getInt("preMonthlyArrCnt", 0);

            monthlyAllStep = preMonthlyAllStep + allstep;
            monthlyDistance = preMonthlyDistance + realDistanceM;
            monthlyTotalCal = preMonthlyTotalCal + dCal;
            monthlyExeCal = preMonthlyExeCal + dExeCal;
            monthlyArrCnt = preMonthlyArrCnt + arrCnt;

            // 기존 값 저장
            editor.putInt("preMonthlyAllStep", monthlyAllStep);
            editor.putFloat("preMonthlyDistance", (float) monthlyDistance);
            editor.putFloat("preMonthlyTotalCal", (float) monthlyTotalCal);
            editor.putFloat("preMonthlyExeCal", (float) monthlyExeCal);
            editor.putInt("preMonthlyArrCnt", monthlyArrCnt);

            // 날짜가 다른 경우(초기화)
            editor.putString("distance", "0");
            editor.putString("cal", "0");
            editor.putString("execal", "0");
            editor.putString("allstep", "0");
            editor.putString("arr", "0");

            editor.putInt("hourlyArrCnt", 0);
            editor.putInt("hourlyAllStep", 0);
            editor.putFloat("hourlyDistance", 0);
            editor.putFloat("hourlyTotalCal", 0);
            editor.putFloat("hourlyExeCal", 0);


            editor.apply();

            hourlyArrCnt = sharedPref.getInt("hourlyArrCnt", 0);
            hourlyAllstep = sharedPref.getInt("hourlyAllStep", 0);
            hourlyDistance = sharedPref.getFloat("hourlyDistance", 0);
            hourlyTotalCal = sharedPref.getFloat("hourlyTotalCal", 0);
            hourlyExeCal = sharedPref.getFloat("hourlyExeCal", 0);
//            hourlyDistanceKM = 0;
//            hourlyDistanceM = 0;

            distance = 0;
            dCal = 0;
            dExeCal = 0;
            allstep = 0;
            arrCnt = 0;
        }

        // 시간 저장
        editor.putString("preDate", stringCurrentDate);
        editor.putString("preHour", stringCurrentHour);
        editor.apply();
    }

    // 데이터 처리
    public void doBufProcess(byte[] buf) {
//        Log.d("doBufProcess", Arrays.toString(buf));
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

                    saveEcgDataAsCsv();

                    ecgPacketList.append(Arrays.toString(ecgPacket));
                    ecgPacketCnt++;

                    if (ecgPacketCnt == 10){
//                        Log.e("ecgPacketList", ecgPacketList.toString());
                        ecgAction();
                    }

                    // step = buf[4]
                    int step = (int) buf[4] & 0xFF;

                    if (step >= 14)
                        step -= 14;

                    if (step >= 10) {
                        String notiString = null;

                        if ((step / 10) == 1) {
                            arr = 1;
                            arrCnt += 1;
                            arrStatus = "arr";
                            String larr = String.valueOf(arr);
                            Log.d("arr", larr);

                            arrAction();

                            if (setArrNotiFlag) {
                                notiString = "arr";
                            }
                        }
                        if ((step / 10) == 2) {
                            HeartAttack = 1;
                            HeartAttackFlag = 1;
                            String lHeartAttack = String.valueOf(HeartAttack);
                            Log.d("HeartAttack", lHeartAttack);

                            if (setHeartAttackNotiFlag && notificationsPermissionCheck) {
                                safeGetActivity().runOnUiThread(() -> heartAttackEvent());
                            }
                        }
                        if ((step / 10) == 3) {
                            noncontact = 1;
                            noncontactCount += 1;
                            noncontactFlag = true;
                            String pnoncontact = String.valueOf(noncontact);
                            Log.d("noncontact", pnoncontact);

                            if (nonContactCheck && setNonContactNotiFlag) {
                                // 10초에 한 번씩만 알림
                                notiString = "nonContact";
                                nonContactCheck = false;
                            }

                            // 파형 안정화
                            if (noncontactCount >= 10){
                                sendTx("c\n");
                                noncontactCount = 0;
                            }

                        }

                        if ((step / 10) == 4) {
                            myo = 1;
                            myoFlag = 1;
                            String pmyo = String.valueOf(myo);
                            Log.d("myo", pmyo);

                            if (myoCheck && setMyoNotiFlag) {
                                notiString = "myo";
                                myoCheck = false;
                            }
                        }

                        if ((step / 10) == 5) {
                            arr = 1;
                            tarchycardia = 1;
                            arrCnt++;
                            arrStatus = "fast";

                            arrAction();


                            if (setTarchycardiaNotiFlag) {
                                notiString = "tarchycardia";
                            }
                        }
                        if ((step / 10) == 6) {

                            if (bluetoothData[1] > 3) {
                                arr = 1;
                                bradycardia = 1;
                                arrCnt++;
                                arrStatus = "slow";

                                arrAction();



                                if (setBradycardiaNotiFlag) {
                                    notiString = "bradycardia";
                                }
                            }
                        }
                        if ((step / 10) == 7) {
                            arr = 1;
                            atrialFibrillaion = 1;
                            arrCnt++;
                            arrStatus = "irregular";

                            arrAction();


                            if (setAtrialFibrillaionNotiFlag) {
                                notiString = "atrialFibrillaion";
                            }
                        }

                        // notifications
                        if (notificationsPermissionCheck && notiString != null) {
                            sendNotification(notiString);
                            playSound(notiString);
                        }

                        // Arr noti
                        if (arr == 1) {
                            switch (arrCnt) {
                                case 50:
                                    notificationId += 1;
                                    sendNotification("50");
                                    notificationId -= 1;
                                    break;
                                case 100:
                                    notificationId += 2;
                                    sendNotification("100");
                                    notificationId -= 2;
                                    break;
                                case 300:
                                    notificationId += 3;
                                    sendNotification("300");
                                    notificationId -= 3;
                                    break;
                                case 600:
                                    notificationId += 4;
                                    sendNotification("600");
                                    notificationId -= 4;
                                    break;
                            }
                        }
                    }
                    else {
                        arr = 0;
                        HeartAttack = 0;
                        HeartAttackFlag = 0;
                        noncontact = 0;
                        noncontactFlag = false;
                        myo = 0;
                        myoFlag = 0;

                        tarchycardia = 0;
                        bradycardia = 0;
                        atrialFibrillaion = 0;

                    }

                    step = step % 10;

                    // step = buf[4]
                    bluetoothData[3] += step;
                    nowstep += step;
                    allstep += step;
                    allstep10s += step;

                    // 운동중
                    wallstep += step;

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

                    while (ds.getEntryCount() > chartxcnt) {
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

//            if (isSaveRaw) {
//                addJsonData();
//            }

        } catch (Exception ignored) {
        }
    }

    public void doLoop() {
        if (thread == null || !thread.isAlive()) {
            thread = new Thread(new Runnable() {

                long lTick = System.currentTimeMillis();
                long lTick100ms = System.currentTimeMillis();
                long lTick500ms = System.currentTimeMillis();
                long lTick5s = System.currentTimeMillis();
                long lTick1s = System.currentTimeMillis();
                long lTick10s = System.currentTimeMillis();

                long lTick1m = System.currentTimeMillis();
                long lTick10m = System.currentTimeMillis();

                @SuppressLint("MissingPermission")
                @Override
                public void run() {
                    isRun = true;
                    //1초마다 벨류값 1씩 증가시키는 스레드임

                    while ((isRun)) {
                        long lNow = System.currentTimeMillis();

                        if (lNow - lTick >= 1000) {
                            lTick = lNow;

                            dps = dpscnt;
                            dpscnt = 0;

                            //------------------------------
                            // calc cal
                            if (realBPM > 50) {
                                iCalTimer++;
                            } else {
                                iCalTimer = 0;
                            }
                            if (iCalTimer >= 2) {
                                iCalTimeCount++;

                                dCalMinU = iCalTimeCount / 60.0;
                                iCalTimeCount = 0;

                                if (iGender == 1) {
                                    dCal = dCal + ((iAge * 0.2017) + (dWeight * 0.1988) + (realBPM * 0.6309) - 55.0969) * dCalMinU / 4.184;
                                    if (realBPM > eCalBPM) {
                                        dExeCal = dExeCal + ((iAge * 0.2017) + (dWeight * 0.1988) + (realBPM * 0.6309) - 55.0969) * dCalMinU / 4.184;
                                    } else if (iGender == 2) {
                                        dCal = dCal + ((iAge * 0.074) + (dWeight * 0.1263) + (realBPM * 0.4472) - 20.4022) * dCalMinU / 4.184;
                                        if (realBPM > eCalBPM) {
                                            dExeCal = dExeCal + ((iAge * 0.074) + (dWeight * 0.1263) + (realBPM * 0.4472) - 20.4022) * dCalMinU / 4.184;
                                        }
                                    }
                                }


                                //운동중
                                if (iGender == 1) {
                                    wdCal = wdCal + ((iAge * 0.2017) + (dWeight * 0.1988) + (realBPM * 0.6309) - 55.0969) * dCalMinU / 4.184;
                                    if (realBPM > eCalBPM) {
                                        wdExeCal = wdExeCal + ((iAge * 0.2017) + (dWeight * 0.1988) + (realBPM * 0.6309) - 55.0969) * dCalMinU / 4.184;
                                    } else if (iGender == 2) {
                                        wdCal = wdCal + ((iAge * 0.074) + (dWeight * 0.1263) + (realBPM * 0.4472) - 20.4022) * dCalMinU / 4.184;
                                        if (realBPM > eCalBPM) {
                                            wdExeCal = wdExeCal + ((iAge * 0.074) + (dWeight * 0.1263) + (realBPM * 0.4472) - 20.4022) * dCalMinU / 4.184;
                                        }
                                    }
                                }

                                dCal = Math.round(dCal * 10) / 10.0;
                                dExeCal = Math.round(dExeCal * 10) / 10.0;

                                wdCal = Math.round(wdCal * 10) / 10.0;
                                wdExeCal = Math.round(wdExeCal * 10) / 10.0;

                            }

                            //------------------------------
                            // calc cal
                            double dh = disheight;
                            avgsize = ((dh * 0.37) + (dh - 100)) / 2.0;   //  dh 사용자의 키
                            if (avgsize < 0) {
                                avgsize = 10;
                            }

                            if (realBPM < eCalBPM)     //BPMchk는 맥박
                            {
                                distance = distance + (avgsize * nowstep);   // nowstep는 걸음수  계산 값의 단위는 cm
                            } else if ((realBPM >= eCalBPM) && (realBPM < eCalBPM + 20)) {
                                distance = distance + ((avgsize + 1) * nowstep);
                            } else if ((realBPM >= eCalBPM + 20) && (realBPM < eCalBPM + 40)) {
                                distance = distance + ((avgsize + 2) * nowstep);
                            } else if ((realBPM >= eCalBPM + 40) && (realBPM < 250)) {
                                distance = distance + ((avgsize + 3) * nowstep);
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

                            realDistanceKM = distance / 100 / 1000;
                            realDistanceM = realDistanceKM * 1000;

                            nowstep = 0;

//                            handler2.post(() -> {
//                                currentTimeCheck();
//                            });
                        }

                        // 그래프
                        if (iRecvCnt > 100 || tickTime() > 200) {
                            handler2.post(() -> addEntry());
                        }

                        // 0.5
                        if (lNow - lTick500ms >= 500) {
                            handler2.post(() -> uiTick());
                            lTick500ms = lNow;
                            tickReset();
                        }

//                    // 0.1
//                    if (lNow - lTick100ms >= 100) {
//                        lTick100ms = lNow;
//                    }

                        // 1초
                        if (lNow - lTick1s >= 1000) {
                            lTick1s = lNow;

                            if(safeGetActivity() != null)
                                safeGetActivity().runOnUiThread(() -> statusCheck());

                            // 로컬 데이터 저장
//                            localDataSave();
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

                                    // wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
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
                                                    Log.e("BLE", "lost connection");
                                                    status = false;
                                                }
                                                BluetoothGattService Service = bluetoothGatt.getService(uuid_service);
                                                if (Service == null) {
                                                    Log.e("BLE", "service not found!");
                                                    status = false;
                                                }
                                                BluetoothGattCharacteristic charac = Service
                                                        .getCharacteristic(uuid_tx);
                                                if (charac == null) {
                                                    Log.e("BLE", "char not found!");
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


                        if (lNow - lTick5s >= 2000) {
                            lTick5s = lNow;
//                        uiTick5s();
                        }

                        // 10초
                        if (lNow - lTick10s >= 10000) {
                            lTick10s = lNow; // 타이머 리셋
                            nonContactCheck = true; // 10초에 한 번만 울리게끔
                            myoCheck = true;

                            tenSecondAction();

                            if (safeGetActivity() != null) {
                                safeGetActivity().runOnUiThread(() -> batReceived((int) doubleBAT));
                            }

                        }

                        // 1m
                        if (lNow - lTick1m >= 60000) {
                            lTick1m = lNow; // 타이머 리셋
                            noncontactCount = 0;
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
            thread.start();//start()붙이면 바로실행시킨다.
        }
    }


    public void sendBpmDataAsCsv() {
        String filename = "";

        String currentYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date());
        String currentMonth = new SimpleDateFormat("MM", Locale.getDefault()).format(new Date());
        String currentDay = new SimpleDateFormat("dd", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        String directoryName = "LOOKHEART/" + currentYear + "/" + currentMonth + "/" + currentDay;
        File directory = new File(safeGetActivity().getFilesDir(), directoryName);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, "BpmData.csv");
        filename = file.getAbsolutePath();
        StringBuilder msg = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                msg.append(line);
                msg.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }

//        $kind = $_POST["kind"];
//        $filename = $_POST["filename"];
//        $data = $_POST["data"];
//        $userid = $_POST["userid"];
//        $eq = $_POST["userid"];

        String getEmail = userDetailsSharedPref.getString("email", "NULL");

        try {
            OkHttpClient client = new OkHttpClient();

            // 배포 서버
//            String url = "http://db.medsyslab.co.kr:40080/lunar/msl/api_getdatasetparam.php";
            String url = "http://db.medsyslab.co.kr:40080/lunar/msl/api_execmdfile.php";
            // 개발 서버
//            String url = "http://121.153.127.222:40081/msl/api_getdata";
//            String url = "http://admin.medsyslab.co.kr:40080/lunar/msl/api_execmdfile.php";


            RequestBody formBody = new FormBody.Builder()
                    .add("kind", "BpmData")
                    .add("filename", filename)
                    .add("username", userName)
                    .add("data", msg.toString())
                    .add("userid", getEmail)
                    .add("eq", getEmail)
                    .add("bodyState", bodyStatus)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();

            Response response = client.newCall(request).execute();
            response.close();

//                Log.i("request : " + request.toString());
//                Log.i("Response : " + response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendCalcDataAsCsv() {
        String filename = "";

        String currentYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date());
        String currentMonth = new SimpleDateFormat("MM", Locale.getDefault()).format(new Date());
        String currentDay = new SimpleDateFormat("dd", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        String directoryName = "LOOKHEART/" + currentYear + "/" + currentMonth + "/" + currentDay;
        File directory = new File(safeGetActivity().getFilesDir(), directoryName);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, "CalAndDistanceData.csv");
        filename = file.getAbsolutePath();
        StringBuilder msg = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                msg.append(line);
                msg.append('\n');
            }

            br.close();

        } catch (IOException e) {
            //You'll need to add proper error handling here
        }

//        $kind = $_POST["kind"];
//        $filename = $_POST["filename"];
//        $data = $_POST["data"];
//        $userid = $_POST["userid"];
//        $eq = $_POST["userid"];

        String getEmail = userDetailsSharedPref.getString("email", "NULL");


        try {
            OkHttpClient client = new OkHttpClient();

            // 배포 서버
//            String url = "http://db.medsyslab.co.kr:40080/lunar/msl/api_getdatasetparam.php";
            String url = "http://db.medsyslab.co.kr:40080/lunar/msl/api_execmdfile.php";
            // 개발 서버
//            String url = "http://121.153.127.222:40081/msl/api_getdata";
//            String url = "http://admin.medsyslab.co.kr:40080/lunar/msl/api_execmdfile.php";

            RequestBody formBody = new FormBody.Builder()
                    .add("kind", "calandDistanceData")
                    .add("filename", filename)
                    .add("username", userName)
                    .add("data", msg.toString())
                    .add("userid", getEmail)
                    .add("eq", getEmail)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();

            Response response = client.newCall(request).execute();
            response.close();

//                Log.i("request : " + request.toString());
//                Log.i("Response : " + response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendDailyDataAsCsv() {
        String filename = "";

        String directoryName = "LOOKHEART/" + "dailyData";
        File directory = new File(safeGetActivity().getFilesDir(), directoryName);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, "dailyCalandDistanceData.csv");
        filename = file.getAbsolutePath();
        StringBuilder msg = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                msg.append(line);
                msg.append('\n');
            }

            br.close();

        } catch (IOException e) {
            //You'll need to add proper error handling here
        }

//        $kind = $_POST["kind"];
//        $filename = $_POST["filename"];
//        $data = $_POST["data"];
//        $userid = $_POST["userid"];
//        $eq = $_POST["userid"];

        String getEmail = userDetailsSharedPref.getString("email", "NULL");

        try {
            OkHttpClient client = new OkHttpClient();

            // 배포 서버
//            String url = "http://db.medsyslab.co.kr:40080/lunar/msl/api_getdatasetparam.php";
            String url = "http://db.medsyslab.co.kr:40080/lunar/msl/api_execmdfile.php";
            // 개발 서버
//            String url = "http://121.153.127.222:40081/msl/api_getdata";
//            String url = "http://admin.medsyslab.co.kr:40080/lunar/msl/api_execmdfile.php";

            RequestBody formBody = new FormBody.Builder()
                    .add("kind", "dailyCalandDistanceData")
                    .add("filename", filename)
                    .add("username", userName)
                    .add("data", msg.toString())
                    .add("userid", getEmail)
                    .add("eq", getEmail)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();

            Response response = client.newCall(request).execute();
            response.close();

//                Log.i("request : " + request.toString());
//                Log.i("Response : " + response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMonthlyDataAsCsv() {
        String filename = "";

        String currentYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date());
        String currentMonth = new SimpleDateFormat("MM", Locale.getDefault()).format(new Date());
        String currentDay = new SimpleDateFormat("dd", Locale.getDefault()).format(new Date());

        String directoryName = "LOOKHEART/" + "monthlyData";
        File directory = new File(safeGetActivity().getFilesDir(), directoryName);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, "monthlyCalAndDistanceData.csv");
        filename = file.getAbsolutePath();
        StringBuilder msg = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                msg.append(line);
                msg.append('\n');
            }

            br.close();

        } catch (IOException e) {
            //You'll need to add proper error handling here
        }

//        $kind = $_POST["kind"];
//        $filename = $_POST["filename"];
//        $data = $_POST["data"];
//        $userid = $_POST["userid"];
//        $eq = $_POST["userid"];

        String getEmail = userDetailsSharedPref.getString("email", "NULL");

        try {
            OkHttpClient client = new OkHttpClient();

            // 배포 서버
//            String url = "http://db.medsyslab.co.kr:40080/lunar/msl/api_getdatasetparam.php";
            String url = "http://db.medsyslab.co.kr:40080/lunar/msl/api_execmdfile.php";
            // 개발 서버
//            String url = "http://121.153.127.222:40081/msl/api_getdata";
//            String url = "http://admin.medsyslab.co.kr:40080/lunar/msl/api_execmdfile.php";

            RequestBody formBody = new FormBody.Builder()
                    .add("kind", "monthlyCalAndDistanceData")
                    .add("filename", filename)
                    .add("username", userName)
                    .add("data", msg.toString())
                    .add("userid", getEmail)
                    .add("eq", getEmail)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();

            Response response = client.newCall(request).execute();
            response.close();

//                Log.i("request : " + request.toString());
//                Log.i("Response : " + response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendArrDataAsCsv() {
        try{
            String filename = "";

            String directoryName = "LOOKHEART/" + currentYear + "/" + currentMonth + "/" + currentDay + "/" + "arrEcgData";
            File directory = new File(safeGetActivity().getFilesDir(), directoryName);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, "arrEcgData_" + arrCnt + ".csv");

            // 서버 전송
            filename = file.getAbsolutePath();
            StringBuilder msg = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    msg.append(line);
                    msg.append('\n');
                }

                br.close();

            } catch (IOException e) {
                //You'll need to add proper error handling here
            }

            String getEmail = userDetailsSharedPref.getString("email", "NULL");

            try {
                OkHttpClient client = new OkHttpClient();

                // 배포 서버
//                String url = "http://db.medsyslab.co.kr:40080/lunar/msl/api_getdatasetparam.php";
                String url = "http://db.medsyslab.co.kr:40080/lunar/msl/api_execmdfile.php";
                // 개발 서버
//                String url = "http://121.153.127.222:40081/msl/api_getdata";
//                String url = "http://admin.medsyslab.co.kr:40080/lunar/msl/api_execmdfile.php";

                RequestBody formBody = new FormBody.Builder()
                        .add("kind", "arrEcgData")
                        .add("filename", filename)
                        .add("username", userName)
                        .add("data", msg.toString())
                        .add("userid", getEmail)
                        .add("eq", getEmail)
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                Response response = client.newCall(request).execute();

                response.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendEcgDataAsCsv() {
        try{
            String filename = "";

            String directoryName = "LOOKHEART/" + currentYear + "/" + currentMonth + "/" + currentDay;
            File directory = new File(safeGetActivity().getFilesDir(), directoryName);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, "EcgData.csv");

            // 서버 전송
            filename = file.getAbsolutePath();

            String getEmail = userDetailsSharedPref.getString("email", "NULL");

            try {
                OkHttpClient client = new OkHttpClient();

                // 배포 서버
//                String url = "http://db.medsyslab.co.kr:40080/lunar/msl/api_getdatasetparam.php";
                String url = "http://db.medsyslab.co.kr:40080/lunar/msl/api_execmdfile.php";
                // 개발 서버
//                String url = "http://121.153.127.222:40081/msl/api_getdata";
//                String url = "http://admin.medsyslab.co.kr:40080/lunar/msl/api_execmdfile.php";

                RequestBody formBody = new FormBody.Builder()
                        .add("kind", "ecgPacket")
                        .add("filename", filename) // 아무 파일 정보 (날짜값 확인을 위함)
                        .add("username", userName)
                        .add("data", String.valueOf(ecgPacketList))
                        .add("userid", getEmail)
                        .add("eq", getEmail)
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                Response response = client.newCall(request).execute();

                response.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testt(){
        Log.d("age", String.valueOf(iAge));
        Log.d("gender", String.valueOf(iGender));
        Log.d("disheight", String.valueOf(disheight));
        Log.d("dWeight", String.valueOf(dWeight));
        Log.d("sleep", String.valueOf(sleep));
        Log.d("wakeup", String.valueOf(wakeup));

        Log.d("bpm", String.valueOf(eCalBPM));

        Log.d("setHeartAttackNotiFlag", String.valueOf(setHeartAttackNotiFlag));
        Log.d("setArrNotiFlag", String.valueOf(setArrNotiFlag));
        Log.d("setMyoNotiFlag", String.valueOf(setMyoNotiFlag));
        Log.d("setNonContactNotiFlag", String.valueOf(setNonContactNotiFlag));
        Log.d("setTarchycardiaNotiFlag", String.valueOf(setTarchycardiaNotiFlag));
        Log.d("setBradycardiaNotiFlag", String.valueOf(setBradycardiaNotiFlag));
        Log.d("setAtrialFibrillaionNotiFlag", String.valueOf(setAtrialFibrillaionNotiFlag));

    }
    private void tenSecondAction() {

//        testt();

        timeZone();

        // csv 파일 저장
        saveBpmDataAsCsv();
        saveCalAndDistanceAsCsv();
        saveDailyDataAsCsv();
        saveMonthlyDataAsCsv();
        serverTask();


        // 변수 초기화
        tenSecondBpmSum = 0;
        tenSecondAvgBPM = 0;
        tenSecondAvgBpmCnt = 0;
        tenSecondAvgHRV = 0;
        tenSecondHRVSum = 0;
        tenSecondAvgTemp = 0;
        tenSecondTempSum = 0;

        allstep10s = 0;
    }

    private void tenMinuteAction() {
        tenMinuteServerTask();
        // 변수 초기화
        tenMinuteBpmSum = 0;
        tenMinuteAvgBPM = 0;
        tenMinuteAvgBpmCnt = 0;
    }

    public void serverTask(){
        new Thread(new Runnable() {
            @Override
            public void run() {
//                Log.e("serverTask", "serverTask");
                // 서버 전송
//                sendBpmDataAsCsv();
//                sendCalcDataAsCsv();
//                sendDailyDataAsCsv();
//                sendMonthlyDataAsCsv();
            }
        }).start();
    }

    public void tenMinuteServerTask(){
        new Thread(new Runnable() {
            @Override
            public void run() {
//                Log.e("tenMinuteServerTask", "tenMinuteServerTask");
                // 서버 전송
//                sendDailyDataAsCsv();
//                sendMonthlyDataAsCsv();
            }
        }).start();
    }

    public void currentTimeCheckTread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    currentTimeCheck();
                    localDataSave();

                    try {
                        Thread.sleep(1000);  // 1초(1000ms) 대기
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

    public void toast(String msg) {
        Toast toast = Toast.makeText(safeGetActivity(), msg, Toast.LENGTH_LONG);
        toast.show();
    }

    public void uiProfileLoad() {
        try {

            String bGender = userDetailsSharedPref.getString("gender", "남자");
            if (bGender.equals("남자"))
                iGender = 1;
            else
                iGender = 2;

            int sAge = userDetailsSharedPref.getInt("age", 40);
            iAge = sAge;

            String name = userDetailsSharedPref.getString("name", "관리자");
            userName = name;

            String sWeight = userDetailsSharedPref.getString("weight", "60");
            dWeight = Integer.valueOf(sWeight);

            String sHeight = userDetailsSharedPref.getString("height", "170");
            disheight = Integer.valueOf(sHeight);

            String sSleep = userDetailsSharedPref.getString("sleep1", "23");
            sleep = Integer.parseInt(sSleep);

            String sWakeup = userDetailsSharedPref.getString("sleep2", "7");
            wakeup = Integer.parseInt(sWakeup);

//            Log.d("iAge", String.valueOf(iAge));
//            Log.d("dWeight", String.valueOf(dWeight));
//            Log.d("disheight", String.valueOf(disheight));
//            Log.d("sleep", String.valueOf(sleep));
//            Log.d("wakeup", String.valueOf(wakeup));

        } catch (Exception ignored) {
            ignored.printStackTrace();
            Log.e("uiProfileLoad", "Error loading profile", ignored);
        }
    }

    public void uiSettingLoad() {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(safeGetActivity() /* Activity context */);
            SharedPreferences.Editor editor = sharedPref.edit();
            String sv = sharedPref.getString("chartxcnt", "");
            if (sv == "") {
                sv = "500";

                editor.putString("chartxcnt", sv);
                editor.apply();
            }
            chartxcnt = Integer.parseInt(sv);

            String ev = sharedPref.getString("o_bpm", "");

//            if (ev == "") {
//                ev = "90";
//
//                editor.putString("eCalBPM", ev);
//                editor.apply();
//            }
            eCalBPM = Integer.parseInt(ev);
//            Log.d("eCalBPM", String.valueOf(eCalBPM));
/*
            sv = sharedPref.getString("arrcnt", "0");
            arrCnt = Integer.parseInt(sv);*/

            sv = sharedPref.getString("arrtime", "0");
            arrTime = sv;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void chartInit() {
        chart = (LineChart) view.findViewById(R.id.myChart);

        chart.setDrawGridBackground(true);
        chart.setBackgroundColor(Color.WHITE);
        chart.setGridBackgroundColor(Color.WHITE);

// description text
        chart.getDescription().setEnabled(false);
//        Description des = chart.getDescription();
//        des.setEnabled(true);
//        des.setText("");
//        des.setTextSize(15f);
//        des.setTextColor(Color.WHITE);

// touch gestures (false-비활성화)
        chart.setTouchEnabled(false);

// scaling and dragging (false-비활성화)
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);

//auto scale
        chart.setAutoScaleMinMaxEnabled(true);

// if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

//X축
        chart.getXAxis().setDrawGridLines(true);
        chart.getXAxis().setDrawAxisLine(false);

        chart.getXAxis().setEnabled(false);//x축 표시에 대한 함수
        chart.getXAxis().setDrawGridLines(false);

        // chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); //x축 표시에 대한 위치 설정
        Legend l = chart.getLegend();
        l.setEnabled(true);
        l.setFormSize(10f); // set the size of the legend forms/shapes
        l.setTextSize(12f);
        l.setTextColor(Color.DKGRAY);

//Y축
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

            eCal_value.setText((int) dExeCal + " Kcal");
            step_value.setText(allstep + " 걸음");
            distance_value.setText((String.format("%.3f", realDistanceKM)) + " km");
            temp_value.setText(String.format("%.1f", bluetoothData[2]) + " °C");

        } catch (Exception E) {
            Log.d("ERR", "" + E.getMessage());
        }
    }


    public void BleConnectCheck() {

        if (btAdapter == null) {
            // 장치가 블루투스를 지원하지 않는 경우.
//            FragmentActivity activity = this;
            Toast.makeText(safeGetActivity(), "블루투스 미지원 기기입니다.", Toast.LENGTH_LONG).show();
        } else {

            // 특정 버전 이상(LOLLIPOP) 저전력 스캐너
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btScanner = btAdapter.getBluetoothLeScanner();
            }

            // 장치가 블루투스를 지원하는 경우
            if (!btAdapter.isEnabled()) {
                // 블루투스 권한 사용 요청
                Log.d("BLE", "BLE");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activityResultLauncher.launch(enableBtIntent);
            } else {
                // 블루투스 기능이 이미 활성화 되어 있는 경우
                if (safeGetActivity().checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                        safeGetActivity().checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    startScanning();
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    public void startScanning() {
        System.out.println("start Scanning");
        btScanning = true;
        deviceIndex = 0;
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
        if (mHandler != null) {
            mHandler.postDelayed(() -> stopScanning(), SCAN_PERIOD);
        }
    }

    @SuppressLint("MissingPermission")
    public void stopScanning() {
        btScanning = false;
        AsyncTask.execute(() -> btScanner.stopScan(leScanCallback));

    }

    // 블루투스 스캔
    // BLE 디바이스 스캔의 결과를 처리하는 콜백 메소드를 포함하는 추상 클래스
    private ScanCallback leScanCallback = new ScanCallback() {

        // onScanResult 메소드는 Bluetooth 스캐너가 새로운 BLE 디바이스를 발견할 때마다 호출
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            // 발견된 디바이스를 devicesDiscovered 리스트에 추가
            devicesDiscovered.add(result.getDevice());
            deviceIndex++;

            // 발견 디바이스의 이름을 가지고 비교
            @SuppressLint("MissingPermission") String name = result.getDevice().getName();
            if (name != null) {
                if (name.equals("KHJ")) {
                    // 연결 시도
                    if(safeGetActivity() != null) {
                        bluetoothGatt = result.getDevice().connectGatt(safeGetActivity().getApplicationContext(), true, btleGattCallback);
                        BluetoothDevice device = bluetoothGatt.getDevice();
                        deviceMacAddress = device.getAddress();
                        Log.e("macAddress", deviceMacAddress);

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
                if (ECGFound == false)
                    ECGFound = true;

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
            System.out.println(newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {

                mConnectionState = STATE_CONNECTED;
                Log.i("BLE", "GATT 서버 연결");
                Log.i("BLE", "서비스 검색 시작");

                bluetoothGatt.discoverServices();

                stopScanning();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectionState = STATE_DISCONNECTED;
                Log.i("BLE", "GATT 서버에서 연결이 끊어졌다");

                try {
                    synchronized (lTx) {
                        lTx.clear();
                    }
                    mHandler.postDelayed(() -> startScanning(), 1000);
                } catch (Exception E) {

                }
            }
        }

        // GATT 서비스가 발견될 때 호출
        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

                try {
                    mBluetoothGattService = gatt.getService(uuid_service);

                    BluetoothGattCharacteristic receiveCharacteristic =
                            mBluetoothGattService.getCharacteristic(uuid_recieve);

                    if (receiveCharacteristic != null) {
                        BTConnected = true;
                        ECGFound = true;

                        BluetoothGattDescriptor receiveConfigDescriptor =
                                receiveCharacteristic.getDescriptor(uuid_config);
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
//            Toast.makeText(context, "받은 액션 : "+action , Toast.LENGTH_SHORT).show();
//            Log.d("Bluetooth action", action);
            final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String macAddress = device.getAddress();

            //입력된 action에 따라서 함수를 처리한다
            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED: //블루투스의 연결 상태 변경
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            Log.v("BLE_STATE", "STATE_OFF");

                            if (deviceMacAddress.equals(macAddress)) {
                                useBleFlag = false;
                                stopThread();
                            }

                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Log.v("BLE_STATE", "STATE_TURNING_OFF");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Log.v("BLE_STATE", "STATE_ON");

                            if (deviceMacAddress.equals(macAddress)) {
                                if (safeGetActivity().checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                                        safeGetActivity().checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
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

                        initVar();

                        if (thread == null) {
                            Log.e("doLoop", "doLoop");
                            doLoop();
                        }
                        else{
                            Log.e("restartThread", "restartThread");
                            restartThread();
                        }

//                        handler.post(serverTask);
//                        handler.post(tenMinuteServerTask);
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

//                        handler.removeCallbacks(serverTask);
//                        handler.removeCallbacks(tenMinuteServerTask);
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

    // 알림 권한
    private ActivityResultLauncher<String> requestPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    if (isGranted) {
//                        Toast.makeText(safeGetActivity()(), "알림 권한 허가", Toast.LENGTH_SHORT).show();
                        notificationsPermissionCheck = true;
                        createNotificationChannel();
                    } else {
//                        Toast.makeText(safeGetActivity()(), "알림 권한 거부", Toast.LENGTH_SHORT).show();
                        notificationsPermissionCheck = false;
                    }
                }
            });


    // Android API 30 and above requires setting the backgroundPermission directly
    private void requestBackgroundPermission() {
        ActivityCompat.requestPermissions(safeGetActivity(),
                new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                2);
    }

    // Background permission request dialog
    private void showPermissionDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("위치 권한");
        builder.setMessage("LOOKHEART는\n앱이 닫히거나 사용 중이 아닐 때도\n위치데이터를 수집하여\n응급상황 알림 기능을 지원합니다.\n위치 권한을 항상 허용으로\n변경해주세요");

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    requestBackgroundPermission();
                }
            }
        };

        builder.setPositiveButton("확인", listener);
        builder.setNegativeButton("취소", null);

        builder.show();
    }

    public void createNotificationChannel(){

        // notification manager 생성
        notificationManager = (NotificationManager)safeGetActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, "Test", notificationManager.IMPORTANCE_HIGH);

        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        notificationChannel.setDescription("Notification");

        notificationManager.createNotificationChannel(notificationChannel);
    }

    public void sendNotification(String noti){
        // Builder 생성
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder(noti);
        notificationManager.notify(notificationId, notifyBuilder.build());
//        notificationId++;
    }

    private NotificationCompat.Builder getNotificationBuilder(String noti) {
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(safeGetActivity(), PRIMARY_CHANNEL_ID);

        switch (noti){
            case "arr":
                notifyBuilder.setContentTitle("비정상맥박 발생!\n");
                break;
            case "bradycardia":
                notifyBuilder.setContentTitle("느린맥박 발생!\n");
                break;
            case "tarchycardia":
                notifyBuilder.setContentTitle("빠른맥박 발생!\n");
                break;
            case "atrialFibrillaion":
                notifyBuilder.setContentTitle("연속적인 비정상맥박!\n");
                break;
            case "myo":
                notifyBuilder.setContentTitle("근전도 발생!\n");
                break;
            case "nonContact":
                notifyBuilder.setContentTitle("전극떨어짐 발생!\n");
                break;
            case "50":
                notifyBuilder.setContentTitle("오늘 비정상맥박이 50회 발생하였습니다!\n");
                notifyBuilder.setContentText("조금 피곤하지는 않으신가요?");
                notifyBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
                return notifyBuilder;
            case "100":
                notifyBuilder.setContentTitle("오늘 비정상맥박이 100회 발생하였습니다!\n");
                notifyBuilder.setContentText("잠깐 휴식시간을 가져보시는게 어떨까요?");
                notifyBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
                return notifyBuilder;
            case "300":
                notifyBuilder.setContentTitle("오늘 비정상맥박이 300회 발생하였습니다!\n");
                notifyBuilder.setContentText("안정을 취하셔야 합니다.");
                notifyBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
                return notifyBuilder;
            case "600":
                notifyBuilder.setContentTitle("오늘 비정상맥박이 600회 발생하였습니다!\n");
                notifyBuilder.setContentText("전문가와 상담이 필요합니다.");
                notifyBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
                return notifyBuilder;
            default:
                return notifyBuilder;
        }

        notifyBuilder.setContentText("시간 : "+currentTime);
        notifyBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        return notifyBuilder;
    }

    private void heartAttackEvent() {

        if(!HeartAttackCheck) {

            HeartAttackCheck = true; // 중첩 알림이 안뜨게 하는 flag

            // 시스템 사운드 재생
            MediaPlayer mediaPlayer = MediaPlayer.create(safeGetActivity(), R.raw.heartattacksound); // res/raw 폴더에 사운드 파일을 넣어주세요
            mediaPlayer.setLooping(true); // 반복
            mediaPlayer.start();

            // 알림 대화상자 표시
            AlertDialog.Builder builder = new AlertDialog.Builder(safeGetActivity(), R.style.AlertDialogTheme);

            View v = LayoutInflater.from(safeGetActivity()).inflate(R.layout.heartattack_dialog, (LinearLayout)view.findViewById(R.id.layoutDialog));

            builder.setView(v);

            ((TextView)v.findViewById(R.id.heartattack_title)).setText("응급 상황");
            ((TextView)v.findViewById(R.id.textMessage)).setText("10초 안에 확인 확인 버튼을 누르지 않으면 보호자에게 메시지가 전송됩니다");
            ((TextView)v.findViewById(R.id.btnOk)).setText("확인");

            AlertDialog alertDialog = builder.create();

            v.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
//                    mediaPlayer.stop();
                    mediaPlayer.release();
                    HeartAttackCheck = false;

                    sendTx("c\n");

                    alertDialog.dismiss();
                }
            });

            // 다이얼로그 형태 지우기
            if (alertDialog.getWindow() != null ) {
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            alertDialog.show();
        }
    }

    public void playSound(String type){
        AudioManager audioManager = (AudioManager) safeGetActivity().getSystemService(Context.AUDIO_SERVICE);

        MediaPlayer mediaPlayer = null;

        switch (type){
            case "arr":
                mediaPlayer = MediaPlayer.create(safeGetActivity(), R.raw.arr);
                break;
            case "bradycardia":
                mediaPlayer = MediaPlayer.create(safeGetActivity(), R.raw.bradycardia);
                break;
            case "tarchycardia":
                mediaPlayer = MediaPlayer.create(safeGetActivity(), R.raw.tarchycardia);
                break;
            case "atrialFibrillaion":
                mediaPlayer = MediaPlayer.create(safeGetActivity(), R.raw.atrialfibrillaion);
                break;
            case "myo":
                mediaPlayer = MediaPlayer.create(safeGetActivity(), R.raw.myo);
                break;
            case "nonContact":
                mediaPlayer = MediaPlayer.create(safeGetActivity(), R.raw.noncontact);
                break;
        }

        if (audioManager != null && (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT || audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)) {
            // 무음 또는 진동 모드일 때
            // mediaPlayer를 시작하지 않음
        } else {
            // 소리 모드일 때
            if(mediaPlayer != null) {
                mediaPlayer.start();
            }
        }

        MediaPlayer finalMediaPlayer = mediaPlayer;

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (finalMediaPlayer != null) {
                    finalMediaPlayer.release();
                }
            }
        }, 3000);  // 3초 (3000ms)
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


    public void ecgChange() {

        boolean ecgCheck = userDetailsSharedPref.getBoolean("e_ecg", true);

        if(ecgCheck) {
            // peak
            sendTx("p\n");
        }else{
            // ecg
            sendTx("e\n");
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

    public void targetCheck() {
        try {
            String sBpm = userDetailsSharedPref.getString("o_bpm", "90");
            eCalBPM = Integer.valueOf(sBpm);

//            Log.d("eCalBPM", String.valueOf(eCalBPM));
            userDetailsEditor.putBoolean("profile3check", false);
            userDetailsEditor.apply();
        } catch (Exception ignored) {
        }
    }

    public void notiCheck() {
        try {

            boolean heartAttack = userDetailsSharedPref.getBoolean("s_emergency", true);
            setHeartAttackNotiFlag = heartAttack;

            boolean arr = userDetailsSharedPref.getBoolean("s_arr", true);
            setArrNotiFlag = arr;

            boolean myo = userDetailsSharedPref.getBoolean("s_muscle", false);
            setMyoNotiFlag = myo;

            boolean nonContact = userDetailsSharedPref.getBoolean("s_drop", true);
            setNonContactNotiFlag = nonContact;

            boolean tarchycardia = userDetailsSharedPref.getBoolean("s_fastarr", false);
            setTarchycardiaNotiFlag = tarchycardia;

            boolean bradycardia = userDetailsSharedPref.getBoolean("s_slowarr", false);
            setBradycardiaNotiFlag = bradycardia;

            boolean atrialFibrillaion = userDetailsSharedPref.getBoolean("s_irregular", false);
            setAtrialFibrillaionNotiFlag = atrialFibrillaion;

            userDetailsEditor.putBoolean("profile2check", false);
            userDetailsEditor.apply();

//            Log.d("setNoti", String.valueOf(notificationsPermissionCheck));
//            Log.d("setHeartAttackNotiFlag", String.valueOf(setHeartAttackNotiFlag));
//            Log.d("setArrNotiFlag", String.valueOf(setArrNotiFlag));
//            Log.d("setMyoNotiFlag", String.valueOf(setMyoNotiFlag));
//            Log.d("setNonContactNotiFlag", String.valueOf(setNonContactNotiFlag));
//            Log.d("setTarchycardiaNotiFlag", String.valueOf(setTarchycardiaNotiFlag));
//            Log.d("setBradycardiaNotiFlag", String.valueOf(setBradycardiaNotiFlag));
//            Log.d("setAtrialFibrillaionNotiFlag", String.valueOf(setAtrialFibrillaionNotiFlag));

        } catch (Exception ignored) {
        }
    }

    public void statusCheck(){
        int intCurrentHour = Integer.parseInt(currentHour);

        if(eCalBPM <= intRealBPM || allstep10s > 6){
            // 활동중
            bodyStatus = "E";

            exerciseBackground.setBackground(ContextCompat.getDrawable(safeGetActivity(), R.drawable.rest_round_press));
            exerciseText.setTextColor(Color.WHITE);
            exerciseImg.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

            restBackground.setBackgroundColor(Color.TRANSPARENT);
            restText.setTextColor(ContextCompat.getColor(safeGetActivity(), R.color.lightGray));
            restImg.setColorFilter(ContextCompat.getColor(safeGetActivity(), R.color.lightGray));

            sleepBackground.setBackgroundColor(Color.TRANSPARENT);
            sleepText.setTextColor(ContextCompat.getColor(safeGetActivity(), R.color.lightGray));
            sleepImg.setColorFilter(ContextCompat.getColor(safeGetActivity(), R.color.lightGray));

        }
        else if( ( sleep < intCurrentHour || wakeup > intCurrentHour  ) && allstep10s < 6){
            // 수면중
            bodyStatus = "S";

            exerciseBackground.setBackgroundColor(Color.TRANSPARENT);
            exerciseText.setTextColor(ContextCompat.getColor(safeGetActivity(), R.color.lightGray));
            exerciseImg.setColorFilter(ContextCompat.getColor(safeGetActivity(), R.color.lightGray));

            restBackground.setBackgroundColor(Color.TRANSPARENT);
            restText.setTextColor(ContextCompat.getColor(safeGetActivity(), R.color.lightGray));
            restImg.setColorFilter(ContextCompat.getColor(safeGetActivity(), R.color.lightGray));

            sleepBackground.setBackground(ContextCompat.getDrawable(safeGetActivity(), R.drawable.rest_round_press));
            sleepText.setTextColor(Color.WHITE);
            sleepImg.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        }
        else{
            // 휴식중
            bodyStatus = "R";

            exerciseBackground.setBackgroundColor(Color.TRANSPARENT);
            exerciseText.setTextColor(ContextCompat.getColor(safeGetActivity(), R.color.lightGray));
            exerciseImg.setColorFilter(ContextCompat.getColor(safeGetActivity(), R.color.lightGray));

            restBackground.setBackground(ContextCompat.getDrawable(safeGetActivity(), R.drawable.rest_round_press));
            restText.setTextColor(Color.WHITE);
            restImg.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

            sleepBackground.setBackgroundColor(Color.TRANSPARENT);
            sleepText.setTextColor(ContextCompat.getColor(safeGetActivity(), R.color.lightGray));
            sleepImg.setColorFilter(ContextCompat.getColor(safeGetActivity(), R.color.lightGray));

        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onDestroy() {
        super.onDestroy();

        stopScanning();
        stopThread();

//        handler.removeCallbacks(serverTask);
//        handler.removeCallbacks(tenMinuteServerTask);
        // 블루투스 연결 활성화 시 연결 해제
        try {
            if (bluetoothGatt != null) {
                bluetoothGatt.disconnect(); // gatt 클라이언트에서 gatt 서버 연결 해제
                bluetoothGatt.close(); // 클라이언트, 리소스 해제
            }
        } catch (Exception ignored) {
        }

        if (serviceIntent != null) {

            stopService();

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }

        safeGetActivity().finish();
        safeGetActivity().finishAffinity();
    }


    public void stopThread() {
        if (thread != null)
            thread.interrupt();
        isRun = false;
    }

    public void restartThread() {
        stopThread();
        isRun = true;
        doLoop();
    }

    public String searchDay(int year, int month, int day){
        LocalDate specificDate = LocalDate.of(year, month, day);
        DayOfWeek dayOfWeek = specificDate.getDayOfWeek();
        String dayOfTheWeek = "";

        switch (dayOfWeek) {
            case MONDAY:
                dayOfTheWeek = "Mon";
                break;
            case TUESDAY:
                dayOfTheWeek = "Tue";
                break;
            case WEDNESDAY:
                dayOfTheWeek = "Wed";
                break;
            case THURSDAY:
                dayOfTheWeek = "Thu";
                break;
            case FRIDAY:
                dayOfTheWeek = "Fri";
                break;
            case SATURDAY:
                dayOfTheWeek = "Sat";
                break;
            case SUNDAY:
                dayOfTheWeek = "Sun";
                break;
        }
        return dayOfTheWeek;
    }

    public void timeZone(){
        // 국가 코드
        Locale current = safeGetActivity().getResources().getConfiguration().getLocales().get(0);
        currentCountry = current.getCountry();

        // 현재 시스템의 기본 타임 존
        TimeZone currentTimeZone = TimeZone.getDefault();

        // 타임 존의 아이디
        String timeZoneId = currentTimeZone.getID();

        ZoneId zoneId = ZoneId.of(timeZoneId);
        ZoneOffset offset = LocalDateTime.now().atZone(zoneId).getOffset();

        String utcTime = String.valueOf(offset);
//        Log.e("utcTime", utcTime);
//        String[] utcOffsetArray = utcTime.split(":");

        String firstChar = String.valueOf(utcTime.charAt(0));
//        int utcHour = Integer.parseInt(utcOffsetArray[0]);

        if (firstChar.equals("+") || firstChar.equals("-")){
            utcOffset = utcTime;
        }
        else {
            utcOffset = "+" + utcTime;
        }

        utcOffsetAndCountry = utcOffset + "/" + currentCountry;

    }

    private FragmentActivity safeGetActivity() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Log.e("HomeFragment", "Fragment is not attached to an activity.");
            return null;
        }
        return activity;
    }
}
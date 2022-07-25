package info.smartinsole.sqlite.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import info.smartinsole.sqlite.R;
import info.smartinsole.sqlite.database.DatabaseHelper;
import info.smartinsole.sqlite.database.model.Datam;
import info.smartinsole.sqlite.database.model.Sesion;
import info.smartinsole.sqlite.database.model.Sole;
import info.smartinsole.sqlite.database.model.Test;
import info.smartinsole.sqlite.http_client.Api;
import info.smartinsole.sqlite.http_client.Datum;
import info.smartinsole.sqlite.http_client.PostJsonRequestQueue;
import info.smartinsole.sqlite.http_client.Session;
import info.smartinsole.sqlite.http_client.SessionModel;
import info.smartinsole.sqlite.http_client.VolleyCallback;
import info.smartinsole.sqlite.http_client.VolleySingleton;
import info.smartinsole.sqlite.login.SharedPrefManager;
import info.smartinsole.sqlite.login.User;
import info.smartinsole.sqlite.services.LeftSamplingService;
import info.smartinsole.sqlite.services.RightSamplingService;
import info.smartinsole.sqlite.services.utils.ThreadPoolManager;
import info.smartinsole.sqlite.utils.MyDividerItemDecoration;
import info.smartinsole.sqlite.utils.RecyclerTouchListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static android.os.Looper.getMainLooper;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


/**
 * Test Fragment
 * Shows a history with tests done until now (saved in SQLite testTable)
 * Add new test if floating action button pressed
 * 3 Types of Tests
 * Start a new test Random Data ara created and saved into 2 list for each sole (Right/Left)
 * Stop the test
 * if discard, throw the data collected
 * if keep, save data to local db (SQLite) AND if internet connection upload them immediately
 */

public class TestFragment extends Fragment {

    private View view;

    private static final int TUG = 1;
    private static final int BALANCE = 2;
    private static final int FREE_WALKING = 3;

    private List<String> list;
    private String tempPatID = "f399212j77";
    private String sessionID = "";

    info.smartinsole.sqlite.database.model.Sesion sessionData;


    private User prefs;

    private TextView timeDiff, test_type, no_tests, sync_txt;
    private CardView sync;
    private Button startStop, tug, balance, freeWalking;
    private View history, select_test, start_stop_test;
    private RecyclerView view_history;

    private String dataSource = "db";
    private JSONObject jsonFram;
    private String post_session_url;


    private boolean testIsStarted;

    private Date startTime, stopTime;
    private Timestamp startTimeStamp, stopTimestamp;
    private final Handler handler = new Handler(getMainLooper());
    private final Handler syncHandler = new Handler(Looper.getMainLooper());
    private final Handler soleHandler = new Handler(getMainLooper());

    private boolean st_sp = false;
    private String strTestType = "";
    private long difference_In_Seconds, difference_In_Minutes, difference_In_Hours;
    private String h, m, s;

    private ArrayList<String> objTimeStamp, objTestType, objDuration, objSync;
    protected HistoryAdapter historyAdapter;

    private JSONPacketManager jsonPacketManager;

    private MainActivity mainActivity;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    String url;
    PostJsonRequestQueue postJsonRequestQueue;
    private CountDownLatch countDownLatch;
    List<Sole> solesR, solesL;
    // boolean test_flag = true;

    //a broadcast for scan status
    public static final String BLE_SCAN_STATUS_BROADCAST_RECEIVER_INTERRUPT = "app.ScanStatus.interrupt";

    //Device Bluetooth Adapter
    private BluetoothAdapter mBluetoothAdapter;

    //Sampling Services
    private LeftSamplingService mLeftSamplingService;
    Intent intentL;
    boolean isBoundL;

    private RightSamplingService mRightSamplingService;
    Intent intentR;
    boolean isBoundR;

    // Thread Pool Manager
    private ThreadPoolManager mthreadPoolManager;

    private String LEFT_DEVICE_ADDR;
    private String Right_DEVICE_ADDR;
    private SharedPreferences spLeft;
    private SharedPreferences spRight;
    private String defaultAddress = "NO ADDRESS PROVIDED!";

    private final int fakePauseMilSec = 250;
    protected List<Sole> soleList = new ArrayList<>();

    public TestFragment() {
        // Required empty public constructor

        initVariables();
    }

    public static TestFragment newInstance() {
        return new TestFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spLeft = getContext().getSharedPreferences("LEFT_MAC_ADDRESS", MODE_PRIVATE);
        LEFT_DEVICE_ADDR = spLeft.getString("leftMac", defaultAddress);
        spRight = getContext().getSharedPreferences("RIGHT_MAC_ADDRESS", MODE_PRIVATE);
        Right_DEVICE_ADDR = spRight.getString("rightMac", defaultAddress);
        prefs = SharedPrefManager.getInstance(getContext()).getUser();


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_tests, container, false);

        mainActivity = (MainActivity) getActivity();
        jsonPacketManager = new JSONPacketManager(mainActivity);
        sync = (CardView) view.findViewById(R.id.sync);
        sync.setVisibility(View.GONE);
        sync_txt = (TextView) view.findViewById(R.id.sync_txt);

        // layout view for history  ================================================================
        history = view.findViewById(R.id.history);
        no_tests = view.findViewById(R.id.empty_tests_view);
        view_history = view.findViewById(R.id.recycler_view_history);

        // layout view for select test  ============================================================
        select_test = view.findViewById(R.id.select_test);
        tug = view.findViewById(R.id.TUG);
        balance = view.findViewById(R.id.Balance);
        freeWalking = view.findViewById(R.id.free_Walking);

        // layout view for start/stop test  ========================================================
        start_stop_test = view.findViewById(R.id.start_test);
        timeDiff = (TextView) view.findViewById(R.id.timeDiff);
        timeDiff.setTextSize(35f);
        test_type = (TextView) view.findViewById(R.id.test_type);
        startStop = (Button) view.findViewById(R.id.start_stop);

        url = getString(R.string.post_sessions_url);
        // TODO check server for processed data

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) getContext().getSystemService(Context.BLUETOOTH_SERVICE);

        // Get the BluetoothAdapter
        mBluetoothAdapter = bluetoothManager.getAdapter();

        //Binding with sampling services for the two soles
        intentL = new Intent(getContext(), LeftSamplingService.class);
        isBoundL = false;
        intentR = new Intent(getContext(), RightSamplingService.class);
        isBoundR = false;

        EnableBluetooth();

        //binding Services
        getContext().bindService(intentL, serviceConnectionLeft, Context.BIND_AUTO_CREATE);
        getContext().bindService(intentR, serviceConnectionRight, Context.BIND_AUTO_CREATE);

        // get the thread pool manager instance
        mthreadPoolManager = ThreadPoolManager.getsInstance();

        tug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStage(2, 1);
            }
        });

        balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStage(2, 2);
            }
        });

        freeWalking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStage(2, 3);
            }
        });

        startStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO: Working Version - hardcoded data
//                if(!testIsStarted){
//                   // try {
//                        startClock();
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
//
//                    testIsStarted = true;
//                }
//                else{
//                    try {
//                        stopClock();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    testIsStarted = false;
//                }

                //TODO: live data version
                LEFT_DEVICE_ADDR = spLeft.getString("leftMac", defaultAddress);
                Right_DEVICE_ADDR = spRight.getString("rightMac", defaultAddress);

                if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent =
                            new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 1);
                } else {
                    if (!st_sp) { // Start
                        if (LEFT_DEVICE_ADDR == defaultAddress && Right_DEVICE_ADDR == defaultAddress) {
                            Toast.makeText(getContext(), "No soles found, Please connect right and left sole!", Toast.LENGTH_LONG).show();
                        }
                        //else if (LEFT_DEVICE_ADDR == defaultAddress) {
                         //   Toast.makeText(getContext(), "Left sole not found, please connect a sole!", Toast.LENGTH_LONG).show();
                    //    }
                    else if (Right_DEVICE_ADDR == defaultAddress) {
                            Toast.makeText(getContext(), "Right sole not found, please connect a sole!", Toast.LENGTH_LONG).show();
                        } else {
                            fakePause(fakePauseMilSec);
                            startClock();
                        }
                    } else { // Stop
                        fakePause(fakePauseMilSec);
                        stopClock();
                    }
                }

            }
        });


        if (mainActivity.sync_all) {
            getTestBasicInfo();
            new Thread(task2).start();
            mainActivity.sync_all = false;
        } else {
            initHistory();
        }
        return view;
    }

    private void initVariables() {
        //Describe the source of sole data
        dataSource = "db";

        post_session_url = "http://83.212.76.159/api/sessions";
    }

    /**
     * Inserting new sole in db
     * and refreshing the list
     */
    protected void createSole(String sole) {
        // inserting sole in db and getting
        // newly inserted sole id
        long id = mainActivity.db.insertSoleData(sole);

        // get the newly inserted sole from db
        Sole n = mainActivity.db.getSole(id);

        if (n != null) {
            // adding new sole to array list at 0 position
            soleList.add(0, n);

            // refreshing the list
            //mAdapter.notifyDataSetChanged();

            //toggleEmptySoles();
        }
    }

    //TODO: version with live data

    /**
     * Called when start button is pressed
     */
    private void startClock() {
        //Start Bluetooth Low Energy Connection with Devices (Left Right Soles)
        //mLeftSamplingService.scanLeDevice(true);
        mRightSamplingService.scanLeDevice(true);

        st_sp = true;
        startTime = new Date();
        startTimeStamp = new Timestamp(startTime.getTime());
        // SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        startTimeStamp = Timestamp.valueOf(sdf.format(startTimeStamp));


        timeUpdater.run();


        fakePause(fakePauseMilSec);
        startCountAlert();
        startStop.setText("Stop");
    }

    //TODO: Working Version - hardcoded data
    /**
     * Called when start button is pressed
     */
//    private Timestamp startClock(){
////
//
//        //Start Bluetooth Low Energy Connection with Devices (Left Right Soles)
////        mLeftSamplingService.scanLeDevice(true);
////        mRightSamplingService.scanLeDevice(true);
////
////        st_sp = true;
//        startTime = new Date();
//        startTimeStamp = new Timestamp(startTime.getTime());
////
////        timeUpdater.run();
////
//        solesR = new ArrayList<>();
//        solesL = new ArrayList<>();
//        Thread t1 = new Thread(soleData);
//
//        //Start add data from scv
//        t1.start();
//
//
//        //Wait till
//        //t1.join();
//
////
////        fakePause(fakePauseMilSec);
////        startCountAlert();
//
//
//        return startTimeStamp;
//    }

    /**
     * For testing purpose
     * Create and  Get Random data or from a file every 20ms (50Hz)
     */
    public Runnable soleData = new Runnable() {
        @Override
        public void run() {

            if (dataSource.equals("dummy")) {
                JSONObject jsonFrameR = jsonPacketManager.generateDummySoleData("RIGHT");
                solesR.add(getFrame(jsonFrameR));
                //if (test_flag) {
                JSONObject jsonFrameL = jsonPacketManager.generateDummySoleData("LEFT");
                solesL.add(getFrame(jsonFrameL));

                //}
                //test_flag = !test_flag;
                soleHandler.postDelayed(soleData, 20);
            } else if (dataSource.equals("scv")) {
                list = read();

                jsonFram = new JSONObject();

                for (int i = 0; i < list.size() - 1; i++) {
                    String[] result;
                    result = list.get(i).split(",");

                    try {
                        jsonFram.put("id", result[0].replace("\"", ""));
                        jsonFram.put("userId", result[1].replace("\"", ""));
                        jsonFram.put("sole", result[2].replace("\"", ""));
                        jsonFram.put("timestamp", result[3].replace("\"", ""));
                        jsonFram.put("right_Left", result[4].replace("\"", ""));
                        jsonFram.put("syncStatus", result[5].replace("\"", ""));
                        jsonFram.put("acclXAxis", result[6].replace("\"", ""));
                        jsonFram.put("acclYAxis", result[7].replace("\"", ""));
                        jsonFram.put("acclZAxis", result[8].replace("\"", ""));
                        jsonFram.put("gyroRoll", result[9].replace("\"", ""));
                        jsonFram.put("gyroPitch", result[10].replace("\"", ""));
                        jsonFram.put("gyroYaw", result[11].replace("\"", ""));
                        jsonFram.put("magnXAxis", result[12].replace("\"", ""));
                        jsonFram.put("magnYAxis", result[13].replace("\"", ""));
                        jsonFram.put("magnZAxis", result[14].replace("\"", ""));
                        jsonFram.put("pe1", result[15].replace("\"", ""));
                        jsonFram.put("pe2", result[16].replace("\"", ""));
                        jsonFram.put("pe3", result[17].replace("\"", ""));
                        jsonFram.put("pe4", result[18].replace("\"", ""));
                        jsonFram.put("pe5", result[19].replace("\"", ""));
                        jsonFram.put("pe6", result[20].replace("\"", ""));
                        jsonFram.put("pe7", result[21].replace("\"", ""));
                        jsonFram.put("pe8", result[22].replace("\"", ""));
                        jsonFram.put("pe9", result[23].replace("\"", ""));
                        jsonFram.put("pe10", result[24].replace("\"", ""));
                        jsonFram.put("Pe11", result[25].replace("\"", ""));
                        jsonFram.put("Pe12", result[26].replace("\"", ""));
                        jsonFram.put("Pe13", result[27].replace("\"", ""));
                        jsonFram.put("Pe14", result[28].replace("\"", ""));
                        jsonFram.put("Pe15", result[29].replace("\"", ""));
                        jsonFram.put("Pe16", result[30].replace("\"", ""));
                        jsonFram.put("gtf", result[31].replace("\"", ""));

                        JSONObject jsonFrame = jsonFram;

                        if (result[4].equals("LEFT")) {
                            solesL.add(getFrame(jsonFrame));

                        } else {
                            solesR.add(getFrame(jsonFrame));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                soleHandler.postDelayed(soleData, 20);
            } else {
                Timestamp start = null, stop = null;

                start = startTimeStamp;
                stop = stopTimestamp;

                int parts = cutInTimeFrames();

                for (int j = 0; j < parts + 1; j++) {

                    //One time frame = 10secs
                    Calendar startcal = Calendar.getInstance();
                    startcal.setTimeInMillis(start.getTime());
                    startcal.add(Calendar.SECOND, j * 10);
                    startTime = new Timestamp(startcal.getTime().getTime());

                    Calendar stopcal = Calendar.getInstance();
                    stopcal.setTimeInMillis(start.getTime());
                    stopcal.add(Calendar.SECOND, 10 + j * 10);
                    stopTime = new Timestamp(startcal.getTime().getTime());


                    if (stopTimestamp.before(stopTime)) {
                        stopTime = stopTimestamp;
                    }

//                    if(start.compareTo(stopTime) == 0)
//                    {
//
//                    }
                    //user: mdev01
                    //pass: qXeD6q5bMPJpKi

                    soleList = mainActivity.db.getSoleDatFromTest(startTime.toString(), stopTime.toString());

                    for (int i = 0; i < soleList.size() - 1; i++) {
                        try {
                            jsonFram.put("id", soleList.get(i).getId());
                            jsonFram.put("userId", soleList.get(i).getUserid());
                            jsonFram.put("sole", soleList.get(i).getSole());
                            jsonFram.put("timestamp", soleList.get(i).getTimestamp());
                            jsonFram.put("right_Left", soleList.get(i).getRL());
                            jsonFram.put("syncStatus", soleList.get(i).getSync());
                            jsonFram.put("acclXAxis", soleList.get(i).getAccx());
                            jsonFram.put("acclYAxis", soleList.get(i).getAccy());
                            jsonFram.put("acclZAxis", soleList.get(i).getAccz());
                            jsonFram.put("gyroRoll", soleList.get(i).getGyropoll());
                            jsonFram.put("gyroPitch", soleList.get(i).getGyropitch());
                            jsonFram.put("gyroYaw", soleList.get(i).getGyroyaw());
                            jsonFram.put("magnXAxis", soleList.get(i).getMagnx());
                            jsonFram.put("magnYAxis", soleList.get(i).getMagny());
                            jsonFram.put("magnZAxis", soleList.get(i).getMagnz());
                            jsonFram.put("pe1", soleList.get(i).getPe1());
                            jsonFram.put("pe2", soleList.get(i).getPe2());
                            jsonFram.put("pe3", soleList.get(i).getPe3());
                            jsonFram.put("pe4", soleList.get(i).getPe4());
                            jsonFram.put("pe5", soleList.get(i).getPe5());
                            jsonFram.put("pe6", soleList.get(i).getPe6());
                            jsonFram.put("pe7", soleList.get(i).getPe7());
                            jsonFram.put("pe8", soleList.get(i).getPe8());
                            jsonFram.put("pe9", soleList.get(i).getPe9());
                            jsonFram.put("pe10", soleList.get(i).getPe10());
                            jsonFram.put("Pe11", soleList.get(i).getPe11());
                            jsonFram.put("Pe12", soleList.get(i).getPe12());
                            jsonFram.put("Pe13", soleList.get(i).getPe13());
                            jsonFram.put("Pe14", soleList.get(i).getPe14());
                            jsonFram.put("Pe15", soleList.get(i).getPe15());
                            jsonFram.put("Pe16", soleList.get(i).getPe16());
                            jsonFram.put("gtf", soleList.get(i).getGtf());

                            JSONObject jsonFrame = jsonFram;

                            if (soleList.get(i).getRL().equals("LEFT")) {
                                solesL.add(getFrame(jsonFrame));

                            } else {
                                solesR.add(getFrame(jsonFrame));
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }

    };

    private void cutTimestampToSmaller() {
        if (stopTimestamp.after(startTimeStamp)) {
            long diff = stopTimestamp.getTime() - startTimeStamp.getTime();

        }

    }

    private Long calculateDifference(Timestamp date1, Timestamp date2) {
//        Timestamp date_1 = stringToTimestamp(date1);
//        Timestamp date_2 = stringToTimestamp(date2);
        String value = "";
        long milliseconds = date1.getTime() - date2.getTime();
        //if (value.equals("second"))
        return milliseconds / 1000;
//        if (value.equals("minute"))
//            return milliseconds / 1000 / 60;
//        if (value.equals("hours"))
//            return milliseconds / 1000 / 3600;
//        else
//            return new Long(value);
    }

    private int cutInTimeFrames() {
        int numberOfParts;
        Long diff = calculateDifference(startTimeStamp, stopTimestamp);
        if (diff > 10) {
            numberOfParts = Math.toIntExact(diff / 10);
        } else {
            numberOfParts = 1;
        }

        return numberOfParts;
    }

    private Timestamp stringToTimestamp(String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parsedDate = dateFormat.parse(date);
            return new Timestamp(parsedDate.getTime());
        } catch (Exception e) {
            return null;
        }
    }

    public List<String> read() {
        List<String> list = new ArrayList<>();
        String data = "";
        StringBuffer buffer = new StringBuffer();
        InputStream in = this.mainActivity.getResources().openRawResource(R.raw.insoletable);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        if (in != null) {
            try {

                while ((data = reader.readLine()) != null) {
                    buffer.append(data + "\n");
                    list.add(data);
                }
                //gatheringData(list);
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return list;
    }


    /**
     * Read JSON data and return them as a Sole Object
     */
    private Sole getFrame(JSONObject jsonFrame) {
        Sole sole = new Sole();
        try {
//            Date date = (Date) jsonFrame.get("timestamp");
//            sole.setTimestamp(dateFormat.format(date));
//            sole.setRL((String) jsonFrame.get("RLsole"));
//            sole.setAccx(String.valueOf(jsonFrame.get("accelX")));
//            sole.setAccy(String.valueOf(jsonFrame.get("accelY")));
//            sole.setAccz(String.valueOf(jsonFrame.get("accelZ")));
//            sole.setGyropoll(String.valueOf(jsonFrame.get("gyroX")));
//            sole.setGyropitch(String.valueOf(jsonFrame.get("gyroY")));
//            sole.setGyroyaw(String.valueOf(jsonFrame.get("gyroZ")));
//            sole.setMagnx(String.valueOf(jsonFrame.get("magX")));
//            sole.setMagny(String.valueOf(jsonFrame.get("magY")));
//            sole.setMagnz(String.valueOf(jsonFrame.get("magZ")));
//
//            JSONArray pressure = (JSONArray) jsonFrame.get("pressure");
//            sole.setPe1(String.valueOf(pressure.getDouble(0)));
//            sole.setPe2(String.valueOf(pressure.getDouble(1)));
//            sole.setPe3(String.valueOf(pressure.getDouble(2)));
//            sole.setPe4(String.valueOf(pressure.getDouble(3)));
//            sole.setPe5(String.valueOf(pressure.getDouble(4)));
//            sole.setPe6(String.valueOf(pressure.getDouble(5)));
//            sole.setPe7(String.valueOf(pressure.getDouble(6)));
//            sole.setPe8(String.valueOf(pressure.getDouble(7)));
//            sole.setPe9(String.valueOf(pressure.getDouble(8)));
//            sole.setPe10(String.valueOf(pressure.getDouble(9)));
//            sole.setPe11(String.valueOf(pressure.getDouble(10)));
//            sole.setPe12(String.valueOf(pressure.getDouble(11)));
//            sole.setPe13(String.valueOf(pressure.getDouble(12)));
//            sole.setPe14(String.valueOf(pressure.getDouble(13)));
//            sole.setPe15(String.valueOf(pressure.getDouble(14)));
//            sole.setPe16(String.valueOf(pressure.getDouble(15)));

            if (jsonFrame.get("id").equals("id")) {
                //Do not load database columns headers
                //sole.setId(0);
                sole.setSole("");
                sole.setUserid("");
                Date date = new Date();
                sole.setTimestamp(dateFormat.format(date));
                sole.setRL("");
                sole.setSync("");
                sole.setAccx("");
                sole.setAccy("");
                sole.setAccz("");
                sole.setGyropoll("");
                sole.setGyropitch("");
                sole.setGyroyaw("");
                sole.setMagnx("");
                sole.setMagny("");
                sole.setMagnz("");
                sole.setPe1("");
                sole.setPe2("");
                sole.setPe3("");
                sole.setPe4("");
                sole.setPe5("");
                sole.setPe6("");
                sole.setPe7("");
                sole.setPe8("");
                sole.setPe9("");
                sole.setPe10("");
                sole.setPe11("");
                sole.setPe12("");
                sole.setPe13("");
                sole.setPe14("");
                sole.setPe15("");
                sole.setPe16("");
                sole.setGtf("");
            } else {
                //sole.setId( jsonFrame.get("id"));
                sole.setSole(String.valueOf(jsonFrame.get("userId")));
                sole.setUserid(String.valueOf(jsonFrame.get("sole")));
                sole.setTimestamp(String.valueOf(jsonFrame.get("timestamp")));
                sole.setRL(String.valueOf(jsonFrame.get("right_Left")));
                sole.setSync(String.valueOf(jsonFrame.get("syncStatus")));
                sole.setAccx(String.valueOf(jsonFrame.get("acclXAxis")));
                sole.setAccz(String.valueOf(jsonFrame.get("acclYAxis")));
                sole.setAccx(String.valueOf(jsonFrame.get("acclZAxis")));
                sole.setGyropoll(String.valueOf(jsonFrame.get("gyroRoll")));
                sole.setGyropitch(String.valueOf(jsonFrame.get("gyroPitch")));
                sole.setGyroyaw(String.valueOf(jsonFrame.get("gyroYaw")));
                sole.setMagnx(String.valueOf(jsonFrame.get("magnXAxis")));
                sole.setMagny(String.valueOf(jsonFrame.get("magnYAxis")));
                sole.setMagnz(String.valueOf(jsonFrame.get("magnZAxis")));
                sole.setPe1(String.valueOf(jsonFrame.get("pe1")));
                sole.setPe2(String.valueOf(jsonFrame.get("pe2")));
                sole.setPe3(String.valueOf(jsonFrame.get("pe3")));
                sole.setPe4(String.valueOf(jsonFrame.get("pe4")));
                sole.setPe5(String.valueOf(jsonFrame.get("pe5")));
                sole.setPe6(String.valueOf(jsonFrame.get("pe6")));
                sole.setPe7(String.valueOf(jsonFrame.get("pe7")));
                sole.setPe8(String.valueOf(jsonFrame.get("pe8")));
                sole.setPe9(String.valueOf(jsonFrame.get("pe9")));
                sole.setPe10(String.valueOf(jsonFrame.get("pe10")));
                sole.setPe11(String.valueOf(jsonFrame.get("Pe11")));
                sole.setPe12(String.valueOf(jsonFrame.get("Pe12")));
                sole.setPe13(String.valueOf(jsonFrame.get("Pe13")));
                sole.setPe14(String.valueOf(jsonFrame.get("Pe14")));
                sole.setPe15(String.valueOf(jsonFrame.get("Pe15")));
                sole.setPe16(String.valueOf(jsonFrame.get("Pe16")));
                sole.setGtf(String.valueOf(jsonFrame.get("gtf")));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sole;

    }

    //Method to convert a byte array to a HEX. string.
    private String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }

    //TODO: live data version
    public void stopClock() {

        //Stop Bluetooth Low Energy Connection with Devices (Left Right Soles)
       // mLeftSamplingService.scanLeDevice(false);
        mRightSamplingService.scanLeDevice(false);
        //cancel all new Pool Manager Queue
        mthreadPoolManager.cancelAllTasks();

        st_sp = false;
        setDurationText("Test Duration: ");
        stopTime = new Date();
        stopTimestamp = new Timestamp(stopTime.getTime());
        handler.removeCallbacks(timeUpdater);
        soleHandler.removeCallbacks(soleData);

        // Keep OR Discard
        new AlertDialog.Builder(this.getContext())
                .setTitle("Test "+ strTestType)
                .setMessage("Do you want to save the test?")
                .setPositiveButton("Keep", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Save Test info
                        mainActivity.createTest();
                        setTestInfo(0);
                        mainActivity.updateTest(0);
                        mainActivity.objSync_now.add(false);
                        // Save Test raw data IF no internet
                        setSoleInfo();
                        timeDiff.setText("");
                        mainActivity.stage--;
                        setStage(mainActivity.stage,0);
                        mainActivity.fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white_24dp));
                        // Send data to cloud
//                        if ((new UserHelper(mainActivity)).hasExpired()){
//                        mainActivity.showMessage("Access Time has expired.\nPlease login again.");
//                            SharedPrefManager.getInstance(mainActivity).logout();
//                            mainActivity.finish();
//                        } else {
//                            new Thread(task1).start();
//                        }
                        initHistory();
                    }
                })
                .setNegativeButton("Discard",  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mainActivity.stage--;
                        setStage(mainActivity.stage,0);
                        goBack();
                        initHistory();
                        mainActivity.fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white_24dp));
                    }
                })
                .setIcon(R.drawable.logo)
                .show();

        fakePause(fakePauseMilSec);
        stopCountAlert();
        startStop.setText("Start");
    }

    //TODO: Working Version - hardcoded data

    /**
     * Called when the stop button is pressed
     */
//    public void stopClock() throws InterruptedException {
//
//
//        startStop.setText("Stop");
//
//        stopTime = new Date();
//        stopTimestamp = new Timestamp(stopTime.getTime());
//
//        setSoleInfo();
//
//        //Match SoleData from inSoles to UOI models
//        SessionModel sessionModel = soleModelConfig();
//
//        postSession(sessionModel); // with Volley library
//        //userLogin();
//        startStop.setText("Start");
//
//
//
//    }
    public SessionModel soleModelConfig() {
        List<Sole> soleList = new ArrayList<>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String insoleID = preferences.getString("patientId", "");
        String patientID = preferences.getString("insoleId", "");


        //TODO: Checking of timestamps
        Timestamp testTimestamp1 = startTimeStamp;
        Timestamp testTimestamp2 = stopTimestamp;


        soleList = mainActivity.db.getSoleDatFromTest("2021-Sep-08 15:57:33:010", "2021-Sep-08 16:01:44:000");

        SessionModel sessionModel = new SessionModel();

        Datum datum = new Datum();
        Session session = new Session();
        List<Datum> datumList = new ArrayList<>();

        /**
         * Config sessionModel basic properties
         */
        sessionModel.setProduct("smartinsole");
        sessionModel.setVersion(0.1);
        Date releaseDate = new Date();
        sessionModel.setReleaseDate(String.valueOf(releaseDate));
        sessionModel.setDemo(false);

        /**
         * Config session
         */
        session.setSessionId(sessionID);
        session.setStartTime(String.valueOf(startTimeStamp));
        session.setEndTime(String.valueOf(stopTimestamp));
        session.setPart(false);
        session.setPatientId(patientID);
        session.setInsoleId(insoleID);
        session.setSessionType(0);
        sessionModel.setSession(session);

        /**
         * Config data
         */
        for (int i = 0; i < soleList.size() - 1; i++) {
            if (soleList.get(i).getRL().equals("LEFT")) {

                datum.setTimestamp(Integer.parseInt(soleList.get(i).getTimestamp()));

                //Left Sole
                datum.setAccLx(Integer.parseInt(soleList.get(i).getAccx()));
                datum.setAccLy(Integer.parseInt(soleList.get(i).getAccy()));
                datum.setAccLz(Integer.parseInt(soleList.get(i).getAccz()));

                datum.setGyroLx(Integer.parseInt(soleList.get(i).getGyropoll()));
                datum.setGyroLy(Integer.parseInt(soleList.get(i).getGyropitch()));
                datum.setGyroLz(Integer.parseInt(soleList.get(i).getGyroyaw()));

                datum.setMagnLx(Integer.parseInt(soleList.get(i).getMagnx()));
                datum.setMagnLy(Integer.parseInt(soleList.get(i).getMagny()));
                datum.setMagnLz(Integer.parseInt(soleList.get(i).getMagnz()));


                datum.setpL01(Integer.parseInt(soleList.get(i).getPe1()));
                datum.setpL02(Integer.parseInt(soleList.get(i).getPe2()));
                datum.setpL03(Integer.parseInt(soleList.get(i).getPe3()));
                datum.setpL04(Integer.parseInt(soleList.get(i).getPe4()));
                datum.setpL05(Integer.parseInt(soleList.get(i).getPe5()));
                datum.setpL06(Integer.parseInt(soleList.get(i).getPe6()));
                datum.setpL07(Integer.parseInt(soleList.get(i).getPe7()));
                datum.setpL08(Integer.parseInt(soleList.get(i).getPe8()));
                datum.setpL09(Integer.parseInt(soleList.get(i).getPe9()));
                datum.setpL10(Integer.parseInt(soleList.get(i).getPe10()));
                datum.setpL11(Integer.parseInt(soleList.get(i).getPe11()));
                datum.setpL12(Integer.parseInt(soleList.get(i).getPe12()));
                datum.setpL13(Integer.parseInt(soleList.get(i).getPe13()));
                datum.setpL14(Integer.parseInt(soleList.get(i).getPe14()));
                datum.setpL15(Integer.parseInt(soleList.get(i).getPe15()));
                datum.setpL16(Integer.parseInt(soleList.get(i).getPe16()));

                //Right values
                datum.setAccRx(null);
                datum.setAccRy(null);
                datum.setAccRz(null);

                datum.setGyroRx(null);
                datum.setGyroRy(null);
                datum.setGyroRz(null);

                datum.setMagnRx(null);
                datum.setMagnRy(null);
                datum.setMagnRz(null);

                datum.setpR01(null);
                datum.setpR02(null);
                datum.setpR03(null);
                datum.setpR04(null);
                datum.setpR05(null);
                datum.setpR06(null);
                datum.setpR07(null);
                datum.setpR08(null);
                datum.setpR09(null);
                datum.setpR10(null);
                datum.setpR11(null);
                datum.setpR12(null);
                datum.setpR13(null);
                datum.setpR14(null);
                datum.setpR15(null);
                datum.setpR16(null);


                datum.setGrfL(Integer.parseInt(soleList.get(i).getGtf()));
            } else {
                datum.setTimestamp(Integer.parseInt(soleList.get(i).getTimestamp()));


                //Right Sole
                datum.setAccRx(Integer.parseInt(soleList.get(i).getAccx()));
                datum.setAccRy(Integer.parseInt(soleList.get(i).getAccy()));
                datum.setAccRz(Integer.parseInt(soleList.get(i).getAccz()));

                datum.setGyroRx(Integer.parseInt(soleList.get(i).getGyropoll()));
                datum.setGyroRy(Integer.parseInt(soleList.get(i).getGyropitch()));
                datum.setGyroRz(Integer.parseInt(soleList.get(i).getGyroyaw()));

                datum.setMagnRx(Integer.parseInt(soleList.get(i).getMagnx()));
                datum.setMagnRy(Integer.parseInt(soleList.get(i).getMagny()));
                datum.setMagnRz(Integer.parseInt(soleList.get(i).getMagnz()));

                datum.setpR01(Integer.parseInt(soleList.get(i).getPe1()));
                datum.setpR02(Integer.parseInt(soleList.get(i).getPe2()));
                datum.setpR03(Integer.parseInt(soleList.get(i).getPe3()));
                datum.setpR04(Integer.parseInt(soleList.get(i).getPe4()));
                datum.setpR05(Integer.parseInt(soleList.get(i).getPe5()));
                datum.setpR06(Integer.parseInt(soleList.get(i).getPe6()));
                datum.setpR07(Integer.parseInt(soleList.get(i).getPe7()));
                datum.setpR08(Integer.parseInt(soleList.get(i).getPe8()));
                datum.setpR09(Integer.parseInt(soleList.get(i).getPe9()));
                datum.setpR10(Integer.parseInt(soleList.get(i).getPe10()));
                datum.setpR11(Integer.parseInt(soleList.get(i).getPe11()));
                datum.setpR12(Integer.parseInt(soleList.get(i).getPe12()));
                datum.setpR13(Integer.parseInt(soleList.get(i).getPe13()));
                datum.setpR14(Integer.parseInt(soleList.get(i).getPe14()));
                datum.setpR15(Integer.parseInt(soleList.get(i).getPe15()));
                datum.setpR16(Integer.parseInt(soleList.get(i).getPe16()));


                //Left Sole
                datum.setAccLx(null);
                datum.setAccLy(null);
                datum.setAccLz(null);

                datum.setGyroLx(null);
                datum.setGyroLy(null);
                datum.setGyroLz(null);

                datum.setMagnLx(null);
                datum.setMagnLy(null);
                datum.setMagnLz(null);


                datum.setpL01(null);
                datum.setpL02(null);
                datum.setpL03(null);
                datum.setpL04(null);
                datum.setpL05(null);
                datum.setpL06(null);
                datum.setpL07(null);
                datum.setpL08(null);
                datum.setpL09(null);
                datum.setpL10(null);
                datum.setpL11(null);
                datum.setpL12(null);
                datum.setpL13(null);
                datum.setpL14(null);
                datum.setpL15(null);
                datum.setpL16(null);


                datum.setGrfR(Integer.parseInt(soleList.get(i).getGtf()));

            }
            datumList.add(datum);
        }

        sessionModel.setData(datumList);

        return sessionModel;
    }


    public void postSession(SessionModel mod) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String insoleID = preferences.getString("patientId", "");
        String patientID = preferences.getString("insoleId", "");
        sessionID = insoleID;

        //Setup sole session properties
        SessionModel sessionModel = new SessionModel();
        Session demoSession = new Session();

        demoSession.setSessionId(sessionID);
        demoSession.setPatientId(patientID);
        demoSession.setInsoleId(insoleID);
        demoSession.setStartTime("2021-11-11T10:05:08.583Z");
        demoSession.setEndTime("2021-11-11T10:05:08.583Z");
        demoSession.setPart(false);
        demoSession.setSessionType(0);

        sessionModel.setSession(demoSession);


        //Setup Sole data properties
        List<Datum> datumList = new ArrayList<>();

        for (int i = 0; i < mod.getData().size(); i++) {
            Datum datum = new Datum();
            datum.setTimestamp(mod.getData().get(i).getTimestamp());
            datum.setAccLx(mod.getData().get(i).getAccLx());
            datum.setAccLy(mod.getData().get(i).getAccLy());
            datum.setAccLz(mod.getData().get(i).getAccLz());

            datum.setGyroLx(mod.getData().get(i).getGyroLx());
            datum.setGyroLy(mod.getData().get(i).getGyroLy());
            datum.setGyroLz(mod.getData().get(i).getGyroLz());

            //TODO:UOI must add Magn
//            datum.setMagnLx(0);
//            datum.setMagnLy(0);
//            datum.setMagnLz(0);
//            datum.setMagnRx(0);
//            datum.setMagnRy(0);
//            datum.setMagnRz(0);

            datum.setpL01(mod.getData().get(i).getpL01());
            datum.setpL02(mod.getData().get(i).getpL02());
            datum.setpL03(mod.getData().get(i).getpL03());
            datum.setpL04(mod.getData().get(i).getpL04());
            datum.setpL05(mod.getData().get(i).getpL05());
            datum.setpL06(mod.getData().get(i).getpL06());
            datum.setpL07(mod.getData().get(i).getpL07());
            datum.setpL08(mod.getData().get(i).getpL08());
            datum.setpL09(mod.getData().get(i).getpL09());
            datum.setpL10(mod.getData().get(i).getpL10());
            datum.setpL11(mod.getData().get(i).getpL11());
            datum.setpL12(mod.getData().get(i).getpL12());
            datum.setpL13(mod.getData().get(i).getpL13());
            datum.setpL14(mod.getData().get(i).getpL14());
            datum.setpL15(mod.getData().get(i).getpL15());
            datum.setpL16(mod.getData().get(i).getpL16());
            datum.setGrfL(mod.getData().get(i).getGrfL());

            datum.setAccRx(mod.getData().get(i).getAccRx());
            datum.setAccRy(mod.getData().get(i).getAccRy());
            datum.setAccRz(mod.getData().get(i).getAccRz());

            datum.setGyroRx(mod.getData().get(i).getGyroRx());
            datum.setGyroRy(mod.getData().get(i).getGyroRy());
            datum.setGyroRz(mod.getData().get(i).getGyroRz());

            datum.setpR01(mod.getData().get(i).getpR01());
            datum.setpR02(mod.getData().get(i).getpR02());
            datum.setpR03(mod.getData().get(i).getpR03());
            datum.setpR04(mod.getData().get(i).getpR04());
            datum.setpR05(mod.getData().get(i).getpR05());
            datum.setpR06(mod.getData().get(i).getpR06());
            datum.setpR07(mod.getData().get(i).getpR07());
            datum.setpR08(mod.getData().get(i).getpR08());
            datum.setpR09(mod.getData().get(i).getpR09());
            datum.setpR10(mod.getData().get(i).getpR10());
            datum.setpR11(mod.getData().get(i).getpR11());
            datum.setpR12(mod.getData().get(i).getpR12());
            datum.setpR13(mod.getData().get(i).getpR13());
            datum.setpR14(mod.getData().get(i).getpR14());
            datum.setpR15(mod.getData().get(i).getpR15());
            datum.setpR16(mod.getData().get(i).getpR16());
            datum.setGrfR(mod.getData().get(i).getGrfR());


            datumList.add(datum);

        }
        sessionModel.setData(datumList);


        //Setup Sole model basic properties
        sessionModel.setDemo(false);
        sessionModel.setReleaseDate("2021-11-11T10:05:08.583Z");
        sessionModel.setVersion(0);
        sessionModel.setProduct("string");


        prefs = SharedPrefManager.getInstance(getContext()).getUser();

        String token = prefs.getToken();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        String apiKey = sp.getString("apiKey", "");

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("", sessionModel);
        String json = null;
        JSONObject reqbody = new JSONObject();
        try {
            reqbody.put("", sessionModel);


        } catch (JSONException e) {

            e.printStackTrace();
        }

        try {
            json = new ObjectMapper().writeValueAsString(sessionModel);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String t = reqbody.toString().trim();

        String test = "{\n" +
                "    product: smartinsole,\n" +
                "    version: 0.1,\n" +
                "    releaseDate: 20200505,\n" +
                "    demo: false,\n" +
                "    session: {\n" +
                "        sessionId: 1212121307,\n" +
                "        patientId: 1212121307,\n" +
                "        insoleId: 1212121307,\n" +
                "        startTime: 20200505,\n" +
                "        endTime: 20200505,\n" +
                "        isPart: false,\n" +
                "        sessionType: 0\n" +
                "    },\n" +
                "    data: [\n" +
                "        {\n" +
                "            timestamp: 0.0,\n" +
                "            accLx: -0.23877,\n" +
                "            accLy: 0.036621,\n" +
                "            accLz: 0.975586,\n" +
                "            gyroLx: -0.98,\n" +
                "            gyroLy: 3.78,\n" +
                "            gyroLz: -0.7,\n" +
                "            pL01: 5.25,\n" +
                "            pL02: 4.25,\n" +
                "            pL03: 4.75,\n" +
                "            pL04: 5.25,\n" +
                "            pL05: 3.25,\n" +
                "            pL06: 0.25,\n" +
                "            pL07: 0.75,\n" +
                "            pL08: 0.25,\n" +
                "            pL09: 2.0,\n" +
                "            pL10: 1.25,\n" +
                "            pL11: 1.25,\n" +
                "            pL12: 0.5,\n" +
                "            pL13: 0.0,\n" +
                "            pL14: 4.25,\n" +
                "            pL15: 0.75,\n" +
                "            pL16: 0.25,\n" +
                "            grfL: 394.0,\n" +
                "            accRx: null,\n" +
                "            accRy: null,\n" +
                "            accRz: null,\n" +
                "            gyroRx: null,\n" +
                "            gyroRy: null,\n" +
                "            gyroRz: null,\n" +
                "            pR01: null,\n" +
                "            pR02: null,\n" +
                "            pR03: null,\n" +
                "            pR04: null,\n" +
                "            pR05: null,\n" +
                "            pR06: null,\n" +
                "            pR07: null,\n" +
                "            pR08: null,\n" +
                "            pR09: null,\n" +
                "            pR10: null,\n" +
                "            pR11: null,\n" +
                "            pR12: null,\n" +
                "            pR13: null,\n" +
                "            pR14: null,\n" +
                "            pR15: null,\n" +
                "            pR16: null,\n" +
                "            grfR: null\n" +
                "        },\n" +
                "    ]\n" +
                "}".toString().trim();


        json = json.toString().replace("part", "isPart");
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        JsonElement jsonElement = gson.fromJson(json, JsonElement.class);
//        JsonObject jobj = jsonElement.getAsJsonObject();

        (Api.getClient().postSession(token.toString().trim(), apiKey.toString().trim(), json)).enqueue(new Callback<Sesion>() {
            @Override
            public void onResponse(Call<Sesion> call, Response<Sesion> response) {
                sessionData = response.body();
                //String apikey =  response.body();
                Log.d("response", "");


            }

            @Override
            public void onFailure(Call<Sesion> call, Throwable t) {
                Log.d("response", t.getStackTrace().toString());
                Log.d("MainActivity", t.getMessage());
            }
        });


//        final ProgressDialog loading = new ProgressDialog(getContext());
//        loading.setMessage("Please wait...uploading data");
//        loading.setCanceledOnTouchOutside(false);
//        loading.show();
//
//        String postUrl = getString(R.string.base_url) + getString(R.string.post_sessions_url);
//        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
//
//        JSONObject postData = new JSONObject();
//        JSONObject postData2 = new JSONObject();
//
//        JSONObject newData = new JSONObject();
//        JSONArray array = new JSONArray();
//        JSONObject session = new JSONObject();
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
//        String apiKey = sp.getString("apiKey", "");
//
//
//        try {
//
//            postData.put("product", "string");
//            postData.put("version", 0);
//            postData.put("releaseDate", "2021-11-03T08:38:50.274Z");
//            postData.put("demo", true);
//
//            session.put("sessionId", "string");
//            session.put("patientId", "string");
//            session.put("insoleId", "string");
//            session.put("startTime", "2021-11-07T19:16:28.623Z");
//            session.put("endTime", "2021-11-07T19:16:28.623Z");
//            session.put("isPart", true);
//            session.put("sessionType", 0);
//
//            postData.put("session", session);
//
//
//            newData.put("timestamp", 0);
//            newData.put("accLx", 0);
//            newData.put("accLy", 0);
//            newData.put("accLz", 0);
//            newData.put("gyroLx", 0);
//            newData.put("gyroLy", 0);
//            newData.put("gyroLz", 0);
//            newData.put("pL01", 0);
//            newData.put("pL02", 0);
//            newData.put("pL03", 0);
//            newData.put("pL04", 0);
//            newData.put("pL05", 0);
//            newData.put("pL06", 0);
//            newData.put("pL07", 0);
//            newData.put("pL08", 0);
//            newData.put("pL09", 0);
//            newData.put("pL10", 0);
//            newData.put("pL11", 0);
//            newData.put("pL12", 0);
//            newData.put("pL13", 0);
//            newData.put("pL14", 0);
//            newData.put("pL15", 0);
//            newData.put("pL16", 0);
//            newData.put("grfL", 0);
//            newData.put("accRx", 0);
//            newData.put("accRy", 0);
//            newData.put("accRz", 0);
//            newData.put("gyroRx", 0);
//            newData.put("gyroRy", 0);
//            newData.put("gyroRz", 0);
//            newData.put("pR01", 0);
//            newData.put("pR02", 0);
//            newData.put("pR03", 0);
//            newData.put("pR04", 0);
//            newData.put("pR05", 0);
//            newData.put("pR06", 0);
//            newData.put("pR07", 0);
//            newData.put("pR08", 0);
//            newData.put("pR09", 0);
//            newData.put("pR10", 0);
//            newData.put("pR11", 0);
//            newData.put("pR12", 0);
//            newData.put("pR13", 0);
//            newData.put("pR14", 0);
//            newData.put("pR15", 0);
//            newData.put("pR16", 0);
//            newData.put("grfR", 0);
//
//            List<JSONObject> datumList = new ArrayList<>();
//
//            datumList.add(newData);
//            for(int i = 0; i < datumList.size(); i++) {
//                array.put(datumList.get(i));
//            }
//
//            try {
//                postData.put("x-patient-api-key", apiKey);
//                postData.put("data", array);
//
//
//
//            } catch(JSONException e) {
//                e.printStackTrace();
//            }
//
//            String t = "";
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Toast.makeText(getContext(), "Response: "+response, Toast.LENGTH_LONG).show();
//            }
//        },new Response.ErrorListener() {
//
////                JsonObjectRequest  request = new JsonObjectRequest (Request.Method.POST, postUrl,postData, new Response.Listener<JSONObject>() {
////            @Override
////            public void onResponse(JSONObject response) {
////
////
////                if (response.has("sessionType")) {
////                    String t ="";
////                }
////
////            }
////
////
////
////
////        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if (error == null || error.networkResponse == null) {
//                    return;
//                }
//
//                String body;
//                //get status code here
//                final String statusCode = String.valueOf(error.networkResponse.statusCode);
//                //get response body and parse with appropriate encoding
//                try {
//                    body = new String(error.networkResponse.data,"UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    // exception
//                }
//
//                //do stuff with the body...
//            }
//        })
//        {
////            @Override
////            protected Map<String, String> getParams() throws AuthFailureError {
////                Map<String, String> params = new HashMap<>();
////
////                params.put("product", "string");
////                params.put("version", String.valueOf(0));
////                params.put("releaseDate", "2021-11-03T08:38:50.274Z");
////                params.put("demo", String.valueOf(true));
////
//////                Gson gson=new Gson();
//////                String json=gson.toJson(createjsonObject());
//////                String data = new Gson().toJson(json);
//////                data = data.replace("\\", "");
//////                params.put("session", data);
////
////                params.put("session", session.toString());
////
//
////
////
////
////
////                return params;
////            }
//
//            @Override
//            public Priority getPriority() {
//                return Priority.HIGH;
//            }
//
//            @Override
//            protected Map<String,String> getParams(){
//                Map<String, String> params = new HashMap<String, String>();
//                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
//                String apiKey = sp.getString("apiKey", "");
//                params.put("x-patient-api-key", "apiKey");
//
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<>();
//                prefs = SharedPrefManager.getInstance(getContext()).getUser();
//
//                String token = prefs.getToken();
//                headers.put("x-token", token);
//                return headers;
//            }
//        };
////        {
////            @Override
////            public Priority getPriority() {
////                return Priority.HIGH;
////            }
////
////            @Override
////            protected Map<String, String> getParams() throws AuthFailureError {
////                Map<String, String> headers = new HashMap<>();
////
////                //String token = prefs.getJWToken();
////
////                //headers.put("Authorization", "Bearer "+ token);
////                //headers.put("Content-Type", "application/json");
////                //headers.put("accept", "application/json");
////
////                return headers;
////            }
////
////
////        };
//
//        requestQueue.add(jsonObjectRequest);

    }

    public void goBack() {
        startStop.setText("Start");
        handler.removeCallbacks(timeUpdater);
        soleHandler.removeCallbacks(soleData);
        timeDiff.setText("");
        solesR = null;
        solesL = null;
        st_sp = false;
    }

    /**
     * Thread to count the test time and live update the text Duration
     */
    public Runnable timeUpdater = new Runnable() {
        @Override
        public void run() {
            Date now = new Date();

            // in Milliseconds
            long difference_In_Time = now.getTime() - startTime.getTime();

            // Calculate time difference
            // in Seconds
            difference_In_Seconds = (difference_In_Time / 1000) % 60;
            // in Minutes
            difference_In_Minutes = (difference_In_Time / (1000 * 60)) % 60;
            // in Hours
            difference_In_Hours = (difference_In_Time / (1000 * 60 * 60)) % 24;

            setDurationText("Test Running: ");
            handler.postDelayed(timeUpdater, 500);
        }
    };

    /**
     * Display Test Duration
     *
     * @param message Time
     */
    @SuppressLint("SetTextI18n")
    private void setDurationText(String message) {
        if (difference_In_Hours == 0) {
            h = "00";
        } else if (difference_In_Hours < 10) {
            h = "0" + difference_In_Hours;
        } else {
            h = "" + difference_In_Hours;
        }

        if (difference_In_Minutes == 0) {
            m = "00";
        } else if (difference_In_Minutes < 10) {
            m = "0" + difference_In_Minutes;
        } else {
            m = "" + difference_In_Minutes;
        }

        if (difference_In_Seconds == 0) {
            s = "00";
        } else if (difference_In_Seconds < 10) {
            s = "0" + difference_In_Seconds;
        } else {
            s = "" + difference_In_Seconds;
        }

        timeDiff.setText(message + h + ":" + m + ":" + s);
    }

    /**
     * Set stage of Fragment
     * Stage 1: All tests are displayed
     * Stage 2: Select test
     * Stage 3: Start/Stop test
     */
    public void setStage(int mStage, int mTestType) {
        switch (mStage) {
            case 0:
                history.setVisibility(View.VISIBLE);
                select_test.setVisibility(View.GONE);
                start_stop_test.setVisibility(View.GONE);
                break;
            case 1:
                history.setVisibility(View.GONE);
                start_stop_test.setVisibility(View.GONE);
                select_test.setVisibility(View.VISIBLE);
                break;
            case 2:
                history.setVisibility(View.GONE);
                select_test.setVisibility(View.GONE);
                start_stop_test.setVisibility(View.VISIBLE);
                switch (mTestType) {
                    case TUG:
                        test_type.setText("TUG");
                        strTestType = "TUG";
                        break;
                    case BALANCE:
                        test_type.setText("BALANCE");
                        strTestType = "BALANCE";
                        break;
                    case FREE_WALKING:
                        test_type.setText("FREE WALKING");
                        strTestType = "FREE WALKING";
                        break;
                }
                break;
        }
    }

    //TODO: live data version

    /**
     * Add RAW Data to SQLite
     */
    private void setSoleInfo() {
        final TestFragment testFragment = this;
        // Run in a back thread
        new Thread(new Runnable() {
            public void run() {
                if (solesR != null)
                    mainActivity.db.insertMultipleSoleData(solesR);
                if (solesL != null)
                    mainActivity.db.insertMultipleSoleData(solesL);
                mainActivity.soleList = mainActivity.db.getAllSoles();
            }
        }).start();
    }

    /**
     * Get Test Duration
     *
     * @return duration as a String
     */
    private String getDuration() {
        return h + ":" + m + ":" + s;
    }

    //TODO: Working version - hardcoded data
    /**
     * Add RAW Data to SQLite
     */
//    private void setSoleInfo() throws InterruptedException {
//
//        final TestFragment testFragment = this;
//        // Run in a back thread
//
//        List<Sole> s = solesR;
//        List<Sole> su = solesL;
//
//        Thread t1 = new Thread(new Runnable() {
//            public void run() {
//                if (solesL != null) {
//                    mainActivity.db.insertMultipleSoleData(solesL);
//                }
//
//            }
//        });
//
//        Thread t2 = new Thread(new Runnable() {
//            public void run() {
//                if (solesR != null) {
//                    mainActivity.db.insertMultipleSoleData(solesR);
//                }
//            }
//        });
//
//
//        Thread t3 = new Thread(new Runnable() {
//            public void run() {
//
//                mainActivity.soleList = mainActivity.db.getAllSoles();
//            }
//        });
//
//        t1.start();
//        t1.join();
//
//        t2.start();
//        t2.join();
//
//        t3.start();
//        t3.join();
//
//    }

    /**
     * Add Basic Info to TestDB
     * TimeStamp - TestType - Duration
     *
     * @param poss Test to add the data
     */
    private void setTestInfo(int poss) {
        Test n = mainActivity.testList.get(poss);
        // Add to db
        n.setSync("NO_SYNC");
        n.setStartTimestamp(startTimeStamp.toString());
        n.setStopTimestamp(stopTimestamp.toString());
        n.setTestType(strTestType);
        n.setDuration(getDuration());
    }

    /**
     * Initialize RecyclerView in HistoryFragment
     * Add Listener
     * - onClick select the sole
     * - onLongClick delete the sole
     */
    public void recView() {
        historyAdapter = new HistoryAdapter(getContext(), objTimeStamp, objTestType, objDuration, objSync, mainActivity.objSync_now);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        view_history.setLayoutManager(mLayoutManager);
        view_history.setItemAnimator(new DefaultItemAnimator());
        view_history.addItemDecoration(new MyDividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL, 16));
        view_history.setAdapter(historyAdapter);

        // refreshing the list
        historyAdapter.notifyDataSetChanged();

        view_history.addOnItemTouchListener(new RecyclerTouchListener(getContext(),
                view_history, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View view, final int position) {
                mainActivity.showMessage("Test No: " + (position + 1) + " is selected.");
                mainActivity.selPosition = position; // Selected Test
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    public void initHistory() {
        getTestBasicInfo();
        // TODO check server for processed data
        recView();
    }

    private Runnable task1 = new Runnable() {
        @Override
        public void run() {
            mainActivity.bottomNavigationView.findViewById(R.id.action_one).setClickable(false);
            mainActivity.bottomNavigationView.findViewById(R.id.action_two).setClickable(false);
            syncTest(solesR, solesL, 0);
            mainActivity.bottomNavigationView.findViewById(R.id.action_one).setClickable(true);
            mainActivity.bottomNavigationView.findViewById(R.id.action_two).setClickable(true);
        }
    };

    public Runnable task2 = new Runnable() {
        @Override
        public void run() {
            syncAllTests();
        }
    };

    /**
     * Used to Load Test Basic Info
     * TimeStamp - TestType - Duration
     */
    private void getTestBasicInfo() {
        objSync = new ArrayList<String>();
        objTimeStamp = new ArrayList<String>();
        objTestType = new ArrayList<String>();
        objDuration = new ArrayList<String>();
        if (mainActivity.testList.size() > 0) {
            for (int i = 0; i < mainActivity.testList.size(); i++) {
                Test test = mainActivity.testList.get(i);

                // Get Data from db
                String sync = test.getSync();
                String strTimeStamp = test.getStartTimestamp();
                String strTestType = test.getTestType();
                String strDuration = test.getDuration();

                objSync.add(sync);
                objTimeStamp.add(strTimeStamp);
                objTestType.add(strTestType);
                objDuration.add(strDuration);
            }
            no_tests.setVisibility(View.GONE);
            view_history.setVisibility(View.VISIBLE);
        } else { // NO TESTS YET
            objTimeStamp.add("2018-02-21 00:15:42");
            objTestType.add("TESTTYPE");
            objDuration.add("DURATION");
            // NO Tests
            no_tests.setVisibility(View.VISIBLE);
            view_history.setVisibility(View.GONE);
        }
    }

    /**
     * Check for unsynchronized Tests
     * and sync them if internet connection exist
     */
    private void syncAllTests() {
        mainActivity.bottomNavigationView.findViewById(R.id.action_one).setClickable(false);
        mainActivity.bottomNavigationView.findViewById(R.id.action_two).setClickable(false);

        int syncSize = objSync.size();
        if (syncSize == 0) return;    // No Tests to sync

        for (int i = 0; i < syncSize; i++) {
            if (objSync.get(i).equals("NO_SYNC")) break; // Test to sync
            if (i == syncSize - 1) return; // No Tests to sync
        }

        // Check internet connection
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            syncHandler.post(new Runnable() {
                @Override
                public void run() {
                    initHistory();
                    mainActivity.showMessage("Can't upload data. No internet.");
                }
            });
            return;
        }

        // For each Test
        for (int i = 0; i < syncSize; i++) {
            // if test not synchronized
            if (objSync.get(i).equals("NO_SYNC")) {
                syncTest(null, null, i);
            }
        }
        mainActivity.bottomNavigationView.findViewById(R.id.action_one).setClickable(true);
        mainActivity.bottomNavigationView.findViewById(R.id.action_two).setClickable(true);
    }

    /**
     * Sync ONLY the current test
     */
    private void syncTest(List<Sole> solesR, List<Sole> solesL, final int test) {
        // Check internet connection
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            syncHandler.post(new Runnable() {
                @Override
                public void run() {
                    mainActivity.showMessage("Can't upload data. No internet.");
                }
            });
            return;
        }

        syncHandler.post(new Runnable() {
            @Override
            public void run() {
                mainActivity.toolbar.setVisibility(View.GONE);
                sync.setVisibility(View.VISIBLE);
                sync_txt.setText("Uploading Test: " + (test + 1) + "\nPlease wait.");
                mainActivity.objSync_now.set(test, true);
                mainActivity.fab.setClickable(false);
                recView();
            }
        });

        if (solesR == null || solesL == null) {
            Test myTest = mainActivity.testList.get(test);
            solesR = mainActivity.db.getSpecificTest(myTest.getStartTimestamp(), myTest.getStopTimestamp(), "RIGHT");
            solesL = mainActivity.db.getSpecificTest(myTest.getStartTimestamp(), myTest.getStopTimestamp(), "LEFT");
        }
        if (solesR == null && solesL == null || solesL.size() == 0 && solesR.size() == 0) {
            mainActivity.showMessage("No valid or corrupted data from the soles for the test No: " + test);
            return;
        }
        // If only one sole null initialize it
        if (solesR == null) solesR = new ArrayList<>();
        if (solesL == null) solesL = new ArrayList<>();

        postTestData(solesR, solesL, test);

        syncHandler.post(new Runnable() {   // if not => crash
            @Override
            public void run() {
                mainActivity.toolbar.setVisibility(View.VISIBLE);
                sync_txt.setText("");
                sync.setVisibility(View.GONE);
                mainActivity.objSync_now.set(test, false);
                mainActivity.fab.setClickable(true);
                recView();
            }
        });
    }

    private boolean postTestData(List<Sole> solesR, List<Sole> solesL, final int test) {
        Random rand = new Random();

        // Create JSON packet
        JSONObject packet = new JSONObject();
        User user = SharedPrefManager.getInstance(mainActivity).getUser();

        // get data for the test
        Test myTest = mainActivity.testList.get(test);
        final String startTimestamp = myTest.getStartTimestamp();
        final String stopTimestamp = myTest.getStopTimestamp();

        if (solesR == null && solesL == null) return false;
        assert solesL != null;
        assert solesR != null;
        int soleSizeR = solesR.size();
        int soleSizeL = solesL.size();
        // Break into 2 mins packets (50Hz*60*2 = 6.000 samples) -> 2.22MB
        // Break into 1.5 min packets (50Hz*60*1.5 = 4.500 samples) -> 1.66MB
        // Max packet with 4.500 samples 1.85MB
        int samples = 4500;
        int packets_numR = (int) Math.ceil((double) soleSizeR / samples);
        int packets_numL = (int) Math.ceil((double) soleSizeL / samples);

        // number of max packets to be send
        int max_packet = Math.max(packets_numR, packets_numL);
        countDownLatch = new CountDownLatch(max_packet);

        postJsonRequestQueue = new PostJsonRequestQueue(mainActivity, url, countDownLatch);
        VolleySingleton.getInstance(mainActivity).setError(false);

        for (int j = 0; j < max_packet; j++) {
            JSONObject session = new JSONObject();
            try {
                int rand_int1 = rand.nextInt(1000000);
                session.put("sessionid", rand_int1);
                session.put("patientid", user.getUsername());
                session.put("insoleid", user.getUsername());
                session.put("starttime", startTimestamp);
                session.put("endtime", stopTimestamp);
                //session.put("pack_num", j);
                session.put("isPart", true);
                session.put("session_num", 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                packet.put("product", "smartinsole");
                packet.put("version", 0.1);
                packet.put("releaseDate", "2020-05-05T00:00:00.000Z");
                packet.put("demo", true);
                //packet.put("total_packets", max_packet);
                packet.put("session", session);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            List<Sole> sR, sL;

            int start = j * samples;

            if (j < packets_numR) {
                int stopR = (j + 1) * samples;
                if (stopR >= soleSizeR) stopR = soleSizeR;      // -1 exclusive
                sR = solesR.subList(start, stopR);
            } else {
                sR = null;
            }
            if (j < packets_numL) {
                int stopL = (j + 1) * samples;
                if (stopL >= soleSizeL) stopL = soleSizeL;      // -1 exclusive
                sL = solesL.subList(start, stopL);
            } else {
                sL = null;
            }

            JSONObject jSONObject = jsonPacketManager.packet(sR, sL);
            try {
                packet.put("data", jSONObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Uncomment for test
            // jsonPacketManager.writeToTXT(packet, "test_"+j+".txt");


            // Post data using VolleySingleton - Suggested by designers
            postJsonRequestQueue.postJSONRequest(packet, new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    Log.d("JSON REQUEST RESULT S: ", result.toString());
                }

                @Override
                public void onError(String result) {
                    Log.d("JSON REQUEST RESULT E: ", result);
                }
            });
            // Remove data key with its value to add new data
            // TODO check packet, maybe new packet in for loop
            packet.remove("data");
        }
        // Wait posts to finish
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // IF ALL posts finished without an error
        if (!VolleySingleton.getInstance(mainActivity).getError()) {
            objSync.set(test, "SYNC");
            myTest.setSync("SYNC");
            mainActivity.updateTest(test);
            mainActivity.db.deleteSpecificTest(startTimestamp, stopTimestamp);
            return true;
        }
        return false;
    }

    /**
     * Request Permission and Enable Bluetooth
     */
    private void EnableBluetooth() {
        // Ensures Bluetooth is available on the device and it is enabled.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent =
                    new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
    }

    /**
     * Connect and Disconnect Left Service
     */
    public ServiceConnection serviceConnectionLeft = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LeftSamplingService.LocalBinder binder = (LeftSamplingService.LocalBinder) service;
            mLeftSamplingService = binder.getService();
            isBoundL = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBoundL = false;
        }
    };

    /**
     * Connect and Disconnect Right Service
     */
    public ServiceConnection serviceConnectionRight = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            RightSamplingService.LocalBinder binder = (RightSamplingService.LocalBinder) service;
            mRightSamplingService = binder.getService();
            isBoundR = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBoundR = false;
        }
    };

    public void fakePause(int ms) {

        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            System.err.format("Something went wrong...", e);
        }

    } // END fakePause(int ms)

    public void startCountAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());

        builder.setMessage("Data count started!");
        builder.setCancelable(false);

        builder.setPositiveButton("Ok", new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fakePause(fakePauseMilSec);
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    } // END startCountAlert()

    public void stopCountAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());

        builder.setMessage("Data count stopped!");
        builder.setCancelable(false);

        builder.setPositiveButton("Ok", new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fakePause(fakePauseMilSec);
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    } // END stopCountAlert()

}
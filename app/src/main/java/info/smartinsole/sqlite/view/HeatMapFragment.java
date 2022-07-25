package info.smartinsole.sqlite.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import ca.hss.heatmaplib.HeatMap;
import ca.hss.heatmaplib.HeatMapMarkerCallback;
import info.smartinsole.sqlite.R;
import info.smartinsole.sqlite.database.model.Sole;
import info.smartinsole.sqlite.http_client.PostJsonRequestQueue;
import info.smartinsole.sqlite.http_client.VolleyCallback;
import info.smartinsole.sqlite.http_client.VolleySingleton;
import info.smartinsole.sqlite.login.SharedPrefManager;
import info.smartinsole.sqlite.login.User;

import static android.os.Looper.getMainLooper;

/**
 * Fragment for the footprint Heatmap
 */
public class HeatMapFragment extends Fragment {

    private HeatMap heatMap;
    private SeekBar seekBar;

    private TextView txtProgress1;
    private ProgressBar progressBar1;

    private RadioGroup rg;
    private int phase, maxRb;

    private float maxX;
    private float maxY;

    private final Handler handler = new Handler(getMainLooper());
    private float[] pointsX;
    private float[] pointsY;

    // For test
    private Random rand = new Random();
    private ArrayList<Sole> lSoleList, rSoleList;
    private int timeCnt;
    private MainActivity mainActivity;
    private CountDownLatch countDownLatch;
    private PostJsonRequestQueue postJsonRequestQueue;
    private JSONPacketManager jsonPacketManager;
    private final Handler syncHandler = new Handler(Looper.getMainLooper());

    public HeatMapFragment(){
        // Required empty public constructor
    }

    public static HeatMapFragment newInstance() {
        return new HeatMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_heatmap, container, false);

        // Upload .txt data
        mainActivity = (MainActivity) getActivity();
        jsonPacketManager = new JSONPacketManager(mainActivity);
        lSoleList = new ArrayList<>();
        rSoleList = new ArrayList<>();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        maxX = displayMetrics.heightPixels / displayMetrics.density;
        maxY = displayMetrics.widthPixels / displayMetrics.density;

        heatMap = (HeatMap) view.findViewById(R.id.heatmap);
        final MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.stage = 0;

        // Set the range that you want the heat maps gradient to cover.
        heatMap.setMinimum(0.0);
        heatMap.setMaximum(50.0);

        // Radio Buttons
        rg = view.findViewById(R.id.seekBarRadioGroup);
        maxRb = rg.getChildCount();
        for (int i = 0; i < maxRb; i++) {
            rg.getChildAt(i).setEnabled(false);
        }
        rg.check(R.id.rb5);
        phase = 4;

        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                View v = rg.getChildAt(i);
//                rg.check(v.getId());
//                phase = i;
//                changeData();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        txtProgress1 = (TextView) view.findViewById(R.id.txtProgress1);
        progressBar1 = (ProgressBar) view.findViewById(R.id.progressBar1);

        (view.findViewById(R.id.click1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar1.setProgress(0);
                txtProgress1.setText("");

                // Permission
                if (Build.VERSION.SDK_INT >= 23) {
                    if (mainActivity.isReadStoragePermissionGranted()) {
                        timeCnt=0;

                        // if pressed second time
                        handler.removeCallbacks(dataUpdater);
                        // Read file
                        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

                        final File file = new File(path, "pd001_1high1.txt");
                        if (readTxtData(file)){
//                            if ((new UserHelper(mainActivity)).hasExpired()){
//                                Toast.makeText(mainActivity, "Access Time has expired.\nPlease login again.", Toast.LENGTH_SHORT).show();
//                                SharedPrefManager.getInstance(mainActivity).logout();
//                                mainActivity.finish();
//                            } else {
                                new Thread(task1).start();
//                            }
                            dataUpdater.run();
                        }
                    }
                }

//                phase = (phase < maxRb-1) ? ++phase : 0;
//                View v1 = rg.getChildAt(phase);
//                rg.check(v1.getId());
//                seekBar.setProgress(phase);
//                changeData();
            }
        });

        setColors();
        createPoints();
        //dataUpdater.run();
//        changeData();

        //set the radius to x pixels.
        heatMap.setRadius(600);
        //set the maximum width to x px
        heatMap.setMaxDrawingWidth(1200);
        //set Blur
        heatMap.setBlur(0.7);

        return view;
    }

    /**
     * Create a map of colors to be used in heatmap
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setColors(){
        //change the colour gradient
        Map<Float, Integer> colors = new ArrayMap<>();

        // Pressure Points colors
        colors.put(0.0f, 0xFF046fd4);  // blue
        colors.put(0.1f, 0xFF1e9ac7);
        colors.put(0.2f, 0xFF27dbba);  // cyan
        colors.put(0.3f, 0xFF6dcf0c);  // green
        colors.put(0.4f, 0xFFc5f213);  // lettuce
        colors.put(0.5f, 0xFFf2ee13);  // yellow
        colors.put(0.6f, 0xFFf2c513);  // mustard
        colors.put(0.7f, 0xFFf2a813);
        colors.put(0.8f, 0xFFed881c);  // orange
        colors.put(0.9f, 0xFFed4d1c);  // dark-orange
        colors.put(1.0f, 0xFFe02b1b);  // red

        heatMap.setColorStops(colors);
    }

    /**
     * Create footprint points
     */
    private void createPoints(){
        // create a grid n x n   (20 x 10 each sole)
        // (0,0) for heatmap is top left corner

        int n = 20;
        int stepX = (int) (maxX/n);       // step X axis
        int stepY = (int) (maxY/n);       // step Y axis

        // X, Y coordinates [0-15] for pressure points && [16] for pressure center
        pointsX = new float[17];
        pointsY = new float[17];

        pointsX[0] = 5f*stepX; pointsX[1] = 2.5f*stepX; pointsX[2] = 5f*stepX; pointsX[3] = 2.5f*stepX;
        pointsX[4] = 5f*stepX; pointsX[5] = 2f*stepX; pointsX[6] = 6f*stepX; pointsX[7] = 2f*stepX;
        pointsX[8] = 7.5f*stepX; pointsX[9] = 6f*stepX; pointsX[10] = 4f*stepX; pointsX[11] = 3f*stepX;
        pointsX[12] = 1.5f*stepX; pointsX[13] = 7f*stepX; pointsX[14] = 5f*stepX; pointsX[15] = 3f*stepX;
        pointsX[16] = 4f*stepX;  // Center

        pointsY[0] = 18f*stepY; pointsY[1] = 18f*stepY; pointsY[2] = 15f*stepY; pointsY[3] = 15.5f*stepY;
        pointsY[4] = 12.5f*stepY; pointsY[5] = 13f*stepY; pointsY[6] = 9f*stepY; pointsY[7] = 10f*stepY;
        pointsY[8] = 5.5f*stepY; pointsY[9] = 5.5f*stepY; pointsY[10] = 6f*stepY; pointsY[11] = 6f*stepY;
        pointsY[12] = 6.5f*stepY; pointsY[13] = 2f*stepY; pointsY[14] = 2.5f*stepY; pointsY[15] = 3.5f*stepY;
        pointsY[16] = 10*stepY;  // Center

    }

    /**
     * Show data
     */
    public Runnable dataUpdater = new Runnable() {
        @Override
        public void run() {

            // uncomment to project points
            //heatMap.setMarkerCallback(new HeatMapMarkerCallback.CircleHeatMapMarker(0xff0911ed));
            heatMap.setMarkerCallback(new MyMarker());

            if (timeCnt < lSoleList.size()) {
                HeatMap.DataPoint point;
                float value, x, y;
                Sole left = lSoleList.get(timeCnt);
                Sole right = rSoleList.get(timeCnt);

                // For total force => center of pressure
                // my X == their +/-Y   --- my Y == their -X

                // Left Sole  ======================================================================
                for (int i = 0; i < 16; i++) {
                    value = 10*Float.parseFloat(left.getValueByPosition(i+1));
                    if (value > 50.0f) value = 50.0f;
                    if (value == 0.0f) value = 0.0000001f;
                    point = new HeatMap.DataPoint(pointsX[i]/maxX, pointsY[i] / maxY, value);
                    heatMap.addData(point);
                }
                // Pressure Center point
                x = (pointsX[16] / maxX)+(Float.parseFloat(left.getValueByPosition(24))/2);
                y = (pointsY[16] / maxY)-Float.parseFloat(left.getValueByPosition(23));
                value = Float.parseFloat(left.getGtf());
                point = new HeatMap.DataPoint(x, y, 0.0f);
                heatMap.addData(point);

                // Right Sole  =====================================================================
                for (int i = 0; i < 16; i++) {
                    value = 10*Float.parseFloat(right.getValueByPosition(i+1));
                    if (value > 50.0f) value = 50.0f;
                    if (value == 0.0f) value = 0.0000001f;
                    point = new HeatMap.DataPoint((maxX - pointsX[i]) / maxX, pointsY[i] / maxY, value);
                    heatMap.addData(point);
                }
                // Pressure Center point
                x = ((maxX - pointsX[16]) / maxX)-(Float.parseFloat(right.getValueByPosition(24))/2);
                y = (pointsY[16] / maxY)-Float.parseFloat(right.getValueByPosition(23));
                value = Float.parseFloat(right.getGtf());
                point = new HeatMap.DataPoint(x, y, 0.0f);
                heatMap.addData(point);

                // Refresh heatMap 10Hz
                heatMap.forceRefresh();

                timeCnt++;
            } else {
                lSoleList.clear();
                rSoleList.clear();
            }
            handler.postDelayed(dataUpdater, 10);
        }
    };

    /**
     * Class - Override drawMarker so it draws a marker only on pressure center points
     */
    class MyMarker implements HeatMapMarkerCallback {

        private Paint paint = new Paint();

        @Override
        public void drawMarker(Canvas canvas, float x, float y, HeatMap.DataPoint point) {

            if (point.value == 0.0f) {
                paint.setColor(0xff0911ed);
                canvas.drawCircle(x, y, 5, paint);   //  (int) point.value/30
            }
        }
    }

    /**
     * Read .txt with real data from moticon soles
     */
    private boolean readTxtData(File file){
        lSoleList.clear();
        rSoleList.clear();

        int cntLine = 0;

        //Read text from file
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                cntLine++;
                if (cntLine >3) {

                    Sole soleL, soleR;
                    String[] measures = line.split("\t");
                    for (int i=0; i<measures.length; i++){
                        if (measures[i].equals("") || measures[i]==null){
                            measures[i]="0.0";
                        }
                    }

                    // gf == total force == measures[23]
                    // magnX == measures[24] == center of pressure[%] X axis
                    // magnY == measures[25] == center of pressure[%] Y axis

                    soleL = new Sole(cntLine, "soleL", "userId", measures[0], "LEFT", "NO_SYNC",
                            measures[17], measures[18], measures[19], measures[20], measures[21], measures[22],
                            measures[24],measures[25],"0",
                            measures[1], measures[2], measures[3], measures[4], measures[5], measures[6],
                            measures[7], measures[8], measures[9], measures[10], measures[11], measures[12],
                            measures[13], measures[14], measures[15], measures[16], measures[23]);

                    lSoleList.add(soleL);

                    soleR = new Sole(cntLine, "soleR", "userId", measures[0], "RIGHT", "NO_SYNC",
                            measures[17], measures[18], measures[19], measures[20], measures[21], measures[22],
                            measures[24],measures[25],"0",
                            measures[1], measures[2], measures[3], measures[4], measures[5], measures[6],
                            measures[7], measures[8], measures[9], measures[10], measures[11], measures[12],
                            measures[13], measures[14], measures[15], measures[16], measures[23]);

                    rSoleList.add(soleR);
                }
            }
            br.close();
        }
        catch (IOException e) {
            Log.v("Load", String.valueOf(e));
            return false;
        }
        return true;
    }

    /**
     * Create a grid and fill it with random data to be projected in heatmap
     */
//    private void createData() {
//        // create a grid n x n
//        int n = 10;
//        float stepX = (maxX/n)/maxX;        // grid step
//        float stepY = (maxY/n)/maxY;
//
//        // Fill the grid with random values
//        for (int x=1; x <= n; x++){
//            for (int y=1; y <= n; y++) {
//                HeatMap.DataPoint point = new HeatMap.DataPoint(x*stepX-(stepX/2), y*stepY-(stepY/2), rand.nextDouble() * 100.0);
//                heatMap.addData(point);
//            }
//        }
//    }

    /**
     * Recreate data
     */
//    private void changeData(){
//        createData();
//
//        int val = rand.nextInt(101);
//        progressBar1.setProgress(val);
//        txtProgress1.setText(val + " %");
//
//        heatMap.forceRefresh();
//    }

    /**
     * Runnable, it will be executed in a background thread,
     * locks the botNavBar and unlocks it when data formatting and uploading is finished
     */
    private Runnable task1 = new Runnable() {
        @Override
        public void run() {
            mainActivity.bottomNavigationView.findViewById(R.id.action_one).setClickable(false);
            mainActivity.bottomNavigationView.findViewById(R.id.action_two).setClickable(false);
            postTestData(rSoleList, lSoleList);
            mainActivity.bottomNavigationView.findViewById(R.id.action_one).setClickable(true);
            mainActivity.bottomNavigationView.findViewById(R.id.action_two).setClickable(true);
        }
    };

    /**
     * 1) check internet connection
     * 2) create JSON
     * 3) post JSON
     * @param solesR List with right soles
     * @param solesL List with left soles
     * @return return true if
     */
    private boolean postTestData(List<Sole> solesR, List<Sole> solesL){
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            syncHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "Can't upload data. No internet.", Toast.LENGTH_SHORT).show();
                }
            });
            return false;
        }


        Random rand = new Random();

        // Create JSON packet
        final JSONObject packet = new JSONObject();
        User user = SharedPrefManager.getInstance(mainActivity).getUser();

        final String startTimestamp = "2020-02-27T09:59:21.220Z";
        final String stopTimestamp = "2020-02-27T09:59:55.549Z";

        if (solesR == null && solesL == null) return false;
        assert solesL != null;
        assert solesR != null;
        int soleSizeR = solesR.size();
        int soleSizeL = solesL.size();

        int samples = 1200;
        int packets_numR = (int) Math.ceil((double) soleSizeR / samples);
        int packets_numL = (int) Math.ceil((double) soleSizeL / samples);

        // number of max packets to be send
        int max_packet = Math.max(packets_numR,packets_numL);
        countDownLatch = new CountDownLatch(max_packet);
        final float progress = 100.0f / max_packet;

        String url =  getString(R.string.results_post_url);
        postJsonRequestQueue = new PostJsonRequestQueue(mainActivity, url, countDownLatch);
        VolleySingleton.getInstance(mainActivity).setError(false);

        for (int j=0; j<max_packet; j++) {
            JSONObject session = new JSONObject();
            try{
                int rand_int1 = rand.nextInt(1000000);
                session.put("sessionid", rand_int1);
                session.put("patientid", user.getUsername());
                session.put("insoleid", user.getUsername());
                session.put("starttime", startTimestamp);
                session.put("endtime", stopTimestamp);
                //session.put("pack_num", j);
                session.put("isPart", true);
                session.put("sessionType", 0);
            } catch (JSONException e){
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

            int start = j*samples;

            if (j < packets_numR) {
                int stopR = (j+1)*samples;
                if (stopR >= soleSizeR) stopR = soleSizeR;      // -1 exclusive
                sR = solesR.subList(start, stopR);
            } else {
                sR = null;
            }
            if (j < packets_numL) {
                int stopL = (j+1)*samples;
                if (stopL >= soleSizeL) stopL = soleSizeL;      // -1 exclusive
                sL = solesL.subList(start, stopL);
            } else {
                sL = null;
            }

            JSONArray jSONArray = jsonPacketManager.packetTxt(sL, sR);
            try {
                packet.put("data", jSONArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Uncomment for test
            //jsonPacketManager.writeToTXT(packet, "test_"+j+".txt");

            // Post data using VolleySingleton - Suggested by designers
            final int finalJ = j+1;
            postJsonRequestQueue.postJSONRequest(packet, new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    progressBar1.setProgress((int) (progress*finalJ));
                    txtProgress1.setText((int) (progress*finalJ) + " %");
                    Toast.makeText(mainActivity, "Post Packet: "+ finalJ +" Done.", Toast.LENGTH_SHORT).show();
                    Log.d("JSON REQUEST RESULT S: ", result.toString());
                }

                @Override
                public void onError(String result) {
                    //progressBar1.setProgress((int) (progress*finalJ));
                    //txtProgress1.setText((int) (progress*finalJ) + " %");
                    Toast.makeText(mainActivity, "Post Packet: "+ finalJ +" Not Done.", Toast.LENGTH_SHORT).show();
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
            Log.v("Load", String.valueOf(e));
            return false;
        }

        return true;
    }

}

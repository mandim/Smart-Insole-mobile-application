package info.smartinsole.sqlite.view;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import info.smartinsole.sqlite.R;
import info.smartinsole.sqlite.database.model.Sole;

import static java.lang.Math.max;

public class JSONPacketManager {

    private Random rand = new Random();
    private MainActivity mainActivity;
    private static final String SAMPLE_CSV_FILE_PATH = "./insoletable.csv";

    public JSONPacketManager(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    /**
     * Create a JSON Frame with Data from booth soles
     */
    private JSONObject createJSONFrame(String[][] sR, String[][] sL){

        JSONObject jsonPacket = new JSONObject();

        // Create JSON with ALL Data
        try {
            jsonPacket.put("timestampR","["+convertObjectArrayToString(sR[0],",")+"]");
            // Right Sole
            // Acccel Range ±16g
            jsonPacket.put("accRX","["+convertObjectArrayToString(sR[1],",")+"]");
            jsonPacket.put("accRY","["+convertObjectArrayToString(sR[2],",")+"]");
            jsonPacket.put("accRZ","["+convertObjectArrayToString(sR[3],",")+"]");
            // Gyro Range  ±2000 degrees/sec
            // 50Hz => 200 = 40
            jsonPacket.put("gyroRX","["+convertObjectArrayToString(sR[4],",")+"]");
            jsonPacket.put("gyroRY","["+convertObjectArrayToString(sR[5],",")+"]");
            jsonPacket.put("gyroRZ","["+convertObjectArrayToString(sR[6],",")+"]");
            // Magn Range ±100 µT
            jsonPacket.put("magRX","["+convertObjectArrayToString(sR[7],",")+"]");
            jsonPacket.put("magRY","["+convertObjectArrayToString(sR[8],",")+"]");
            jsonPacket.put("magRZ","["+convertObjectArrayToString(sR[9],",")+"]");
            // Add pressure
            jsonPacket.put("pR01","["+convertObjectArrayToString(sR[10],",")+"]");
            jsonPacket.put("pR02","["+convertObjectArrayToString(sR[11],",")+"]");
            jsonPacket.put("pR03","["+convertObjectArrayToString(sR[12],",")+"]");
            jsonPacket.put("pR04","["+convertObjectArrayToString(sR[13],",")+"]");
            jsonPacket.put("pR05","["+convertObjectArrayToString(sR[14],",")+"]");
            jsonPacket.put("pR06","["+convertObjectArrayToString(sR[15],",")+"]");
            jsonPacket.put("pR07","["+convertObjectArrayToString(sR[16],",")+"]");
            jsonPacket.put("pR08","["+convertObjectArrayToString(sR[17],",")+"]");
            jsonPacket.put("pR09","["+convertObjectArrayToString(sR[18],",")+"]");
            jsonPacket.put("pR10","["+convertObjectArrayToString(sR[19],",")+"]");
            jsonPacket.put("pR11","["+convertObjectArrayToString(sR[20],",")+"]");
            jsonPacket.put("pR12","["+convertObjectArrayToString(sR[21],",")+"]");
            jsonPacket.put("pR13","["+convertObjectArrayToString(sR[22],",")+"]");
            jsonPacket.put("pR14","["+convertObjectArrayToString(sR[23],",")+"]");
            jsonPacket.put("pR15","["+convertObjectArrayToString(sR[24],",")+"]");
            jsonPacket.put("pR16","["+convertObjectArrayToString(sR[25],",")+"]");
            jsonPacket.put("gtfR","["+convertObjectArrayToString(sR[26],",")+"]");
            // Left Sole
            jsonPacket.put("timestampL","["+convertObjectArrayToString(sL[0],",")+"]");
            jsonPacket.put("accLX","["+convertObjectArrayToString(sL[1],",")+"]");
            jsonPacket.put("accLY","["+convertObjectArrayToString(sL[2],",")+"]");
            jsonPacket.put("accLZ","["+convertObjectArrayToString(sL[3],",")+"]");
            // Gyro Range  ±2000 degrees/sec
            // 50Hz => 200 = 40
            jsonPacket.put("gyroLX","["+convertObjectArrayToString(sL[4],",")+"]");
            jsonPacket.put("gyroLY","["+convertObjectArrayToString(sL[5],",")+"]");
            jsonPacket.put("gyroLZ","["+convertObjectArrayToString(sL[6],",")+"]");
            // Magn Range ±100 µT
            jsonPacket.put("magLX","["+convertObjectArrayToString(sL[7],",")+"]");
            jsonPacket.put("magLY","["+convertObjectArrayToString(sL[8],",")+"]");
            jsonPacket.put("magLZ","["+convertObjectArrayToString(sL[9],",")+"]");
            // Add pressure
            jsonPacket.put("pL01","["+convertObjectArrayToString(sL[10],",")+"]");
            jsonPacket.put("pL02","["+convertObjectArrayToString(sL[11],",")+"]");
            jsonPacket.put("pL03","["+convertObjectArrayToString(sL[12],",")+"]");
            jsonPacket.put("pL04","["+convertObjectArrayToString(sL[13],",")+"]");
            jsonPacket.put("pL05","["+convertObjectArrayToString(sL[14],",")+"]");
            jsonPacket.put("pL06","["+convertObjectArrayToString(sL[15],",")+"]");
            jsonPacket.put("pL07","["+convertObjectArrayToString(sL[16],",")+"]");
            jsonPacket.put("pL08","["+convertObjectArrayToString(sL[17],",")+"]");
            jsonPacket.put("pL09","["+convertObjectArrayToString(sL[18],",")+"]");
            jsonPacket.put("pL10","["+convertObjectArrayToString(sL[19],",")+"]");
            jsonPacket.put("pL11","["+convertObjectArrayToString(sL[20],",")+"]");
            jsonPacket.put("pL12","["+convertObjectArrayToString(sL[21],",")+"]");
            jsonPacket.put("pL13","["+convertObjectArrayToString(sL[22],",")+"]");
            jsonPacket.put("pL14","["+convertObjectArrayToString(sL[23],",")+"]");
            jsonPacket.put("pL15","["+convertObjectArrayToString(sL[24],",")+"]");
            jsonPacket.put("pL16","["+convertObjectArrayToString(sL[25],",")+"]");
            jsonPacket.put("gtfL","["+convertObjectArrayToString(sL[26],",")+"]");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonPacket;
    }

    /**
     * Converts data to json, if no data from one sole => sole == null, then []
     * @param soleR Right Sole List Data
     * @param soleL Left Sole List Data
     * @return JSOBObject with data from both soles
     */
    public JSONObject packet(List<Sole> soleR, List<Sole> soleL){
        JSONObject data;
        String[][] empty = {{""}, {""}, {""}, {""}, {""}, {""},{""}, {""}, {""},{""}, {""}, {""}, {""},
                {""}, {""}, {""}, {""}, {""},{""}, {""}, {""},{""}, {""}, {""},{""}, {""}, {""}, {""}};

        String[][] sR, sL;
        if (soleR != null) {
            sR = makeArray(soleR);
        } else {
            sR = empty;
        }
        if (soleL != null) {
            sL = makeArray(soleL);
        } else {
            sL = empty;
        }
        data = createJSONFrame(sR, sL);

        return data;
    }

    public JSONObject packet(){
        Random rand = new Random();
        int rand_int1 = rand.nextInt(1000000);
        JSONObject data = null;
        String test= "{  \"product\": \"smartinsole\",  \"version\": 0.1,  \"releaseDate\": \"2020-12-14T08:53:39.704Z\",  \"demo\": true,  \"session\": {    \"sessionId\": "+rand_int1+",    \"patientId\": \"testdev1c3\",    \"insoleId\": \"tests0l3\",    \"startTime\": \"2020-10-14T08:50:39.704Z\",    \"endTime\": \"2020-10-14T08:53:39.704Z\",    \"isPart\": true,    \"sessionType\": 0  },  \"data\": [    {      \"timestamp\": 0,      \"accLx\": 0,      \"accLy\": 0,      \"accLz\": 0,      \"gyroLx\": 0,      \"gyroLy\": 0,      \"gyroLz\": 0,      \"pL01\": 0,      \"pL02\": 0,      \"pL03\": 0,      \"pL04\": 0,      \"pL05\": 0,      \"pL06\": 0,      \"pL07\": 0,      \"pL08\": 0,      \"pL09\": 0,      \"pL10\": 0,      \"pL11\": 0,      \"pL12\": 0,      \"pL13\": 0,      \"pL14\": 0,      \"pL15\": 0,      \"pL16\": 0,      \"grfL\": 0,      \"accRx\": 0,      \"accRy\": 0,      \"accRz\": 0,      \"gyroRx\": 0,      \"gyroRy\": 0,      \"gyroRz\": 0,      \"pR01\": 0,      \"pR02\": 0,      \"pR03\": 0,      \"pR04\": 0,      \"pR05\": 0,      \"pR06\": 0,      \"pR07\": 0,      \"pR08\": 0,      \"pR09\": 0,      \"pR10\": 0,      \"pR11\": 0,      \"pR12\": 0,      \"pR13\": 0,      \"pR14\": 0,      \"pR15\": 0,      \"pR16\": 0,      \"gffR\": 0    }  ]}";
        try{
            data = new JSONObject(test);
        } catch (JSONException err){
            Log.d("Error", err.toString());
        }

        return data;
    }

    /**
     * Object array to string
     */
    private static String convertObjectArrayToString(Object[] arr, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : arr)
            sb.append(obj.toString()).append(delimiter);
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * Return data from soles as String Arrays
     */
    private String[][] makeArray(List<Sole> soles){
        String[][] pack = new String[27][];
        int size = soles.size();
        String[] frameTimestamp, accelX, accelY, accelZ, gyroX, gyroY, gyroZ, magX,
                magY, magZ, pe1, pe2, pe3, pe4, pe5, pe6, pe7, pe8, pe9, pe10, pe11, pe12, pe13, pe14, pe15, pe16, gtf;
        frameTimestamp = new String[size];
        accelX = new String[size]; accelY = new String[size]; accelZ = new String[size];
        gyroX = new String[size]; gyroY = new String[size]; gyroZ = new String[size];
        magX = new String[size]; magY = new String[size]; magZ = new String[size];
        pe1 = new String[size]; pe2 = new String[size]; pe3 = new String[size]; pe4 = new String[size];
        pe5 = new String[size]; pe6 = new String[size]; pe7 = new String[size]; pe8 = new String[size];
        pe9 = new String[size]; pe10 = new String[size]; pe11 = new String[size]; pe12 = new String[size];
        pe13 = new String[size]; pe14 = new String[size]; pe15 = new String[size]; pe16 = new String[size]; gtf = new String[size];

        for (int i=0; i<size; i++){
            frameTimestamp[i] = soles.get(i).getTimestamp();
            accelX[i] = soles.get(i).getAccx();
            accelY[i] = soles.get(i).getAccy();
            accelZ[i] = soles.get(i).getAccz();
            gyroX[i] = soles.get(i).getGyropoll();
            gyroY[i] = soles.get(i).getGyropitch();
            gyroZ[i] = soles.get(i).getGyroyaw();
            magX[i] = soles.get(i).getMagnx();
            magY[i] = soles.get(i).getMagny();
            magZ[i] = soles.get(i).getMagnz();
            pe1[i] = soles.get(i).getPe1();
            pe2[i] = soles.get(i).getPe2();
            pe3[i] = soles.get(i).getPe3();
            pe4[i] = soles.get(i).getPe4();
            pe5[i] = soles.get(i).getPe5();
            pe6[i] = soles.get(i).getPe6();
            pe7[i] = soles.get(i).getPe7();
            pe8[i] = soles.get(i).getPe8();
            pe9[i] = soles.get(i).getPe9();
            pe10[i] = soles.get(i).getPe10();
            pe11[i] = soles.get(i).getPe11();
            pe12[i] = soles.get(i).getPe12();
            pe13[i] = soles.get(i).getPe13();
            pe14[i] = soles.get(i).getPe14();
            pe15[i] = soles.get(i).getPe15();
            pe16[i] = soles.get(i).getPe16();
            gtf[i] = soles.get(i).getGtf();
        }
        pack[0] = frameTimestamp;
        pack[1] = accelX;
        pack[2] = accelY;
        pack[3] = accelZ;
        pack[4] = gyroX;
        pack[5] = gyroY;
        pack[6] = gyroZ;
        pack[7] = magX;
        pack[8] = magY;
        pack[9] = magZ;
        pack[10] = pe1;
        pack[11] = pe2;
        pack[12] = pe3;
        pack[13] = pe4;
        pack[14] = pe5;
        pack[15] = pe6;
        pack[16] = pe7;
        pack[17] = pe8;
        pack[18] = pe9;
        pack[19] = pe10;
        pack[20] = pe11;
        pack[21] = pe12;
        pack[22] = pe13;
        pack[23] = pe14;
        pack[24] = pe15;
        pack[25] = pe16;
        pack[26] = gtf;

        return pack;
    }

    // For Testing purpose =========================================================================

    /**
     * Generate JSON with Random data
     * @param rl_sole String that indicate which sole send the data
     * @return JSON with measures from one sole
     */
    public JSONObject generateDummySoleData(String rl_sole){


        JSONObject jsonFrame = new JSONObject();

        Date timeStamp = new Date();

        // generate ADC data (4 pressure points)
        double[] pressure = new double[16];
        for (int i = 0; i < 16; i++) {
            pressure[i] = Math.round((rand.nextDouble()*10.0-5.0)*100)/100d;
        }

        try {
            jsonFrame.put("timestamp", new Timestamp(timeStamp.getTime()));
            jsonFrame.put("RLsole", rl_sole);
            // Acccel Range ±16g
            jsonFrame.put("accelX", Math.round((rand.nextDouble()*32.0-16.0)*1000000)/1000000d);
            jsonFrame.put("accelY", Math.round((rand.nextDouble()*32.0-16.0)*1000000)/1000000d);
            jsonFrame.put("accelZ", Math.round((rand.nextDouble()*32.0-16.0)*1000000)/1000000d);
            // Gyro Range  ±2000 degrees/sec
            // 50Hz => 200 = 40
            jsonFrame.put("gyroX", Math.round((rand.nextDouble()*80.0-40.0)*1000000)/1000000d);
            jsonFrame.put("gyroY", Math.round((rand.nextDouble()*80.0-40.0)*1000000)/1000000d);
            jsonFrame.put("gyroZ", Math.round((rand.nextDouble()*80.0-40.0)*1000000)/1000000d);
            // Magn Range ±100 µT
            jsonFrame.put("magX", Math.round((rand.nextDouble()*200.0-100.0)*1000000)/1000000d);
            jsonFrame.put("magY", Math.round((rand.nextDouble()*200.0-100.0)*1000000)/1000000d);
            jsonFrame.put("magZ", Math.round((rand.nextDouble()*200.0-100.0)*1000000)/1000000d);
            // Add pressure
            jsonFrame.put("pressure", new JSONArray(pressure));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonFrame;
    }



    /**
     * Generate JSON with Random data
     * @param rl_sole String that indicate which sole send the data
     * @return JSON with measures from one sole
     */
    public JSONObject readSCVSoleData(String rl_sole)
    {


        JSONObject jsonFrame = new JSONObject();

        Date timeStamp = new Date();

        // generate ADC data (4 pressure points)
        double[] pressure = new double[16];
        for (int i = 0; i < 16; i++) {
            pressure[i] = Math.round((rand.nextDouble()*10.0-5.0)*100)/100d;
        }

        try {
            jsonFrame.put("timestamp", new Timestamp(timeStamp.getTime()));
            jsonFrame.put("RLsole", rl_sole);
            // Acccel Range ±16g
            jsonFrame.put("accelX", Math.round((rand.nextDouble()*32.0-16.0)*1000000)/1000000d);
            jsonFrame.put("accelY", Math.round((rand.nextDouble()*32.0-16.0)*1000000)/1000000d);
            jsonFrame.put("accelZ", Math.round((rand.nextDouble()*32.0-16.0)*1000000)/1000000d);
            // Gyro Range  ±2000 degrees/sec
            // 50Hz => 200 = 40
            jsonFrame.put("gyroX", Math.round((rand.nextDouble()*80.0-40.0)*1000000)/1000000d);
            jsonFrame.put("gyroY", Math.round((rand.nextDouble()*80.0-40.0)*1000000)/1000000d);
            jsonFrame.put("gyroZ", Math.round((rand.nextDouble()*80.0-40.0)*1000000)/1000000d);
            // Magn Range ±100 µT
            jsonFrame.put("magX", Math.round((rand.nextDouble()*200.0-100.0)*1000000)/1000000d);
            jsonFrame.put("magY", Math.round((rand.nextDouble()*200.0-100.0)*1000000)/1000000d);
            jsonFrame.put("magZ", Math.round((rand.nextDouble()*200.0-100.0)*1000000)/1000000d);
            // Add pressure
            jsonFrame.put("pressure", new JSONArray(pressure));

        } catch (JSONException e) {
            e.printStackTrace();
        }


//        try {
//            jsonFrame.getJSONArray(String.valueOf(list));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        return jsonFrame;
    }

    /**
     * Create TXT file
     * @param jsonPacket JSON with ALL data
     * @param fName File name
     */
    public void writeToTXT(JSONObject jsonPacket, String fName){
        File file = new File(mainActivity.getExternalFilesDir(null), "sample");
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            File gpxfile = new File(file, "json"+fName+".txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(jsonPacket.toString(4));
            writer.flush();
            writer.close();
            //System.out.println(jsonPacket.toString(4));
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    // Upload .txt data ============================================================================

    /**
     * Create JSON in old format
     * @param soleL List with left soles
     * @param soleR List with Right sole
     * @return JSONArray with data, each measure is a different JSONObject
     */
    public JSONArray packetTxt(List<Sole> soleL, List<Sole> soleR){
        JSONArray data = new JSONArray();

        Sole emptySole = new Sole(0, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
                "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
        int sizeL = soleL.size();
        int sizeR = soleR.size();

        int size = max(sizeL, sizeR);
        for (int i=0; i<size; i++) {
            JSONObject jsonPacket = new JSONObject();
            Sole sL, sR;
            if (i < sizeL) {
                sL = soleL.get(i);
            } else {
                sL = emptySole;}
            if (i < sizeR) {
                sR = soleR.get(i);
            } else {
                sR = emptySole;
            }

            try {
                jsonPacket.put("timestamp", sL.getTimestamp());
                jsonPacket.put("accLX", sL.getAccx());
                jsonPacket.put("accLY", sL.getAccy());
                jsonPacket.put("accLZ", sL.getAccz());
                jsonPacket.put("gyroLX", sL.getGyropoll());
                jsonPacket.put("gyroLY", sL.getGyropitch());
                jsonPacket.put("gyroLZ", sL.getGyroyaw());
                jsonPacket.put("pL01", sL.getPe1());
                jsonPacket.put("pL02", sL.getPe2());
                jsonPacket.put("pL03", sL.getPe3());
                jsonPacket.put("pL04", sL.getPe4());
                jsonPacket.put("pL05", sL.getPe5());
                jsonPacket.put("pL06", sL.getPe6());
                jsonPacket.put("pL07", sL.getPe7());
                jsonPacket.put("pL08", sL.getPe8());
                jsonPacket.put("pL09", sL.getPe9());
                jsonPacket.put("pL10", sL.getPe10());
                jsonPacket.put("pL11", sL.getPe11());
                jsonPacket.put("pL12", sL.getPe12());
                jsonPacket.put("pL13", sL.getPe13());
                jsonPacket.put("pL14", sL.getPe14());
                jsonPacket.put("pL15", sL.getPe15());
                jsonPacket.put("pL16", sL.getPe16());
                jsonPacket.put("grfL", "0");

                // Right Sole
                jsonPacket.put("accRX", sR.getAccx());
                jsonPacket.put("accRY", sR.getAccy());
                jsonPacket.put("accRZ", sR.getAccz());
                jsonPacket.put("gyroRX", sR.getGyropoll());
                jsonPacket.put("gyroRY", sR.getGyropitch());
                jsonPacket.put("gyroRZ", sR.getGyroyaw());
                jsonPacket.put("pR01", sR.getPe1());
                jsonPacket.put("pR02", sR.getPe2());
                jsonPacket.put("pR03", sR.getPe3());
                jsonPacket.put("pR04", sR.getPe4());
                jsonPacket.put("pR05", sR.getPe5());
                jsonPacket.put("pR06", sR.getPe6());
                jsonPacket.put("pR07", sR.getPe7());
                jsonPacket.put("pR08", sR.getPe8());
                jsonPacket.put("pR09", sR.getPe9());
                jsonPacket.put("pR10", sR.getPe10());
                jsonPacket.put("pR11", sR.getPe11());
                jsonPacket.put("pR12", sR.getPe12());
                jsonPacket.put("pR13", sR.getPe13());
                jsonPacket.put("pR14", sR.getPe14());
                jsonPacket.put("pR15", sR.getPe15());
                jsonPacket.put("pR16", sR.getPe16());
                jsonPacket.put("grfR", "0");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            data.put(jsonPacket);
        }
        return data;
    }
}

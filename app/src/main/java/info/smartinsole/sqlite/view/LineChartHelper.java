package info.smartinsole.sqlite.view;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Random;

import info.smartinsole.sqlite.database.model.Sole;

/**
 * This class is used to Generate Random data and send them to
 * ScrollingChart Fragment to be drawn
 */
public class LineChartHelper {

    public LineChartHelper(){
        // Empty Constructor
    }

    /**
     * Create Random data and add them to TEST and to Accelerometer Line Chart
     * @param lineFragment The Line Chart fragment that is displayed now
     * @param position the sole (position in soleList) to add the data
     */
    protected void addAccToTest(List<Sole> soleList, ScrollingChartFragment lineFragment, int position) throws JSONException {
        Sole n = soleList.get(position);

        // Create new Random Data
        final Random r = new Random();
        double AcclX = 30 * r.nextDouble();
        double AcclY = 30 * r.nextDouble();
        double AcclZ = 30 * r.nextDouble();

        // Get Data from db
        String strAcclX = n.getAccx();
        String strAcclY = n.getAccy();
        String strAcclZ = n.getAccz();

        JSONObject jsonX = null, jsonY = null, jsonZ = null;

        // If data exists --> copy them and add new value to List
        if (!strAcclX.equals("ACCLX") && !strAcclY.equals("ACCLY") && !strAcclZ.equals("ACCLZ")) {
            try {
                jsonX = new JSONObject(strAcclX);
                jsonY = new JSONObject(strAcclY);
                jsonZ = new JSONObject(strAcclZ);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray tempArrayX = jsonX.getJSONArray("AcclX");
            tempArrayX.put(AcclX);
            JSONArray xArray = new JSONArray(tempArrayX.toString());
            jsonX.put("AcclX", xArray);

            JSONArray tempArrayY = jsonY.getJSONArray("AcclY");
            tempArrayY.put(AcclY);
            JSONArray yArray = new JSONArray(tempArrayY.toString());
            jsonY.put("AcclY", yArray);

            JSONArray tempArrayZ = jsonZ.getJSONArray("AcclZ");
            tempArrayZ.put(AcclZ);
            JSONArray zArray = new JSONArray(tempArrayZ.toString());
            jsonZ.put("AcclZ", zArray);

        } else { // If no data has been inserted
            jsonX = new JSONObject();
            jsonY = new JSONObject();
            jsonZ = new JSONObject();

            JSONArray xArray = new JSONArray();
            xArray.put(AcclX);
            jsonX.put("AcclX", xArray);

            JSONArray yArray = new JSONArray();
            yArray.put(AcclY);
            jsonY.put("AcclY", yArray);

            JSONArray zArray = new JSONArray();
            zArray.put(AcclZ);
            jsonZ.put("AcclZ", zArray);
        }

        String arrayListX = jsonX.toString();
        String arrayListY = jsonY.toString();
        String arrayListZ = jsonZ.toString();

        // Add to db
        n.setAccx(arrayListX);
        n.setAccy(arrayListY);
        n.setAccz(arrayListZ);

        lineFragment.insertDataToAcc(AcclX, AcclY, AcclZ);
    }

    /**
     * Create Random data and add them to TEST and to Gyroscope Line Chart
     * @param lineFragment The Line Chart fragment that is displayed now
     * @param position the sole (position in soleList) to add the data
     */
    protected void addGyroToTest(List<Sole> soleList, ScrollingChartFragment lineFragment, int position) throws JSONException {
        Sole n = soleList.get(position);

        // Create new Random Data
        final Random r = new Random();
        double GyroR = 30 * r.nextDouble();
        double GyroP = 30 * r.nextDouble();
        double GyroY = 30 * r.nextDouble();

        // Get Data from db
        String strGyroR = n.getGyropoll();
        String strGyroP = n.getGyropitch();
        String strGyroY = n.getGyroyaw();

        JSONObject jsonR = null, jsonP = null, jsonY = null;

        // If data exists --> copy them and add new value to List
        if (!strGyroR.equals("GYROROLL") && !strGyroP.equals("GYROPITCH") && !strGyroY.equals("GYROYAW")) {
            try {
                jsonR = new JSONObject(strGyroR);
                jsonP = new JSONObject(strGyroP);
                jsonY = new JSONObject(strGyroY);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray tempArrayR = jsonR.getJSONArray("GYROROLL");
            tempArrayR.put(GyroR);
            JSONArray rArray = new JSONArray(tempArrayR.toString());
            jsonR.put("GYROROLL", rArray);

            JSONArray tempArrayP = jsonP.getJSONArray("GYROPITCH");
            tempArrayP.put(GyroP);
            JSONArray pArray = new JSONArray(tempArrayP.toString());
            jsonP.put("GYROPITCH", pArray);

            JSONArray tempArrayY = jsonY.getJSONArray("GYROYAW");
            tempArrayY.put(GyroY);
            JSONArray yArray = new JSONArray(tempArrayY.toString());
            jsonY.put("GYROYAW", yArray);

        } else { // If no data has been inserted
            jsonR = new JSONObject();
            jsonP = new JSONObject();
            jsonY = new JSONObject();

            JSONArray rArray = new JSONArray();
            rArray.put(GyroR);
            jsonR.put("GYROROLL", rArray);

            JSONArray pArray = new JSONArray();
            pArray.put(GyroP);
            jsonP.put("GYROPITCH", pArray);

            JSONArray yArray = new JSONArray();
            yArray.put(GyroY);
            jsonY.put("GYROYAW", yArray);
        }

        String arrayListR = jsonR.toString();
        String arrayListP = jsonP.toString();
        String arrayListY = jsonY.toString();

        // Add to db
        n.setGyropoll(arrayListR);
        n.setGyropitch(arrayListP);
        n.setGyroyaw(arrayListY);

        lineFragment.insertDataToGyro(GyroR, GyroP, GyroY);
    }

    /**
     * Create Random data and add them to TEST and to Magnetometer Line Chart
     * @param lineFragment The Line Chart fragment that is displayed now
     * @param position the sole (position in soleList) to add the data
     */
    protected void addMagnToTest(List<Sole> soleList, ScrollingChartFragment lineFragment, int position) throws JSONException {
        Sole n = soleList.get(position);

        // Create new Random Data
        final Random r = new Random();
        double MagnX = 30 * r.nextDouble();
        double MagnY = 30 * r.nextDouble();
        double MagnZ = 30 * r.nextDouble();

        // Get Data from db
        String strMagnX = n.getMagnx();
        String strMagnY = n.getMagny();
        String strMagnZ = n.getMagnz();

        JSONObject jsonX = null, jsonY = null, jsonZ = null;

        // If data exists --> copy them and add new value to List
        if (!strMagnX.equals("MAGNXAXIS") && !strMagnY.equals("MAGNYAXIS") && !strMagnZ.equals("MAGNZAXIS")) {
            try {
                jsonX = new JSONObject(strMagnX);
                jsonY = new JSONObject(strMagnY);
                jsonZ = new JSONObject(strMagnZ);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray tempArrayX = jsonX.getJSONArray("MAGNXAXIS");
            tempArrayX.put(MagnX);
            JSONArray xArray = new JSONArray(tempArrayX.toString());
            jsonX.put("MAGNXAXIS", xArray);

            JSONArray tempArrayY = jsonY.getJSONArray("MAGNYAXIS");
            tempArrayY.put(MagnY);
            JSONArray yArray = new JSONArray(tempArrayY.toString());
            jsonY.put("MAGNYAXIS", yArray);

            JSONArray tempArrayZ = jsonZ.getJSONArray("MAGNZAXIS");
            tempArrayZ.put(MagnZ);
            JSONArray zArray = new JSONArray(tempArrayZ.toString());
            jsonZ.put("MAGNZAXIS", zArray);

        } else { // If no data has been inserted
            jsonX = new JSONObject();
            jsonY = new JSONObject();
            jsonZ = new JSONObject();

            JSONArray xArray = new JSONArray();
            xArray.put(MagnX);
            jsonX.put("MAGNXAXIS", xArray);

            JSONArray yArray = new JSONArray();
            yArray.put(MagnY);
            jsonY.put("MAGNYAXIS", yArray);

            JSONArray zArray = new JSONArray();
            zArray.put(MagnZ);
            jsonZ.put("MAGNZAXIS", zArray);
        }

        String arrayListX = jsonX.toString();
        String arrayListY = jsonY.toString();
        String arrayListZ = jsonZ.toString();

        // Add to db
        n.setMagnx(arrayListX);
        n.setMagny(arrayListY);
        n.setMagnz(arrayListZ);

        lineFragment.insertDataToMagn(MagnX, MagnY, MagnZ);
    }

    /*protected void initBtnUpload(final Test test,  final ScrollingChartFragment lineFrag){
        Button btnUpload = lineFrag.requireActivity().findViewById(R.id.btn);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Get Data from db
                // Accelerometer Data
                String strAcclX = test.getAccx();
                String strAcclY = test.getAccy();
                String strAcclZ = test.getAccz();

                Object[] objAccX = toObjectArray(strAcclX);
                Object[] objAccY = toObjectArray(strAcclY);
                Object[] objAccZ = toObjectArray(strAcclZ);

                // Gyroscope Data
                String strGyroR = test.getGyropoll();
                String strGyroP = test.getGyropitch();
                String strGyroY = test.getGyroyaw();

                Object[] objGyroR = toObjectArray(strGyroR);
                Object[] objGyroP = toObjectArray(strGyroP);
                Object[] objGyroY = toObjectArray(strGyroY);

                // Magnetometer Data
                String strMagnX = test.getMagnx();
                String strMagnY = test.getMagny();
                String strMagnZ = test.getMagnz();

                Object[] objMagnX = toObjectArray(strMagnX);
                Object[] objMagnY = toObjectArray(strMagnY);
                Object[] objMagnZ = toObjectArray(strMagnZ);

                if (objAccX.length == 0 && objAccY.length == 0 && objAccZ.length == 0 &&
                        objGyroR.length == 0 && objGyroP.length == 0 && objGyroY.length == 0 &&
                        objMagnX.length == 0 && objMagnY.length == 0 && objMagnZ.length == 0){
                    Toast.makeText(lineFrag.getContext(), "No data.", Toast.LENGTH_SHORT).show();
                } else {
                    lineFrag.uploadData(objAccX, objAccY, objAccZ, objGyroR, objGyroP, objGyroY, objMagnX, objMagnY, objMagnZ);
                }
            }
        });
    }*/

    private Object[] toObjectArray(String str){
        // Check if data exist
        if (str == null || str.equals("")){
            return new Object[0];
        }
        String[] temp = str.split(",");

        Object[] newObj = new Object[temp.length];

        for (int i=0;i<temp.length;i++){
            newObj[i] = Double.parseDouble(temp[i]);
        }

        return newObj;
    }

    /**
     * Upload the Data from db to Line Chart
     * @param lineFrag The Line Chart fragment that is displayed now
     * @param pos the sole (position in soleList) to add the data
     */
    /*protected void initBtnUploadJSON(final List<Test> testList, final ScrollingChartFragment lineFrag, final int pos){
        Button btnUpload = lineFrag.requireActivity().findViewById(R.id.btn);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (testList.size() > 0) {
                    Test n = testList.get(pos);

                    // Get Data from db
                    // Accelerometer Data
                    String strAcclX = n.getAccx();
                    String strAcclY = n.getAccy();
                    String strAcclZ = n.getAccz();

                    Object[] objAccX = jsonToObjectArray(strAcclX, "AcclX");
                    Object[] objAccY = jsonToObjectArray(strAcclY, "AcclY");
                    Object[] objAccZ = jsonToObjectArray(strAcclZ, "AcclZ");

                    // Gyroscope Data
                    String strGyroR = n.getGyropoll();
                    String strGyroP = n.getGyropitch();
                    String strGyroY = n.getGyroyaw();

                    Object[] objGyroR = jsonToObjectArray(strGyroR, "GYROROLL");
                    Object[] objGyroP = jsonToObjectArray(strGyroP, "GYROPITCH");
                    Object[] objGyroY = jsonToObjectArray(strGyroY, "GYROYAW");

                    // Magnetometer Data
                    String strMagnX = n.getMagnx();
                    String strMagnY = n.getMagny();
                    String strMagnZ = n.getMagnz();

                    Object[] objMagnX = jsonToObjectArray(strMagnX, "MAGNXAXIS");
                    Object[] objMagnY = jsonToObjectArray(strMagnY, "MAGNYAXIS");
                    Object[] objMagnZ = jsonToObjectArray(strMagnZ, "MAGNZAXIS");

                    if (objAccX.length == 0 && objAccY.length == 0 && objAccZ.length == 0 &&
                            objGyroR.length == 0 && objGyroP.length == 0 && objGyroY.length == 0 &&
                            objMagnX.length == 0 && objMagnY.length == 0 && objMagnZ.length == 0){
                        Toast.makeText(lineFrag.getContext(), "No data.", Toast.LENGTH_SHORT).show();
                    } else {
                        lineFrag.uploadData(objAccX, objAccY, objAccZ, objGyroR, objGyroP, objGyroY, objMagnX, objMagnY, objMagnZ);
                    }
                } else {
                    Toast.makeText(lineFrag.getContext(), "No Sole exists.\nPlease make a new test.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }*/

    /**
     * Convert JSONArray from db to Object[]
     * @param strJSON JSONArray from db (as a String)
     * @param name JSONArray's map name
     * @return Object[] Array
     */
    private Object[] jsonToObjectArray(String strJSON, String name){
        JSONArray tempArray = null;

        try {
            JSONObject jsonX = new JSONObject(strJSON);
            tempArray = jsonX.getJSONArray(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Check if data exist
        if (tempArray == null){
            return new Object[0];
        }
        Object[] newObj = new Object[tempArray.length()];

        for (int i=0;i<tempArray.length();i++){
            try {
                newObj[i] = Double.parseDouble(tempArray.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return newObj;
    }

}

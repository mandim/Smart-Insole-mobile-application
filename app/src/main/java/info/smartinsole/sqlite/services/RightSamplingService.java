package info.smartinsole.sqlite.services;

import android.app.Service;
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
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import info.smartinsole.sqlite.R;
import info.smartinsole.sqlite.database.model.Sole;
import info.smartinsole.sqlite.services.utils.CustomCallable;
import info.smartinsole.sqlite.services.utils.ThreadPoolManager;

import static android.bluetooth.BluetoothGatt.CONNECTION_PRIORITY_HIGH;
import static android.bluetooth.BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
import static android.os.AsyncTask.execute;
import static info.smartinsole.sqlite.view.TestFragment.BLE_SCAN_STATUS_BROADCAST_RECEIVER_INTERRUPT;

public class RightSamplingService extends Service {

    private static final String TAG = "Sampling_Service";
    private final IBinder iBinder = new RightSamplingService.LocalBinder();

    private ThreadPoolManager mThreadPoolManager;

    private static final long SCAN_PERIOD = 20000;

    //Buffer Counter
    int incomecount =0;
    List<Sole> measurementList= new ArrayList<Sole>();

    // MTU Size change
    private int GATT_MAX_MTU_SIZE = 244;

    //private String Right_DEVICE_ADDR = "10:52:1C:50:B3:8A";
    private String Right_DEVICE_ADDR;
    private String defaultAddress = "NO ADDRESS PROVIDED!";
    SharedPreferences sp;


    private final UUID SERVICE_UUID = UUID.fromString("cff6dbb0-996f-427b-9618-9e131a1d6d3f");
    private final UUID DATA_CHARACTERISTIC_UUID = UUID.fromString("e3a534da-19c3-4979-b6dc-c2fca585eabc");
    private final UUID SETUP_CHARACTERISTIC_UUID = UUID.fromString("f0f8c0d7-ac26-441b-85f2-63283e2ff83b");
    private final UUID DATA_DESCRIPTOR_UUID = convertFromInteger(0x2902);

    private boolean leScanStatus;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;

    private Map<String, BluetoothGatt> connectedDeviceMap;
    public String userId;

    private boolean mScanning;
    Handler handler;

    private StringBuilder stringBuilderMeasurementDevice;

    private boolean isConnectedModule = false;

    public class LocalBinder extends Binder {
        public RightSamplingService getService() {
            return RightSamplingService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return iBinder;
    }

    public RightSamplingService() {
        handler = new Handler();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        String PREF_NAME = getBaseContext().getResources().getString(R.string.PREF_NAME);
        String KEY_USERID = getBaseContext().getResources().getString(R.string.KEY_USERID);
        this.userId = getBaseContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(KEY_USERID, "none");
        connectedDeviceMap = new HashMap<>();

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) getBaseContext().getSystemService(Context.BLUETOOTH_SERVICE);
        // Get the BluetoothAdapter
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        stringBuilderMeasurementDevice = new StringBuilder();

        // get the thread pool manager instance
        mThreadPoolManager = ThreadPoolManager.getsInstance();

        sp = getSharedPreferences("RIGHT_MAC_ADDRESS", MODE_PRIVATE);
        Right_DEVICE_ADDR = sp.getString("rightMac", defaultAddress);

    }

    /**
     * Scan for available BLE Devices
     * @param enable  BLE Device Scan Enable
     */
    public void scanLeDevice(final boolean enable) {
        if (enable) {

            mScanning = true;
            ScanFilter scanFilter = new ScanFilter.Builder().setServiceUuid(new ParcelUuid(SERVICE_UUID)).build();
            ArrayList<ScanFilter> filters = new ArrayList<>();

            filters.add(scanFilter);

            ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).setReportDelay(0).build();

            execute(() -> {
                mScanning = true;
                mBluetoothLeScanner.startScan(filters, scanSettings, mLeScanCallback);
                Log.i("scanLeDevice", "Scanning startScan");
            });

            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(() -> {
                mScanning = false;
                mBluetoothLeScanner.stopScan(mLeScanCallback);
                mBluetoothLeScanner.flushPendingScanResults(mLeScanCallback);
                Log.i("scanLeDevice", "Scanning stopped");
                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent(BLE_SCAN_STATUS_BROADCAST_RECEIVER_INTERRUPT).putExtra("status",mScanning));
            }, SCAN_PERIOD);
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent(BLE_SCAN_STATUS_BROADCAST_RECEIVER_INTERRUPT).putExtra("status",mScanning));
        } else {
            mScanning = false;
            mBluetoothLeScanner.stopScan(mLeScanCallback);
            mBluetoothLeScanner.flushPendingScanResults(mLeScanCallback);
            //Cleaning list with gatt connections
            for (Map.Entry<String, BluetoothGatt> entry : connectedDeviceMap.entrySet()) {
                entry.getValue().disconnect();
                entry.getValue().close();
            }
            connectedDeviceMap.clear();

            stringBuilderMeasurementDevice = new StringBuilder();

            isConnectedModule = false;
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent(BLE_SCAN_STATUS_BROADCAST_RECEIVER_INTERRUPT).putExtra("status",mScanning));
            Log.i("scanLeDevice", "Scanning stopScan");
        }
    }


    //CallBack for Bluetooth Device scanner
    private ScanCallback mLeScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result)
        {

            BluetoothManager bluetoothManager = (BluetoothManager) getBaseContext().getSystemService(Context.BLUETOOTH_SERVICE);

            Right_DEVICE_ADDR = sp.getString("rightMac", defaultAddress);

            if (result.getDevice().getAddress().equals(Right_DEVICE_ADDR)) {
                int connectionState = bluetoothManager.getConnectionState(result.getDevice(), BluetoothProfile.GATT);
                if (connectionState == BluetoothProfile.STATE_DISCONNECTED && !isConnectedModule) {
                    isConnectedModule = true;
                    // connect your device
                    result.getDevice().connectGatt(getBaseContext(), true, mGattCallback, BluetoothDevice.TRANSPORT_LE);
                } else if (connectionState == BluetoothProfile.STATE_CONNECTED) {
                    // already connected . send Broadcast if needed
                    Log.i("GATT", "Already connected Device Left ");
                }
            }
            else {
                Log.e("GATT", "ERROR: Non-Smart Insole Left device connection detected!");
            }
        }
    };

    //CallBack for Bluetooth Device connection
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            try {
                BluetoothDevice device = gatt.getDevice();
                String address = device.getAddress();

                switch (newState) {
                    case BluetoothProfile.STATE_CONNECTED:
                        if (!connectedDeviceMap.containsKey(address)) {
                            connectedDeviceMap.put(address, gatt);
                        }
                        Log.i("gattCallback", "STATE_CONNECTED");     //device connected

                        //request priority change
                        boolean rpt = gatt.requestConnectionPriority(CONNECTION_PRIORITY_HIGH);

                        //request to change MTU size (BLE Payload size Default = 20)
                        boolean MTU_status = gatt.requestMtu(GATT_MAX_MTU_SIZE);
                        Log.d("MTU", "MTU Status Accept :" + MTU_status);
                        break;

                    case BluetoothProfile.STATE_DISCONNECTED:
                        Log.e("gattCallback", "STATE_DISCONNECTED");
                        isConnectedModule = false;
                        break;

                    default:
                        Log.e("gattCallback", "STATE_OTHER");
                }
            }catch (Exception e){
                Log.e("Exception", "Exception L267: " + e.getMessage());
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            do {
                super.onMtuChanged(gatt, mtu, status);
            } while(status!=0);

            if (BluetoothGatt.GATT_SUCCESS == status) {
                Log.d("onMtuChanged","onMtuChanged success MTU = " + mtu);
                gatt.discoverServices();
            } else {
                Log.d("onMtuChanged","onMtuChanged fail ");
            }
        }



        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            try {
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    return;
                }

                //Get BLE Service
                BluetoothGattService service = gatt.getService(SERVICE_UUID);
                //Get the characteristic
                BluetoothGattCharacteristic dataCharacteristic = service.getCharacteristic(DATA_CHARACTERISTIC_UUID);
                //Get the characteristic

                //Get characteristic's Descriptor
                BluetoothGattDescriptor descriptor = dataCharacteristic.getDescriptor(DATA_DESCRIPTOR_UUID);
                if (!gatt.setCharacteristicNotification(descriptor.getCharacteristic(), true)) {
                    // Log.e("setCharacteristicNotification", String.format("ERROR: setCharacteristicNotification failed for descriptor: %s", descriptor.getUuid()));
                }
                descriptor.setValue(ENABLE_NOTIFICATION_VALUE);
                boolean result = gatt.writeDescriptor(descriptor);
                if (!result) {
                    Log.e("setCharactNotification", String.format("ERROR: writeDescriptor failed for descriptor: %s", descriptor.getUuid()));

                } else {
                    Log.d("setCharactNotification", String.format("OK: writeDescriptor for descriptor: %s", descriptor.getUuid()));
                }
            }catch (Exception ex){
                Log.d("Exception", "Exception L305: " + ex.getMessage());
            }
        }

        public void pause(int ms) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                System.err.format("IOException: %s%n", e);
            }
        }



        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            int flag = 0;
            while (flag==0) {
                Log.d("onDescriptorWrite", "------------- onDescriptorWrite status: " + status);
                BluetoothGattCharacteristic setupCharacteristic = gatt.getService(SERVICE_UUID).getCharacteristic(SETUP_CHARACTERISTIC_UUID);
                //Calculate Timestamp
                long tsLong = System.currentTimeMillis() / 1000;
                String ts = Long.toString(tsLong);
                //Send timestamp into setup characteristic
                pause(100);
                setupCharacteristic.setValue(ts);
                if (gatt.writeCharacteristic(setupCharacteristic)) {
                    Log.d("writeCharacteristic", "Sending timestamp to device successfully!");
                    flag = 1;
                }
            }
        }


        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

            String fragmentData = new String(characteristic.getValue());
            String deviceMac = gatt.getDevice().getAddress();
            if (deviceMac.equals(Right_DEVICE_ADDR)) {
                PacketUpdate(stringBuilderMeasurementDevice, fragmentData, deviceMac);
            }
            else {
                Log.e("onCharacteristicChanged", "ERROR Non Smart Insole Device read!!");
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //we are still connected to the service
            if (status == BluetoothGatt.GATT_SUCCESS) {

                String measurement;

                try {
                    measurement = new JSONObject(new String(characteristic.getValue())).toString();

                    Log.d("DATA----> ", "-");
                    if (measurement.equals("No valid timestamp received")) {
                        Log.e("characteristic READ", "No valid timestamp received");
                    } else {
                        Log.i("characteristic READ", "DATA exists!");
                        GetMeasurementObj(gatt.getDevice().getAddress(), measurement);
                        Log.i("characteristic READ", "Ta grafei kai ego");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Measurement JSON", "Measurement----> Null");
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e("Measurement Parse", "Measurement: " + e.getMessage());
                }catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Measurement Exception", "Measurement: " + e.getMessage());
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d("onCharacteristicWrite", "------------- onCharacteristicWrite status: " + status);
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.e("onCharacteristicWrite", "Failed write, retrying");
            }
        }
    };

    /**
     * Building Json Packages from incoming Bluetooth Low Energy Characteristic Data fragment
     * @param stringBuilder Json String Builder
     * @param fragmentData Bluetooth Low Energy Characteristic Data fragment
     * @param deviceMac Device Mac Address
     */
    public void PacketUpdate(StringBuilder stringBuilder, String fragmentData, String deviceMac) {
//        try{
//            if (fragmentData.contains("No valid timestamp received"))
//            {
//                stringBuilder.delete(0, stringBuilder.length());
//            }
//            //append data
//            stringBuilder.append(fragmentData);
//
//            //if is the middle fragment of data
//            if (fragmentData.contains("}{")) {
//                while (stringBuilder.toString().contains("}{"))
//                {
//                    if(stringBuilder.toString().contains("}")) {
//                        int templength = stringBuilder.indexOf("}{");
//                        String tempJsonObj = stringBuilder.substring(0, templength + 1);
//                        DataProcessing(deviceMac, tempJsonObj);
//                        stringBuilder.delete(0, templength + 1);
//                    }else { break;}
//                }
//            }
//            //if is the last fragment of data
//            else if (fragmentData.contains("}")) {
//                DataProcessing(deviceMac, stringBuilder.toString());
//                stringBuilder.delete(0, stringBuilder.length());
//            }
//        }catch (Exception e){
//            Log.e("StringBuilder: ", e.getMessage());
//        }
//


    }

    /**
     * Buffering and Batching data packages for ThreadPool Manager Queue
     * @param deviceMAC Device Mac Address
     * @param measurement
     */
    public void DataProcessing(String deviceMAC, String measurement) {

        try {
            if (measurement.equals("No valid timestamp received")) {
                Log.e("characteristic READ", "No valid timestamp received");

            } else {
                Sole soleMeasurement = GetMeasurementObj(deviceMAC, measurement);
                incomecount++;

                measurementList.add(soleMeasurement);
                //Batching Data packages
                if(incomecount >=250){
                    CustomCallable callable = new CustomCallable(measurementList, this);
                    callable.setThreadPoolManager(mThreadPoolManager);
                    mThreadPoolManager.addCallable(callable);
                    incomecount=0;
                    measurementList = new ArrayList<Sole>();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Measurement JSON", "Measurement----> Null");
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("Measurement Parse", "Measurement: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Measurement Exception", "Measurement: " + e.getMessage());
        }
    }

    /**
     *  Parse Json to Sole Measurements
     * @param deviceMAC Device Mac Address
     * @param measurement Json
     * @return Sole Measurement
     * @throws JSONException
     * @throws ParseException
     */
    private Sole GetMeasurementObj(String deviceMAC, String measurement) throws JSONException, ParseException {

        JSONObject obj;
        double accelX , accelY , accelZ,
                gyropoll  , gyropitch  , gyroyaw,
                magX   , magY   , magZ;

        JSONArray array;
        // Create an int array for the final array of numbers.
        double[] pressures = new double[16];

        obj = new JSONObject(measurement);

        String date = obj.getString("T");

        accelX = obj.getDouble("1");
        accelY = obj.getDouble("2");
        accelZ = obj.getDouble("3");

        gyropoll = obj.getDouble("4");
        gyropitch = obj.getDouble("5");
        gyroyaw = obj.getDouble("6");

        magX = obj.getDouble("7");
        magY = obj.getDouble("8");
        magZ = obj.getDouble("9");

        array = obj.getJSONArray("P");

        // Extract numbers from JSON array.
        for (int i = 0; i < array.length(); ++i) {
            pressures[i] = array.optInt(i);
        }

        return new Sole(0, deviceMAC, userId, date, "RIGHT", "0",
                String.valueOf(accelX), String.valueOf(accelY), String.valueOf(accelZ),
                String.valueOf(gyropoll), String.valueOf(gyropitch), String.valueOf(gyroyaw),
                String.valueOf(magX), String.valueOf(magY), String.valueOf(magZ),
                String.valueOf(pressures[0]), String.valueOf(pressures[1]), String.valueOf(pressures[2]), String.valueOf(pressures[3]), String.valueOf(pressures[4]),
                String.valueOf(pressures[5]), String.valueOf(pressures[6]), String.valueOf(pressures[7]), String.valueOf(pressures[8]), String.valueOf(pressures[9]),
                String.valueOf(pressures[10]), String.valueOf(pressures[11]), String.valueOf(pressures[12]), String.valueOf(pressures[13]), String.valueOf(pressures[14]),
                String.valueOf(pressures[15]),"GTF"
        );
    }

    /**
     *  Convert Hex to Integer
     * @param i Hex number
     * @return an Integer from Hex
     */
    public UUID convertFromInteger(int i) {
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new UUID(MSB | (value << 32), LSB);
    }

}

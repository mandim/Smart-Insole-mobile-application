package info.smartinsole.sqlite.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import info.smartinsole.sqlite.R;

public class ScanBleActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView devicesList;
    private ArrayAdapter<MyBleDeviceListAdapter> listAdapter;
    private ArrayList<String> allBleDevicesNames;
    private Button scanBtn;

    private static final long SCAN_PERIOD = 10000;

    //Device Bluetooth Adapter
    private BluetoothAdapter BluetoothAdapter;

    //Device Bluetooth Scanner
    private BluetoothLeScanner BluetoothLeScanner;

    private boolean scanning;
    Handler handler;

    //MAC ADDRESS
    SharedPreferences leftSharedPreferences;
    SharedPreferences rightSharedPreferences;

    MyBleDeviceListAdapter myBleDeviceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_ble);

        // ask to turn on BT
        final BluetoothManager bluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter = bluetoothManager.getAdapter();
        EnableBluetooth();

        BluetoothLeScanner = bluetoothManager.getAdapter().getBluetoothLeScanner();
        handler = new Handler();

        devicesList = findViewById(R.id.devicesList);
        scanBtn = findViewById(R.id.scanBtn);

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        devicesList.setAdapter(listAdapter);
        devicesList.setOnItemClickListener(this);

        leftSharedPreferences = getSharedPreferences("LEFT_MAC_ADDRESS", MODE_PRIVATE);
        rightSharedPreferences = getSharedPreferences("RIGHT_MAC_ADDRESS", MODE_PRIVATE);

        allBleDevicesNames = new ArrayList<>();

        scanBtn.setEnabled(true);
    }

    //region Methods

    private void EnableBluetooth(){
        // Ensures Bluetooth is available on the device and it is enabled.
        if (BluetoothAdapter == null || !BluetoothAdapter.isEnabled()) {
            Intent enableBtIntent =
                    new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
    }

    private void scanLeDevice(boolean enable) {
        if(enable){
            try {
                if (!scanning) {
                    // Stops scanning after a predefined scan period.
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scanning = false;
                            BluetoothLeScanner.stopScan(leScanCallback);
                            scanBtn.setEnabled(true);
                        }
                    }, SCAN_PERIOD);

                    scanning = true;
                    BluetoothLeScanner.startScan(leScanCallback);
                    scanBtn.setEnabled(false);
                } else {
                    scanning = false;
                    BluetoothLeScanner.stopScan(leScanCallback);
                    scanBtn.setEnabled(true);
                }

            }catch (Exception exception){
                Toast.makeText(getApplicationContext(), "Unable to scan...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //CallBack for Bluetooth Device scanner
    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);

                    myBleDeviceListAdapter = new MyBleDeviceListAdapter(result.getDevice());
                    ArrayList<MyBleDeviceListAdapter> allBleDevices = new ArrayList<>();

                    if(myBleDeviceListAdapter.deviceName != null && myBleDeviceListAdapter.deviceName.startsWith("SmartInSole")){
                        if (!allBleDevicesNames.contains(myBleDeviceListAdapter.deviceName)){
                            allBleDevicesNames.add(myBleDeviceListAdapter.deviceName);
                            allBleDevices.add(myBleDeviceListAdapter);

                            listAdapter.addAll(allBleDevices);
                            listAdapter.notifyDataSetChanged();
                        }
                    }
                }
            };

    //endregion

    //region Events

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        MyBleDeviceListAdapter device = (MyBleDeviceListAdapter) parent.getItemAtPosition(position);
        SharedPreferences.Editor leftEditor = leftSharedPreferences.edit();
        SharedPreferences.Editor rightEditor = rightSharedPreferences.edit();
        String bleDeviceAddress = device.deviceAddress;

        if(device.deviceName.endsWith("Left")){

            leftEditor.clear();
            leftEditor.apply();

            leftEditor.putString("leftMac",bleDeviceAddress);
            leftEditor.apply();

            Toast.makeText(getApplicationContext(), "Connected with: " + device.deviceName, Toast.LENGTH_LONG).show();

        }else if(device.deviceName.endsWith("Right")){

            rightEditor.clear();
            rightEditor.apply();

            rightEditor.putString("rightMac",bleDeviceAddress);
            rightEditor.apply();

            Toast.makeText(getApplicationContext(), "Connected with: " + device.deviceName, Toast.LENGTH_LONG).show();

        }else{
            Toast.makeText(getApplicationContext(), "ATTENTION - Cannot connect with device: " + device.deviceName, Toast.LENGTH_LONG).show();
        }
    }

    Intent enableBtIntent;

    public void onScanBtnClick(View view){
        // Ensures Bluetooth is available on the device and it is enabled.
        if (BluetoothAdapter == null || !BluetoothAdapter.isEnabled()) {
            enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);

        }else{
            Toast.makeText(getApplicationContext(), "Scanning for available devices...", Toast.LENGTH_SHORT).show();
            listAdapter.clear();
            allBleDevicesNames.clear();
            scanLeDevice(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode >= 0){
            // User rejects BluetoothAdapter.ACTION_REQUEST_ENABLE
            Toast.makeText(getApplicationContext(), "SCAN needs Bluetooth enabled to work!", Toast.LENGTH_LONG).show();
        }
        else{
            // User accepts BluetoothAdapter.ACTION_REQUEST_ENABLE
            Toast.makeText(getApplicationContext(), "Please tap scan to find available devices", Toast.LENGTH_LONG).show();
        }
    }

    //endregion

    private class MyBleDeviceListAdapter extends BaseAdapter {

        private ArrayList<BluetoothDevice> bleDevices;
        private String deviceName;
        private String deviceAddress;
        //private LayoutInflater mInflator;

        public MyBleDeviceListAdapter() {
            super();
        }

        public MyBleDeviceListAdapter(BluetoothDevice device) {
            super();
            bleDevices = new ArrayList<BluetoothDevice>();
            deviceName = device.getName();
            deviceAddress = device.getAddress();
        }

        public void addDevice(BluetoothDevice device) {
            if(!bleDevices.contains(device)) {
                bleDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return bleDevices.get(position);
        }

        public void clear() {
            bleDevices.clear();
        }

        @Override
        public int getCount() {
            return bleDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return bleDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public String toString() {
            return deviceName;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return view;
        }
    }

}
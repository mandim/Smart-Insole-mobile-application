package info.smartinsole.sqlite.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.buffer.adaptablebottomnavigation.view.AdaptableBottomNavigationView;
import org.buffer.adaptablebottomnavigation.view.ViewSwapper;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import info.smartinsole.sqlite.R;
import info.smartinsole.sqlite.database.DatabaseHelper;
import info.smartinsole.sqlite.database.model.Sole;
import info.smartinsole.sqlite.database.model.Test;
import info.smartinsole.sqlite.http_client.GetLogoutRequest;
import info.smartinsole.sqlite.login.SharedPrefManager;
import info.smartinsole.sqlite.login.UserHelper;
import info.smartinsole.sqlite.utils.MyDividerItemDecoration;
import info.smartinsole.sqlite.utils.RecyclerTouchListener;

import static android.bluetooth.BluetoothProfile.GATT;

public class MainActivity extends AppCompatActivity {

    private static final int HEAT_MAP = 0;
    private static final int LINE_CHART = 1;
    private static final int TEST = 2;
    protected Toolbar toolbar;
    protected FloatingActionButton fab;
    protected AdaptableBottomNavigationView bottomNavigationView;
    protected ViewSwapper viewSwapper;
    protected List<Sole> soleList = new ArrayList<>();
    protected List<Test> testList = new ArrayList<>();
    protected DatabaseHelper db;
    protected int selPosition = 0;
    protected int stage;
    protected boolean sync_all;
    protected ArrayList<Boolean> objSync_now = new ArrayList<Boolean>();
    protected BluetoothManager mBluetoothManager;
    private SoleAdapter mAdapter;
    private RecyclerView recyclerView;
    private TextView noSolesView;
    private ViewSwapperAdapter viewSwapperAdapter;
    private int selectedPosition;
    private LineChartHelper lineChartHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver(this);
        networkChangeReceiver.registerNetworkCallback();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler_view);
        noSolesView = findViewById(R.id.empty_soles_view);
        sync_all = false;

        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        // Create BottomNavBar with Swapper && Adapter ---------------------------------------------
        bottomNavigationView = (AdaptableBottomNavigationView)
                findViewById(R.id.view_bottom_navigation);
        viewSwapper = (ViewSwapper) findViewById(R.id.view_swapper);
        viewSwapperAdapter = new ViewSwapperAdapter(getSupportFragmentManager());
        viewSwapper.setAdapter(viewSwapperAdapter);
        bottomNavigationView.setupWithViewSwapper(viewSwapper);

        db = new DatabaseHelper(this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        // Hide fab for first/default fragment
        fab.hide();

        lineChartHelper = new LineChartHelper();

        final Menu menu = (Menu) bottomNavigationView.getMenu();
        // BottomNavBar Listener
        // Hide fab for HEAT_MAP && HISTORY fragments
        // Disable the possibility to check again the already selected fragment
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectedPosition = viewSwapper.getCurrentItem();

                switch (selectedPosition) {
                    case HEAT_MAP:
                        menu.getItem(1).setEnabled(true);
                        menu.getItem(2).setEnabled(true);
                        item.setEnabled(false);
                        fab.hide();
                        return true;
                    case LINE_CHART:
                        menu.getItem(0).setEnabled(true);
                        menu.getItem(2).setEnabled(true);
                        item.setEnabled(false);
                        fab.show();
                        // Initialize Upload button in fragment 2
                        FragmentManager fr1 = getSupportFragmentManager();
                        List<Fragment> fragmentList1 = fr1.getFragments();
                        ScrollingChartFragment lineFrag1 = (ScrollingChartFragment) fragmentList1.get(0);
                        //lineChartHelper.initBtnUploadJSON(testList, lineFrag1, selPosition);
                        //lineChartHelper.initBtnUpload(testList.get(selPosition), lineFrag1);
                        return true;
                    case TEST:
                        menu.getItem(0).setEnabled(true);
                        menu.getItem(1).setEnabled(true);
                        item.setEnabled(false);
                        stage = 0;
                        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white_24dp));
                        fab.show();
                        return true;
                }
                return false;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                // Get Fragment
                selectedPosition = viewSwapper.getCurrentItem();
                FragmentManager fr = getSupportFragmentManager();
                List<Fragment> fragmentList = fr.getFragments();
                switch (selectedPosition) {
                    // fab button is hidden for this fragments
                    case HEAT_MAP:
                        break;
                    case LINE_CHART:
                        // Add random data to charts when fab is pressed
                        ScrollingChartFragment lineFragment = (ScrollingChartFragment) fragmentList.get(0);
                        if (testList.size() > 0) {
//                            try {
//                                lineChartHelper.addAccToTest(testList, lineFragment, selPosition);
//                                lineChartHelper.addGyroToTest(testList, lineFragment, selPosition);
//                                lineChartHelper.addMagnToTest(testList, lineFragment, selPosition);
//                                updateTest(selPosition);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
                            viewSwapperAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(lineFragment.getContext(), "No Test exists.\nPlease make a new test.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case TEST:
                        TestFragment testFragment = (TestFragment) fragmentList.get(0);
                        switch (stage) {
                            case 0:  // Add test
                                stage++;
                                testFragment.setStage(stage, 0);
                                fab.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_ff));
                                fab.setRotation(180f);
                                break;
                            case 1:  // Back
                                stage--;
                                testFragment.setStage(stage, 0);
                                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white_24dp));
                                testFragment.goBack();
                                //testFragment.initHistory();
                                break;
                        }
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + selectedPosition);
                }
            }
        });

        mAdapter = new SoleAdapter(this, soleList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        //toggleEmptySoles();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

//        if (db.getSolesCount() > 0) soleList = db.getAllSoles();
//        if (db.getTestsCount() > 0) {
//            testList = db.getAllTests();
//            for (int i = 0; i < testList.size(); i++)
//                objSync_now.add(false);
//        }


    }

    /**
     * Called when internet connection is established to sync/upload data from test(s)
     */
    @SuppressLint("ResourceType")
    public void sync() {
        if (viewSwapper.getCurrentItem() != 2) {
            bottomNavigationView.findViewById(R.id.action_three).post(new Runnable() {
                @Override
                public void run() {
                    bottomNavigationView.findViewById(R.id.action_three).performClick();
                }
            });
        } else {
            FragmentManager fr = getSupportFragmentManager();
            List<Fragment> fragmentList = fr.getFragments();
            TestFragment testFragment = (TestFragment) fragmentList.get(0);
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.detach(testFragment);
            ft.attach(testFragment);
            ft.commit();
        }
    }

    /**
     * Place fab on top of Bottom Navigation Bar
     * (measurements must be after the splashActivity)
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // The height will be set at this point
        int height = bottomNavigationView.getMeasuredHeight();
        CoordinatorLayout.LayoutParams lay = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lay.gravity = Gravity.BOTTOM | Gravity.END;
        lay.setMargins(0, 0, 16, height + 16);
        fab.setLayoutParams(lay);
    }

    /**
     * Check read permission
     */
    public boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Load", "Permission is granted1");
                return true;
            } else {

                Log.v("Load", "Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Load", "Permission is granted1");
            return true;
        }
    }

    /**
     * Request permission if not granted
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 3:
                Log.d("Load", "External storage1");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("Load", "Permission: " + permissions[0] + "was " + grantResults[0]);
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    /**
     * Inserting new test in testDB
     * and refreshing the list
     */
    protected void createTest() {
        // inserting test in db and getting
        // newly inserted test id
        long id = db.insertTestData();

        // get the newly inserted sole from db
        Test n = db.getTest(id);

        if (n != null) {
            // adding new sole to array list at 0 position
            testList.add(0, n);

            // refreshing the list
            //mAdapter.notifyDataSetChanged();

            //toggleEmptySoles();
        }
    }

    /**
     * Inserting new sole in db
     * and refreshing the list
     */
    protected void createSole(String sole) {
        // inserting sole in db and getting
        // newly inserted sole id
        long id = db.insertSoleData(sole);

        // get the newly inserted sole from db
        Sole n = db.getSole(id);

        if (n != null) {
            // adding new sole to array list at 0 position
            soleList.add(0, n);

            // refreshing the list
            //mAdapter.notifyDataSetChanged();

            //toggleEmptySoles();
        }
    }

    /**
     * Updating sole in db and updating
     * item in the list by its position
     */
    protected void updateSole(String sole, int position) {
        Sole n = soleList.get(position);
        // updating sole text
        n.setSole(sole);

        // updating sole in db
        db.updateSole(n);

        // refreshing the list
        soleList.set(position, n);

        //mAdapter.notifyItemChanged(position);

        //toggleEmptySoles();
    }

    /**
     * Update test in position
     */
    protected void updateTest(int position) {
        Test n = testList.get(position);

        // updating sole in db
        db.updateTest(n);
        // refreshing the list
        testList.set(position, n);
    }

    /**
     * Deleting sole from SQLite and removing the
     * item from the list by its position
     */
    private void deleteSole(int position) {
        // deleting the sole from db
        db.deleteSole(soleList.get(position));

        // removing the sole from the list
        soleList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptySoles();
    }

    /**
     * Toggling list and empty sole view
     */
    private void toggleEmptySoles() {
        if (db.getSolesCount() > 0) {
            noSolesView.setVisibility(View.GONE);
        } else {
            noSolesView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Main menu in the Toolbar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Actions to perform when an item in Toolbar is selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                if ((new UserHelper(this)).hasExpired()) {
                    SharedPrefManager.getInstance(this).logout();
                    finish();
                } else {
                    String url = getString(R.string.logout_url);
                    GetLogoutRequest getLogoutRequest = new GetLogoutRequest(this, url);
                    getLogoutRequest.userLogout();
                }
                break;
            case R.id.action_sync:
                if ((new UserHelper(this)).hasExpired()) {
                    Toast.makeText(this, "Access Time has expired.\nPlease login again.", Toast.LENGTH_SHORT).show();
                    SharedPrefManager.getInstance(this).logout();
                    finish();
                } else {
                    sync_all = true;
                    sync();
                }
            case R.id.action_connect:
                Intent intent = new Intent(MainActivity.this, ScanBleActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("MissingPermission")
    public int getDevices() {
        return mBluetoothManager.getConnectedDevices(GATT).size();
    }

}

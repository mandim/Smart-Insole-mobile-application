package info.smartinsole.sqlite.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import info.smartinsole.sqlite.R;

/**
 * Network Change state in real time
 */
public class NetworkChangeReceiver {

    MainActivity mainActivity;

    public NetworkChangeReceiver(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    // Network Check
    public void registerNetworkCallback() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(){
                   @Override
                   public void onAvailable(Network network) {
                       for (int i=0; i<mainActivity.testList.size(); i++){
                           if (mainActivity.testList.get(i).getSync().equals("NO_SYNC")){ // Unsynced data
                                if (mainActivity.stage != 1 && mainActivity.stage !=2 ){
                                   askSync();
                               }
                           }
                           return;
                       }
                   }
                   @Override
                   public void onLost(Network network) {
                       Toast.makeText(mainActivity, "Network Lost.", Toast.LENGTH_SHORT).show();
                   }
               });
            }
        }catch (Exception e){
            Log.d("Network CAllback", String.valueOf(e));
        }
    }

    /**
     * Ask user if wants to sync data when internet connection is established
     */
    private void askSync(){
        new AlertDialog.Builder(mainActivity)
                .setTitle("Internet connection established.")
                .setMessage("There are unsynced tests.")
                .setPositiveButton("Sync Now", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mainActivity.sync_all = true;
                        mainActivity.sync();
                    }
                })
                .setNegativeButton("Later",  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {// Do nothing
                    }
                })
                .setIcon(R.drawable.logo)
                .show();
    }
}

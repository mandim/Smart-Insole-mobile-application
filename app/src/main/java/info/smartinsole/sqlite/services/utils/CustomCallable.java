package info.smartinsole.sqlite.services.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import info.smartinsole.sqlite.database.DatabaseHelper;
import info.smartinsole.sqlite.database.model.Sole;

public class CustomCallable implements Callable {

    private WeakReference<ThreadPoolManager> mThreadPoolManagerWeakReference;
    private Context context;
    List<Sole> solelist = new ArrayList<Sole>();

    public CustomCallable(List<Sole> mSoleList, Context mcontext) {
        try {
            this.context = mcontext;
            for (Sole sole: mSoleList) {
                this.solelist.add(sole);
            }
        }catch (Exception ex){
            Log.e("Callable: ", "List Transfer Error: " + ex.getMessage());
        }

    }

    @Override
    public Object call() throws Exception {
        try {
            // check if thread is interrupted before lengthy operation
            if (Thread.interrupted()) throw new InterruptedException();
            DatabaseHelper db = new DatabaseHelper(this.context);
           db.insertMultipleSoleData(solelist);
        } catch (InterruptedException e) {
            e.printStackTrace();
           // Log.e("Callable InterruptedException: ", e.getMessage());
        }catch (Exception exception){
            //Log.e("Callable Exception: ", exception.getMessage());
        }
        return null;
    }

    public void setThreadPoolManager(ThreadPoolManager threadPoolManager) {
        this.mThreadPoolManagerWeakReference = new WeakReference<ThreadPoolManager>(threadPoolManager);
    }
}
package info.smartinsole.sqlite.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import info.smartinsole.sqlite.R;

/**
 * This class is responsible for the recycleView shown in HistoryFragment
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> objTimeStamp, objTestType, objDuration, objSync;
    private ArrayList<Boolean> objSync_now;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        protected ProgressBar loading;
        public TextView timeStampVal;
        public TextView testTypeVal;
        public TextView durationVal;

        public MyViewHolder(View view) {
            super(view);

            loading = (ProgressBar) view.findViewById(R.id.loading);
            loading.setVisibility(View.GONE);
            imageView = view.findViewById(R.id.results);
            timeStampVal = view.findViewById(R.id.timeStampVal);
            testTypeVal = view.findViewById(R.id.testTypeVal);
            durationVal = view.findViewById(R.id.durationVal);
        }
    }

    public HistoryAdapter(Context context, ArrayList<String> timeStamp, ArrayList<String> testType,
                          ArrayList<String> duration, ArrayList<String> sync, ArrayList<Boolean> objSync_now){
        this.context = context;

        this.objSync = sync;
        this.objTimeStamp = timeStamp;
        this.objTestType = testType;
        this.objDuration = duration;
        this.objSync_now = objSync_now;
    }

    @Override
    public HistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test_history_row, parent, false);

        return new HistoryAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(HistoryAdapter.MyViewHolder holder, int position) {
        String sync = objSync.get(position);
        String timeStamp = objTimeStamp.get(position);
        String testType = objTestType.get(position);
        String duration = objDuration.get(position);
        boolean sync_now = objSync_now.get(position);

        if (sync_now){
            holder.loading.setVisibility(View.VISIBLE);
        } else {
            holder.loading.setVisibility(View.GONE);
        }

        if (sync.equals("PROC")) {
            holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.tick));
        } else if (sync.equals("SYNC")){
            holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.upload_done));
        } else {
            holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.pending));
        }
        holder.timeStampVal.setText(timeStamp);
        holder.testTypeVal.setText(testType);
        holder.durationVal.setText(duration);
    }

    @Override
    public int getItemCount() {
        return objTimeStamp.size();
    }

}

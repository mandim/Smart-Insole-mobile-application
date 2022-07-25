package info.smartinsole.sqlite.view;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import info.smartinsole.sqlite.R;
import info.smartinsole.sqlite.database.model.Sole;

public class SoleAdapter extends RecyclerView.Adapter<SoleAdapter.MyViewHolder> {

    private Context context;
    private List<Sole> soleList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView soles;
        public TextView dot;

        public TextView userId;
        public TextView sole;
        public TextView timestam;
        public TextView syncstatus;

        public MyViewHolder(View view) {
            super(view);
            soles = view.findViewById(R.id.sole);


            userId = view.findViewById(R.id.userIdTextview);
            sole = view.findViewById(R.id.soleTextview);
            timestam = view.findViewById(R.id.timestampTextview);
            syncstatus = view.findViewById(R.id.syncStatusTextview);

        }
    }


    public SoleAdapter(Context context, List<Sole> solesList) {
        this.context = context;
        this.soleList = solesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sole_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Sole sole = soleList.get(position);

        holder.userId.setText(sole.getUserid());
        holder.sole.setText(sole.getSole());
        holder.timestam.setText(formatDate(sole.getTimestamp()));
        holder.syncstatus.setText(sole.getSync());
    }

    @Override
    public int getItemCount() {
        return soleList.size();
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }
}

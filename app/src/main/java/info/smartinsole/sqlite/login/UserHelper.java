package info.smartinsole.sqlite.login;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UserHelper {

    private Context context;

    public UserHelper(Context context){
        this.context = context;
    }

    /**
     * Check if token has expired
     * @return true if is expired / false if is still valid
     */
    public boolean hasExpired(){
        Date tokenTime = null;
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            String tk = SharedPrefManager.getInstance(context).getUser().getExpiresAt();
            if (tk == null) return true;
            tokenTime = curFormater.parse(tk);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (currentTime.compareTo(tokenTime) < 0) return false;
        return true;
    }
}

package autotest.gionee.automonkeypowertest.util.Helper;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class AlarmHelper {

    public static void set(Context mContext, String action, long time) {
        Intent intent = new Intent(action);
        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pending = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pending);
        }else{
            am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pending);
        }
    }

    public static void cancel(Context mContext, String action) {
        Intent intent = new Intent(action);
        AlarmManager mAlarm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        PendingIntent mPending = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarm.cancel(mPending);
    }

}

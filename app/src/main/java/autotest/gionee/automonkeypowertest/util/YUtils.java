package autotest.gionee.automonkeypowertest.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryStats;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.internal.os.BatterySipper;
import com.android.internal.os.BatteryStatsHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import autotest.gionee.automonkeypowertest.bean.PowerInfoObj;

/**
 * Created by xhk on 2018/1/29.
 */

public class YUtils {

    /**
     * 设置闹钟
     *
     * @param mContext 上下文
     * @param action   广播
     * @param time     定时
     * @param type     参数
     */
    public static void setAlarm(Context mContext, String action, long time, int type) {
        Intent intent = new Intent(action);
        intent.putExtra("type", type);
        AlarmManager  am      = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pending = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pending);
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pending);
        }
    }

    /**
     * 取消一个闹钟
     *
     * @param mContext
     * @param action
     */
    public static void cancelAlarm(Context mContext, String action) {
        Intent intent = new Intent(action);
        AlarmManager mAlarm = (AlarmManager) mContext
                .getSystemService(Context.ALARM_SERVICE);
        PendingIntent mPending = PendingIntent.getBroadcast(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarm.cancel(mPending);
    }

    /**
     * 获取时间
     *
     * @return
     */
    public static long getTimeData() {
        long             time   = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date             data   = new Date(time);
        return Long.parseLong(format.format(data));
    }

    /**
     * 比较文件,把高功耗的引用 存入本地文件
     *
     * @param endData
     */
    public static void setPowerData(ArrayList<PowerInfoObj> endData) {
        String string = Preference.getString(Constants.START_POWER, "");
        Type listType = new TypeToken<ArrayList<PowerInfoObj>>() {
        }.getType();
        ArrayList<PowerInfoObj> startData        = new Gson().fromJson(string.toString(), listType);
        ArrayList<PowerInfoObj> powerInfoObjList = new ArrayList<>();

        for (int i = 0; i < endData.size(); i++) {
            for (int j = 0; j < startData.size(); j++) {
                if (endData.get(i).getPakName().equals(startData.get(j).getPakName())) {
                    if (endData.get(i).getEndPower() - startData.get(j).getStartPower() > 7) {
                        powerInfoObjList.add(new PowerInfoObj(startData.get(j).getStartTime(), startData.get(j).getStartPower(), endData.get(i).getEndTime(), endData.get(i).getEndPower(), endData.get(i).getPakName()));
                    }
                }
            }
        }
        FileIOUtils.createFile(new File(Constants.HIGH_POWER_NAME));
        if (powerInfoObjList.size() != 0) {
            String json = new Gson().toJson(powerInfoObjList);
            FileIOUtils.writeTxtFile(json, new File(Constants.HIGH_POWER_NAME));
        }
    }

    /**
     * 获取所有功耗
     *
     * @param context 上下文
     * @param flas    1.开始  2.结束
     * @return
     */
    public static ArrayList<PowerInfoObj> getAllPower(Context context, int flas) {
        ArrayList<PowerInfoObj> powerInfoObjList   = new ArrayList<PowerInfoObj>();
        BatteryStatsHelper      batteryStatsHelper = new BatteryStatsHelper(context, true);
        batteryStatsHelper.create((Bundle) null);
        batteryStatsHelper.clearStats();
        batteryStatsHelper.refreshStats(BatteryStats.STATS_SINCE_CHARGED, -1);
        List<BatterySipper> usageList = batteryStatsHelper.getUsageList();
        long                time      = System.currentTimeMillis();
        for (int i = 0; i < usageList.size(); i++) {
            String pkg = usageList.get(i).packageWithHighestDrain;
            if (!TextUtils.isEmpty(pkg) && !pkg.equals("system")) {
                if (flas == 1) {
                    powerInfoObjList.add(new PowerInfoObj(time, usageList.get(i).totalPowerMah, 0, 0, pkg));
                } else {
                    powerInfoObjList.add(new PowerInfoObj(0, 0, time, usageList.get(i).totalPowerMah, pkg));
                }
            }
        }
        return powerInfoObjList;
    }

}

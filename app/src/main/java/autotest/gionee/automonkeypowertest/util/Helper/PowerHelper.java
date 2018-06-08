package autotest.gionee.automonkeypowertest.util.Helper;


import android.content.Context;
import android.os.BatteryStats;
import android.os.Bundle;

import com.android.internal.os.BatterySipper;
import com.android.internal.os.BatteryStatsHelper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo.MyBatterySipper;
import autotest.gionee.automonkeypowertest.util.Constants;
import autotest.gionee.automonkeypowertest.util.Preference;
import autotest.gionee.automonkeypowertest.util.Util;

import static autotest.gionee.automonkeypowertest.util.Constants.DEFAULT_DIGIT_KEEP;

public class PowerHelper {
    private static PowerHelper instance = new PowerHelper();
    private BatteryStatsHelper mStatsHelper;
    private boolean isInit = false;
    private Context mContext;

    private PowerHelper() {
    }

    public static PowerHelper getInstance() {
        if (instance == null) {
            instance = new PowerHelper();
        }
        return instance;
    }

    public void create(Context context, Bundle savedInstanceState) {
        this.mContext = context;
        if (!isInit) {
            mStatsHelper = new BatteryStatsHelper(context, true);
            mStatsHelper.create(savedInstanceState);
            isInit = true;
        }
    }

    public double getTotalPower() {
        if (!isInit) {
            throw new ExceptionInInitializerError("当前没有初始化，应先调用create()方法");
        }
        return mStatsHelper.getTotalPower();
    }

    public void clear() {
        mStatsHelper.clearStats();
    }

    public HashMap<String, MyBatterySipper> getMyBatterySipperMap() {
        if (!isInit) {
            throw new ExceptionInInitializerError("当前没有初始化，应先调用create()方法");
        }
        HashMap<String, MyBatterySipper> pkgMap = new HashMap<>();
        mStatsHelper.clearStats();
        mStatsHelper.refreshStats(BatteryStats.STATS_SINCE_CHARGED, -1);
        List<BatterySipper> usageList = mStatsHelper.getUsageList();
        for (BatterySipper sipper : usageList) {
            if (sipper.drainType != BatterySipper.DrainType.USER && sipper.drainType == BatterySipper.DrainType.APP) {
                MyBatterySipper mySipper = new MyBatterySipper()
                        .setSumPower(sipper.sumPower())
                        .setUsagePowerMah(sipper.usagePowerMah)
                        .setCpuPowerMah(sipper.cpuPowerMah)
                        .setCameraPowerMah(sipper.cameraPowerMah)
                        .setGpsPowerMah(sipper.gpsPowerMah)
                        .setFlashlightPowerMah(sipper.flashlightPowerMah)
                        .setMobileRadioPowerMah(sipper.mobileRadioPowerMah)
                        .setSensorPowerMah(sipper.sensorPowerMah)
                        .setWifiPowerMah(sipper.wifiPowerMah);
                String packName = sipper.packageWithHighestDrain;
                if (null != packName && !"".equals(packName)) {
                    pkgMap.put(packName, mySipper);
                }
            }
        }
        return pkgMap;
    }

    public MyBatterySipper mapToSipper(HashMap<String, MyBatterySipper> hashMap, String pkg) {
        Util.i("map size =" + hashMap.size());
        MyBatterySipper myBatterySipper = hashMap.get(pkg);
        return myBatterySipper == null ? new MyBatterySipper() : myBatterySipper;
    }

    public MyBatterySipper getPkgSipper(String pkg) {
        return mapToSipper(getMyBatterySipperMap(), pkg);
    }

    public static MyBatterySipper reduceSipper(MyBatterySipper before, MyBatterySipper after) {
        if (after == null)
            return new MyBatterySipper();
        if (before == null)
            before = new MyBatterySipper();
        MyBatterySipper myBatterySipper = new MyBatterySipper();
        myBatterySipper.setSumPower(after.sumPower - before.sumPower);
        myBatterySipper.setUsagePowerMah(after.usagePowerMah - before.usagePowerMah);
        myBatterySipper.setCameraPowerMah(after.cameraPowerMah - before.cameraPowerMah);
        myBatterySipper.setCpuPowerMah(after.cpuPowerMah - before.cpuPowerMah);
        myBatterySipper.setFlashlightPowerMah(after.flashlightPowerMah - before.flashlightPowerMah);
        myBatterySipper.setGpsPowerMah(after.gpsPowerMah - before.gpsPowerMah);
        myBatterySipper.setMobileRadioPowerMah(after.mobileRadioPowerMah - before.mobileRadioPowerMah);
        myBatterySipper.setSensorPowerMah(after.sensorPowerMah - before.sensorPowerMah);
        myBatterySipper.setWakeLockPowerMah(after.wakeLockPowerMah - before.wakeLockPowerMah);
        myBatterySipper.setWifiPowerMah(after.wifiPowerMah - before.wifiPowerMah);
        return myBatterySipper;
    }

    public static Double setDoubleScale(Double f) {
        int digit_keep = Preference.getInt(Constants.KEY_DIGIT_KEEP, DEFAULT_DIGIT_KEEP);
        return setDoubleScale(f, digit_keep);
    }

    public static Double setDoubleScale(Double f, int keep_digit) {
        BigDecimal b;
        try {
            b = new BigDecimal(f);
        } catch (Exception e) {
            return 0.0;
        }
        return b.setScale(keep_digit, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static MyBatterySipper getNABatterSipper() {
        return new MyBatterySipper().setSumPower(-3.1415926535897932384626);
    }


}

package autotest.gionee.automonkeypowertest.util.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

import autotest.gionee.automonkeypowertest.R;
import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo.MyBatterySipper;
import autotest.gionee.automonkeypowertest.bean.PowerInfoObj;
import autotest.gionee.automonkeypowertest.bean.PowerInfoResult;
import autotest.gionee.automonkeypowertest.bean.ReportBean;
import autotest.gionee.automonkeypowertest.bean.ReportResultBean;
import autotest.gionee.automonkeypowertest.bean.TestParams;
import autotest.gionee.automonkeypowertest.bean.VoltageBean;
import autotest.gionee.automonkeypowertest.util.Helper.AppHelper;
import autotest.gionee.automonkeypowertest.util.Helper.PowerHelper;
import autotest.gionee.automonkeypowertest.util.Helper.PowerMonitor.PowerBean;
import autotest.gionee.automonkeypowertest.util.Util;

import static autotest.gionee.automonkeypowertest.bean.HtmlReportInfo.MyBatterySipper.AppInfo;
import static autotest.gionee.automonkeypowertest.bean.HtmlReportInfo.VersionSipper;

public class DatabaseUtil {

    private Context        mContext;
    private SQLiteDatabase db;

    public DatabaseUtil(Context context) {
        this.mContext = context;
        open();
    }

    private void open() {
        DatabaseHelper mDBHelper = new DatabaseHelper(mContext);
        db = mDBHelper.getReadableDatabase();
    }

    public void close() {
        if (db.isOpen()) {
            db.close();
        }
    }

    private long insert(String table_name, ContentValues values) {
        return db.insert(table_name, null, values);
    }

    public long insert(ContentValues values) {
        return insert(DatabaseHelper.TABLE_NAME_MONKEY_POWER, values);
    }

    private void update(String table, ContentValues values, String pkgName, String softVersion) {
        db.update(table, values, "packagename=? And softVersion=?", new String[]{pkgName, softVersion});
    }

    private void update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        db.update(table, values, whereClause, whereArgs);
    }

    public void update(String pkgName, String softVersion, ContentValues values) {
        update(DatabaseHelper.TABLE_NAME_MONKEY_POWER, values, pkgName, softVersion);
    }


    public Cursor rawQuery(String query, String[] selection) {
        return db.rawQuery(query, selection);
    }


    public boolean isEmpty(String table) {
        Cursor cursor = db.rawQuery("select * from " + table + " where softVersion=?", new String[]{Util.getSoftVersion()});
        return cursor == null || cursor.getCount() == 0;
    }

    public void deleteTable(String table_name) {
        try {
            db.delete(table_name, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void del(String tableName, String whereClause, String[] whereArgs) {
        db.delete(tableName, whereClause, whereArgs);
    }

    public ArrayList<String> getColumnSample(Column column) {
        ArrayList<String> sampleList = new ArrayList<>();
        Cursor            cursor     = db.rawQuery("select " + column.name() + " from " + DatabaseHelper.TABLE_NAME_MONKEY_POWER, null);
        if (cursor == null || cursor.getCount() == 0) {
            return sampleList;
        }
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String string = cursor.getString(cursor.getColumnIndex(column.name()));
            if (!sampleList.contains(string)) {
                sampleList.add(string);
            }
        }
        return sampleList;
    }

    public void clear() {
        db.delete(DatabaseHelper.TABLE_NAME_MONKEY_POWER, null, null);
    }

    public ArrayList<String> getAppNameList() {
        ArrayList<String> moduleList = new ArrayList<>();
        Cursor            cursor     = db.rawQuery("select * from " + DatabaseHelper.TABLE_NAME_MONKEY_POWER, null);
        if (cursor == null) {
            return moduleList;
        }
        cursor.moveToFirst();
        while (cursor.isFirst() || cursor.moveToNext()) {
            moduleList.add(cursor.getString(cursor.getColumnIndex(DatabaseUtil.APP_NAME)));
        }
        return moduleList;
    }
//
//    public void saveVersionParams(ContentValues map, long batch) {
//        Cursor cursor = rawQuery("select * from " + DatabaseHelper.TABLE_NAME_MONKEY_POWER_PARAMS + " where batch=?", new String[]{String.valueOf(batch)});
//        if (cursor.getCount() > 0) {
//            update(DatabaseHelper.TABLE_NAME_MONKEY_POWER_PARAMS, map, "batch=?", new String[]{String.valueOf(batch)});
//        } else {
//            insert(DatabaseHelper.TABLE_NAME_MONKEY_POWER_PARAMS, map);
//        }
//    }

    public VersionSipper getVersionSipper(long batch) {
        VersionSipper versionSipper;
        try {
            versionSipper = getVersionSipper(Util.getSoftVersion(), batch);
        } catch (Exception e) {
            return new VersionSipper();
        }
        return versionSipper;
    }

    private VersionSipper getVersionSipper(String softVersion, long batch) {
        ArrayList<String>                pkgNames     = new ArrayList<>();
        HashMap<String, MyBatterySipper> sipper_Map_F = getFrontSipperMap(softVersion, batch, pkgNames);
        HashMap<String, MyBatterySipper> sipper_Map_B = getBackSipperMap(softVersion, batch, pkgNames);
//        VersionSipper                    params_totalPower_appSize = getParams(softVersion, batch);
        return new VersionSipper()
                .setSipper_Map_Front(sipper_Map_F)
                .setSipper_Map_Back(sipper_Map_B)
                .setSoftVersion(softVersion)
//                .setTotalPower(params_totalPower_appSize.totalPower)
                .setBatch(batch);
//                .setAppSize(params_totalPower_appSize.appSize)
//                .setTestType(params_totalPower_appSize.testType);
    }

    public TestParams getTestParams(long batchId) {
        Cursor cursor = rawQuery("select * from " + DatabaseHelper.TABLE_NAME_MONKEY_POWER_BATCH + " where _id=? ", new String[]{String.valueOf(batchId)});
        if (cursor != null) {
            if (cursor.moveToNext()) {
                long   testTime     = cursor.getLong(cursor.getColumnIndex(TEST_TIME));
                long   appWaitTime  = cursor.getLong(cursor.getColumnIndex(APP_WAIT_TIME));
                long   lastWaitTime = cursor.getLong(cursor.getColumnIndex(LAST_WAIT_TIME));
                long   cycleCount   = cursor.getLong(cursor.getColumnIndex(CYCLE_COUNT));
                int    testType     = cursor.getInt(cursor.getColumnIndex(TEST_TYPE));
                int    appSize      = cursor.getInt(cursor.getColumnIndex(APP_SIZE));
                String softVersion  = cursor.getString(cursor.getColumnIndex(SOFT_VERSION));
                return new TestParams().setTestTime(testTime).setAppWaitTime(appWaitTime).setLastWaitTime(lastWaitTime)
                        .setCycleCount(cycleCount).setAppSize(appSize).setTestType(testType).setSoftVersion(softVersion);
            }
        }
        return new TestParams();
    }

//    private VersionSipper getParams(String softVersion, long batch) {
//        Cursor params     = rawQuery("select * from " + DatabaseHelper.TABLE_NAME_MONKEY_POWER_PARAMS + " where softVersion=? And batch=? ", new String[]{softVersion, String.valueOf(batch)});
//        double totalPower = 0.0;
//        int    appSize    = 0;
//        int    testType   = R.id.monkeyTest;
//        if (params != null && params.getCount() > 0) {
//            params.moveToFirst();
//            try {
//                String string = params.getString(params.getColumnIndex(DatabaseUtil.TOTAL_POWER));
//                if (string != null) {
//                    totalPower = Double.parseDouble(string);
//                }
//            } catch (Exception e) {
//                Util.i(e.toString());
//            }
//            String appSize_str = params.getString(params.getColumnIndex(DatabaseUtil.APP_SIZE));
//            if (appSize_str != null) {
//                appSize = Integer.parseInt(appSize_str);
//            }
//            testType = params.getInt(params.getColumnIndex(DatabaseUtil.TEST_TYPE));
//        }
//        return new VersionSipper().setTotalPower(totalPower).setAppSize(appSize).setTestType(testType);
//    }


    private HashMap<String, MyBatterySipper> getFrontSipperMap(String softVersion, long batch, ArrayList<String> pkgNames) {
        HashMap<String, MyBatterySipper> sipper_Map_F = new HashMap<>();
        Cursor                           cursor       = rawQuery("select * from " + DatabaseHelper.TABLE_NAME_MONKEY_POWER + " where softVersion=? And batchId=? And isFront=?", new String[]{softVersion, String.valueOf(batch), String.valueOf(1)});
        if (cursor == null || cursor.getCount() == 0) {
            return sipper_Map_F;
        }
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String pkgName = cursor.getString(cursor.getColumnIndex(DatabaseUtil.PKGNAME));
            if (!pkgNames.contains(pkgName)) {
                pkgNames.add(pkgName);
            }
            sipper_Map_F.put(pkgName, getMyBatterySipper(cursor));
        }
        return sipper_Map_F;
    }

    @NonNull
    private HashMap<String, MyBatterySipper> getBackSipperMap(String softVersion, long batch, ArrayList<String> pkgNames) {
        HashMap<String, MyBatterySipper> sipper_Map_B = new HashMap<>();
        Cursor                           back         = rawQuery("select * from " + DatabaseHelper.TABLE_NAME_MONKEY_POWER + " where softVersion=? And batchId=? And isFront=?", new String[]{softVersion, String.valueOf(batch), String.valueOf(0)});
        if (back != null && back.getCount() > 0) {
            for (back.moveToFirst(); !back.isAfterLast(); back.moveToNext()) {
                String          pkgName         = back.getString(back.getColumnIndex(DatabaseUtil.PKGNAME));
                MyBatterySipper myBatterySipper = getMyBatterySipper(back);
                sipper_Map_B.put(pkgName, myBatterySipper);
            }
        }
        for (String pkgName : pkgNames) {
            if (!sipper_Map_B.containsKey(pkgName)) {
                sipper_Map_B.put(pkgName, new MyBatterySipper());
            }
        }
        return sipper_Map_B;
    }

    private MyBatterySipper getMyBatterySipper(Cursor cursor) {
        long    batchId             = cursor.getLong(cursor.getColumnIndex(DatabaseUtil.BATCH_ID));
        String  appName             = cursor.getString(cursor.getColumnIndex(DatabaseUtil.APP_NAME));
        String  appVersion          = cursor.getString(cursor.getColumnIndex(DatabaseUtil.APPVERSION));
        String  sumPower            = cursor.getString(cursor.getColumnIndex(DatabaseUtil.SUM_POWER));
        String  pkgName             = cursor.getString(cursor.getColumnIndex(DatabaseUtil.PKGNAME));
        String  softVersion         = cursor.getString(cursor.getColumnIndex(DatabaseUtil.SOFTVERSION));
        AppInfo appInfo             = new AppInfo(appName, pkgName, appVersion, softVersion);
        String  cpuPowerMah         = cursor.getString(cursor.getColumnIndex(DatabaseUtil.CPU_POWER_MAH));
        String  cameraPowerMah      = cursor.getString(cursor.getColumnIndex(DatabaseUtil.CAMERA_POWER_MAH));
        String  gpsPowerMah         = cursor.getString(cursor.getColumnIndex(DatabaseUtil.GPS_POWER_MAH));
        String  flashlightPowerMah  = cursor.getString(cursor.getColumnIndex(DatabaseUtil.FLASHLIGHT_POWER_MAH));
        String  wakeLockPowerMah    = cursor.getString(cursor.getColumnIndex(DatabaseUtil.WAKELOCK_POWER_MAH));
        String  mobileRadioPowerMah = cursor.getString(cursor.getColumnIndex(DatabaseUtil.MOBILE_RADIO_POWER_MAH));
        String  wifiPowerMah        = cursor.getString(cursor.getColumnIndex(DatabaseUtil.WIFI_POWER_MAH));
        String  usagePowerMah       = cursor.getString(cursor.getColumnIndex(DatabaseUtil.USAGE_POWER_MAH));
        String  sensorPowerMah      = cursor.getString(cursor.getColumnIndex(DatabaseUtil.SENSOR_POWER_MAH));
        String  batteryPercent      = cursor.getString(cursor.getColumnIndex(DatabaseUtil.BATTERY_PERCENT));
        batteryPercent = batteryPercent == null ? "0" : batteryPercent;
        String wholePower = cursor.getString(cursor.getColumnIndex(DatabaseUtil.WHOLE_POWER));
        wholePower = wholePower == null ? "0" : wholePower;
        String voltageStart = cursor.getString(cursor.getColumnIndex(DatabaseUtil.VOLTAGE_START));
        voltageStart = voltageStart == null ? "0" : voltageStart;
        String voltageEnd = cursor.getString(cursor.getColumnIndex(DatabaseUtil.VOLTAGE_END));
        voltageEnd = voltageEnd == null ? "0" : voltageEnd;
        String voltageAvg = cursor.getString(cursor.getColumnIndex(DatabaseUtil.VOLTAGE_AVG));
        voltageAvg = voltageAvg == null ? "0" : voltageAvg;
        String coverage = cursor.getString(cursor.getColumnIndex(DatabaseUtil.COVERAGE));
        coverage = coverage == null ? "0.0" : coverage;
        String duration = cursor.getString(cursor.getColumnIndex(DatabaseUtil.DURATION));
//        String batch    = cursor.getString(cursor.getColumnIndex(DatabaseUtil.BATCH));
        String isFront = cursor.getString(cursor.getColumnIndex(DatabaseUtil.IS_FRONT));
        String time    = cursor.getString(cursor.getColumnIndex(DatabaseUtil.TIME));
        return new MyBatterySipper()
                .setBatchID(batchId)
                .setUsagePowerMah(Double.parseDouble(usagePowerMah))
                .setCpuPowerMah(Double.parseDouble(cpuPowerMah))
                .setCameraPowerMah(Double.parseDouble(cameraPowerMah))
                .setSensorPowerMah(Double.parseDouble(sensorPowerMah))
                .setGpsPowerMah(Double.parseDouble(gpsPowerMah))
                .setMobileRadioPowerMah(Double.parseDouble(mobileRadioPowerMah))
                .setFlashlightPowerMah(Double.parseDouble(flashlightPowerMah))
                .setWakeLockPowerMah(Double.parseDouble(wakeLockPowerMah))
                .setWifiPowerMah(Double.parseDouble(wifiPowerMah))
                .setSumPower(Double.parseDouble(sumPower))
                .setBatteryPercent(Integer.parseInt(batteryPercent))
                .setWholePower(Double.parseDouble(wholePower))
                .setVoltageBean(new VoltageBean(Integer.parseInt(voltageStart), Integer.parseInt(voltageEnd), Integer.parseInt(voltageAvg)))
                .setCoverage(Double.parseDouble(coverage))
                .setAppInfo(appInfo)
                .setDuration(Integer.parseInt(duration))
//                .setBatch(Integer.parseInt(batch))
                .setIsFront(Integer.parseInt(isFront))
                .setTime(time);
    }

    public ReportResultBean getReportBean(long batch) {
        ArrayList<ReportBean>            beanList             = new ArrayList<>();
        VersionSipper                    versionSipper        = getVersionSipper(batch);
        HashMap<String, MyBatterySipper> sipper_map_front     = versionSipper.sipper_Map_Front;
        HashMap<String, MyBatterySipper> sipper_map_back      = versionSipper.sipper_Map_Back;
        ArrayList<PowerInfoResult>       highPowerInfoResults = getHighPowerInfoResults(batch);
        TestParams                       testParams           = getTestParams(batch);
        for (String s : sipper_map_front.keySet()) {
            MyBatterySipper sipper_f = sipper_map_front.get(s);
            MyBatterySipper sipper_b = sipper_map_back.get(s);
            AppInfo         appInfo  = sipper_f.appInfo;
            Util.i("report=覆盖率=" + sipper_f.coverage);
            ReportBean reportBean = ReportBean.get().setTestType(testParams.testType)
                    .setAppName(appInfo.appName)
                    .setPkgName(appInfo.packageName)
                    .setAppVersion(appInfo.appVersion)
                    .setSoftVersion(Util.getSoftVersion())
                    .setPower_front(PowerHelper.setDoubleScale(sipper_f.sumPower))
                    .setPower_front_avg(PowerHelper.setDoubleScale(sipper_f.sumPower / ((float) sipper_f.duration / 60)))
                    .setDuration(sipper_f.duration)
                    .setDuration_Back(testParams.testType == R.id.traverseAppTest ? 0 : sipper_b.duration)
                    .setBatteryPercent(sipper_f.batteryPercent)
                    .setBatteryPercent_back(sipper_b.batteryPercent)
                    .setWholePower_front(PowerHelper.setDoubleScale(sipper_f.wholePower / ((float) sipper_f.duration / 60)))
                    .setWholePower_back(PowerHelper.setDoubleScale(sipper_b.wholePower / ((float) sipper_b.duration / 60)))
                    .setTime(sipper_f.time)
                    .setPower_back(PowerHelper.setDoubleScale(sipper_b.sumPower))
                    .setPower_back_avg(PowerHelper.setDoubleScale(sipper_b.sumPower / ((float) sipper_b.duration / 60)))
                    .setVoltageBean_F(sipper_f.voltageBean)
                    .setVoltageBean_B(sipper_b.voltageBean)
                    .setCoverage_front(String.valueOf(sipper_f.coverage))
                    .setCoverage_back(String.valueOf(sipper_b.coverage));
            beanList.add(reportBean);
        }
        return new ReportResultBean().setReportBeans(beanList).setParams(testParams).setHighPowerResults(highPowerInfoResults);
    }

    public ArrayList<PowerInfoResult> getHighPowerInfoResults(long batchId) {
        ArrayList<PowerInfoResult> powerInfoResults = new ArrayList<>();
        ArrayList<PowerInfoObj>    list             = getHighPowerInfoList(batchId);
        if (list == null || list.size() == 0) {
            return powerInfoResults;
        }
        HashMap<String, AppInfo> pkgNameAppInfoMap = new AppHelper(mContext).getPkgNameAppInfoMap();
        for (PowerInfoObj info : list) {
            String          appName         = "";
            try {
                appName = info == null ? "" : pkgNameAppInfoMap.get(info.getPakName()).appName;
            } catch (Exception e) {
                e.printStackTrace();
            }
            PowerInfoResult powerInfoResult = new PowerInfoResult(String.valueOf(info.getEndPower() - info.getStartPower()), Util.long2String(info.getEndTime()), appName, info.getPakName());
            powerInfoResults.add(powerInfoResult);
        }
        return powerInfoResults;
    }

    public ArrayList<PowerInfoObj> getHighPowerInfoList(long batchId) {
        ArrayList<PowerInfoObj> list   = new ArrayList<>();
        Cursor                  cursor = rawQuery("select * from " + DatabaseHelper.HighPowerTable.NAME + " where batchId=?", new String[]{String.valueOf(batchId)});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long         startTime  = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.HighPowerTable.START_TIME));
                double       startPower = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.HighPowerTable.START_POWER));
                long         endTime    = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.HighPowerTable.END_TIME));
                double       endPower   = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.HighPowerTable.END_POWER));
                String       pkgName    = cursor.getString(cursor.getColumnIndex(DatabaseHelper.HighPowerTable.PAK_NAME));
                PowerInfoObj powerInfo  = new PowerInfoObj(startTime, startPower, endTime, endPower, pkgName);
                list.add(powerInfo);
            }
        }
        return list;
    }

    public void insertResultData(PowerBean bean, int isFront) {
        ContentValues contentValues = MyBatterSipperToCV(bean, isFront, 0);
        insert(contentValues);
    }

    public static ContentValues MyBatterSipperToCV(PowerBean bean, int isFront, int duration) {
        AppInfo         info           = bean.appInfo;
        MyBatterySipper myReduceSipper = bean.myBatterySipper;
        ContentValues   map            = new ContentValues();
        if (myReduceSipper == null || info == null) {
            return map;
        }
        map.put(BATCH_ID, bean.batchId);
        map.put(APP_NAME, info.appName);
        map.put(PKGNAME, info.packageName);
        map.put(SOFTVERSION, Util.getSoftVersion());
        map.put(APPVERSION, info.appVersion);
        double sumPower = myReduceSipper.sumPower;
        map.put(SUM_POWER, sumPower < 0.0 ? 0.0 : sumPower);
        map.put(USAGE_POWER_MAH, myReduceSipper.usagePowerMah);
        map.put(CPU_POWER_MAH, myReduceSipper.cpuPowerMah);
        map.put(CAMERA_POWER_MAH, myReduceSipper.cameraPowerMah);
        map.put(FLASHLIGHT_POWER_MAH, myReduceSipper.flashlightPowerMah);
        map.put(GPS_POWER_MAH, myReduceSipper.gpsPowerMah);
        map.put(MOBILE_RADIO_POWER_MAH, myReduceSipper.mobileRadioPowerMah);
        map.put(SENSOR_POWER_MAH, myReduceSipper.sensorPowerMah);
        map.put(WAKELOCK_POWER_MAH, myReduceSipper.wakeLockPowerMah);
        map.put(WIFI_POWER_MAH, myReduceSipper.wifiPowerMah);
        map.put(BATTERY_PERCENT, bean.batteryPercent);
        map.put(WHOLE_POWER, bean.wholePower);
        map.put(VOLTAGE_START, bean.voltageBean.voltageStart);
        map.put(VOLTAGE_END, bean.voltageBean.voltageEnd);
        map.put(VOLTAGE_AVG, bean.voltageBean.voltageAvg);
        map.put(COVERAGE, bean.coverage);
        map.put(DURATION, duration);
//        map.put(BATCH, Preference.getLong(Constants.KEY_MAX_BATCH, Constants.DEFAULT_MAX_BATCH));
        map.put(IS_FRONT, isFront);
        map.put(TIME, Util.getCurrentTime());
        return map;
    }

    public long addBatch(TestParams params) {
        ContentValues cv = new ContentValues();
        cv.put(TEST_TIME, params.testTime);
        cv.put(APP_WAIT_TIME, params.appWaitTime);
        cv.put(LAST_WAIT_TIME, params.lastWaitTime);
        cv.put(CYCLE_COUNT, params.cycleCount);
        cv.put(APP_SIZE, params.appSize);
        cv.put(TEST_TYPE, params.testType);
        cv.put(SOFT_VERSION, params.softVersion);
        return db.insert(DatabaseHelper.TABLE_NAME_MONKEY_POWER_BATCH, null, cv);
    }

    public ArrayList<Long> getBatchs() {
        ArrayList<Long> longs = new ArrayList<>();
        Cursor          query = rawQuery("select * from " + DatabaseHelper.TABLE_NAME_MONKEY_POWER_BATCH, null);
        if (query == null || query.getCount() == 0) {
            return longs;
        }
        while (query.moveToNext()) {
            try {
                long batch = query.getLong(query.getColumnIndex("_id"));
                longs.add(batch);
            } catch (Exception e) {
                Util.i(e.toString());
            }
        }
        return longs;
    }

    public long getLastBatch() {
        Cursor rawQuery  = db.rawQuery("select * from " + DatabaseHelper.TABLE_NAME_MONKEY_POWER_BATCH + " where _id in (select max(_id) from " + DatabaseHelper.TABLE_NAME_MONKEY_POWER_BATCH + ")", null);
        int    lastBatch = -1;
        while (rawQuery.moveToNext()) {
            try {
                lastBatch = rawQuery.getInt(rawQuery.getColumnIndex("_id"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lastBatch;
    }

    public TestParams getLastTestParams() {
        return getParams(getLastBatch());
    }

    public TestParams getParams(long id) {
        Cursor query = rawQuery("select * from " + DatabaseHelper.TABLE_NAME_MONKEY_POWER_BATCH + " where _id=?", new String[]{String.valueOf(id)});
        if (query == null || query.getCount() == 0) {
            return new TestParams();
        }
        while (query.moveToNext()) {
            try {
                long   testTime     = query.getLong(query.getColumnIndex(TEST_TIME));
                long   appWaitTime  = query.getLong(query.getColumnIndex(APP_WAIT_TIME));
                long   lastWaitTime = query.getLong(query.getColumnIndex(LAST_WAIT_TIME));
                long   cycleCount   = query.getLong(query.getColumnIndex(CYCLE_COUNT));
                int    appSize      = query.getInt(query.getColumnIndex(APP_SIZE));
                int    testType     = query.getInt(query.getColumnIndex(TEST_TYPE));
                String softVersion  = query.getString(query.getColumnIndex(SOFT_VERSION));
                return new TestParams().setTestTime(testTime).setAppWaitTime(appWaitTime)
                        .setLastWaitTime(lastWaitTime).setCycleCount(cycleCount).setTestType(testType)
                        .setSoftVersion(softVersion).setAppSize(appSize);
            } catch (Exception e) {
                Util.i(e.toString());
            }
        }
        return new TestParams();
    }

//    public int getMaxBatch() {
//        Cursor query = rawQuery("select * from " + DatabaseHelper.TABLE_NAME_MONKEY_POWER_PARAMS, null);
//        if (query == null || query.getCount() == 0) {
//            return 0;
//        }
//        int maxBatch = 0;
//        while (query.moveToNext()) {
//            try {
//                int batch = query.getInt(query.getColumnIndex("batch"));
//                maxBatch = Math.max(maxBatch, batch);
//            } catch (Exception e) {
//                Util.i(e.toString());
//            }
//        }
//        return maxBatch;
//    }

    public void insertHighPowerList(long batchId, ArrayList<PowerInfoObj> list) {
        if (list == null) return;
        for (PowerInfoObj info : list) {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.HighPowerTable.BATCH_ID, batchId);
            cv.put(DatabaseHelper.HighPowerTable.START_TIME, info.getStartTime());
            cv.put(DatabaseHelper.HighPowerTable.START_POWER, info.getStartPower());
            cv.put(DatabaseHelper.HighPowerTable.END_TIME, info.getEndTime());
            cv.put(DatabaseHelper.HighPowerTable.END_POWER, info.getEndPower());
            cv.put(DatabaseHelper.HighPowerTable.PAK_NAME, info.getPakName());
            insert(DatabaseHelper.HighPowerTable.NAME, cv);
        }
    }


    public static final String BATCH_ID               = "batchId";
    public static final String APP_NAME               = "appName";
    public static final String PKGNAME                = "packagename";
    public static final String SOFTVERSION            = "softVersion";
    public static final String APPVERSION             = "appVersion";
    public static final String SUM_POWER              = "sumPower";
    public static final String USAGE_POWER_MAH        = "usagePowerMah";
    public static final String CPU_POWER_MAH          = "cpuPowerMah";
    public static final String CAMERA_POWER_MAH       = "cameraPowerMah";
    public static final String FLASHLIGHT_POWER_MAH   = "flashlightPowerMah";
    public static final String GPS_POWER_MAH          = "gpsPowerMah";
    public static final String MOBILE_RADIO_POWER_MAH = "mobileRadioPowerMah";
    public static final String SENSOR_POWER_MAH       = "sensorPowerMah";
    public static final String WAKELOCK_POWER_MAH     = "wakeLockPowerMah";
    public static final String WIFI_POWER_MAH         = "wifiPowerMah";
    public static final String DURATION               = "duration";
    public static final String BATTERY_PERCENT        = "batteryPercent";
    public static final String WHOLE_POWER            = "wholePower";
    public static final String VOLTAGE_START          = "voltageStart";
    public static final String VOLTAGE_END            = "voltageEnd";
    public static final String VOLTAGE_AVG            = "voltageAvg";
    public static final String COVERAGE               = "coverage";

    //    public static final String BATCH       = "batch";
    public static final String IS_FRONT    = "isFront";
    public static final String TIME        = "time";
    public static final String TOTAL_POWER = "totalPower";

    public static final String TEST_TIME      = "testTime";
    public static final String APP_WAIT_TIME  = "appWaitTime";
    public static final String LAST_WAIT_TIME = "lastWaitTime";
    public static final String CYCLE_COUNT    = "cycleCount";
    public static final String APP_SIZE       = "appSize";
    public static final String TEST_TYPE      = "testType";
    public static final String SOFT_VERSION   = "softVersion";

}

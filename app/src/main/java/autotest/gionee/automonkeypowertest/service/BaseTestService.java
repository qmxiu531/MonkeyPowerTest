package autotest.gionee.automonkeypowertest.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo.MyBatterySipper.AppInfo;
import autotest.gionee.automonkeypowertest.bean.SettingsInfo;
import autotest.gionee.automonkeypowertest.bean.TestParams;
import autotest.gionee.automonkeypowertest.main.MainActivity;
import autotest.gionee.automonkeypowertest.util.Command;
import autotest.gionee.automonkeypowertest.util.Configurator;
import autotest.gionee.automonkeypowertest.util.Constants;
import autotest.gionee.automonkeypowertest.util.Helper.AlarmHelper;
import autotest.gionee.automonkeypowertest.util.Helper.AppHelper;
import autotest.gionee.automonkeypowertest.util.Helper.BrightnessUtils;
import autotest.gionee.automonkeypowertest.util.Helper.NotificationHelper;
import autotest.gionee.automonkeypowertest.util.Helper.ScreenHelper;
import autotest.gionee.automonkeypowertest.util.Helper.WakeHelper;
import autotest.gionee.automonkeypowertest.util.Util;
import autotest.gionee.automonkeypowertest.util.sqlite.ChargeUtils;
import autotest.gionee.automonkeypowertest.util.sqlite.DBManager;


public abstract class BaseTestService extends Service {
    private static final String  ACTION_STOP_TEST = "automonkeytest.stoptest";
    private static final int     TESTING_FLAG     = 0;
    private static       boolean testFinish       = false;
    private static       boolean isException      = false;
    private static       String  errorMsg         = "";
    private ArrayList<AppInfo> mAppList;
    private NotificationHelper mNotifi;
    private WakeHelper         mWake;
    private MyReceiver         mReceiver;
    private AppHelper          mAppHelper;
    private boolean isStop    = false;
    private int     appIndex  = 0;
    private int     testIndex = 0;
    private AppInfo      appInfo;
    private SettingsInfo settings;
    private long         batchID;
    private TestParams   mParams;

    public WakeHelper getWake() {
        return mWake;
    }

    protected void onInit(ArrayList<AppInfo> mAppList) {
    }

    protected void onBeforeStart(ArrayList<AppInfo> mAppList) {

    }

    protected abstract void onBeforeTest(AppInfo appInfo, long testIndex);

    protected abstract void onTestApp(AppHelper appHelper, AppInfo appInfo, int testIndex, int appIndex);

    protected abstract boolean onTimeUp(AppHelper appHelper, AppInfo appInfo, int testIndex, int appIndex);

    protected void onBeforeStopTest(AppInfo appInfo, int testIndex) {
    }

    protected abstract boolean onStopTest(boolean isException, String errorMsg);

    public TestParams getParams() {
        return mParams;
    }

    @Override
    public void onCreate() {
//        super.onCreate();
        Util.i("BaseTestService:onCreate");
        mAppHelper = new AppHelper(this);
        mAppList = mAppHelper.getSelectedAppInfo();
        Util.i("共有" + mAppList.size() + "个应用需要测试");
        Toast.makeText(this, "共有" + mAppList.size() + "个应用需要测试", Toast.LENGTH_LONG).show();
//        Preference.putLong(Constants.KEY_APP_SIZE, mAppList.size());
        mWake = new WakeHelper(this);
        mWake.getLock().setReferenceCounted(false);
        mWake.getLock().acquire();
        mWake.disableKeyguard();
        mNotifi = new NotificationHelper(this);
        mNotifi.set(TESTING_FLAG);
        regReceiver();
        mParams = Configurator.getParams();
        onInit(mAppList);
        settings = Configurator.getSettings();
        if (settings.screenOff) {
            setScreenOff(true);
        }
        if (settings.disCharge) {
            int node_type_battery_charging_enabled = ChargeUtils.getNodeState(getApplicationContext(), "NODE_TYPE_BATTERY_CHARGING_ENABLED");
            Util.i("充电状态=" + node_type_battery_charging_enabled);
            Util.i("设置充电状态 false");
            ChargeUtils.setChargeEnable(getApplicationContext(), false);
            int node_type_battery_charging_enabled2 = ChargeUtils.getNodeState(getApplicationContext(), "NODE_TYPE_BATTERY_CHARGING_ENABLED");
            Util.i("充电状态=" + node_type_battery_charging_enabled2);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Util.i("BaseTestService:onStartCommand");
        isStop = false;
        appIndex = 0;
        testIndex = 0;
        batchID = DBManager.addBatch(mParams);
        checkTestBatch();
        onBeforeStart(mAppList);
        startTest();
        return super.onStartCommand(intent, flags, startId);
    }


    private void startTest() {
        try {
            appInfo = mAppList.get(appIndex % mAppList.size());
            onBeforeTest(appInfo, testIndex);
            onTestApp(getAppHelper(), appInfo, testIndex, appIndex);
        } catch (Throwable e) {
            Toast.makeText(this, "测试过程中，遇到异常，停止测试", Toast.LENGTH_LONG).show();
            isException = true;
            errorMsg = "测试过程中，遇到异常，停止测试;" + e.toString();
            Util.isCycle = false;
            stopTest();
            StringWriter sw = new StringWriter();
            PrintWriter  pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            Util.i(sw.toString());
        }
    }

    void setAlarm(double time_minute) {
        if (time_minute > 0) {
            Util.i("设置定时闹钟，" + time_minute + "分钟后测试下一个应用");
            AlarmHelper.set(this, Constants.ACTION_SAVE_FRONT_POWER, (long) (time_minute * 60 * 1000));
        }
    }

    void setSecondAlarm(long time_second) {
        if (time_second > 0) {
            Util.i("设置定时闹钟，" + time_second + "秒后测试下一个应用");
            AlarmHelper.set(this, Constants.ACTION_SAVE_FRONT_POWER, time_second * 1000);
        }
    }

    protected void notifyTestFinish() {
        if (Util.isTest) {
            if (onTimeUp(mAppHelper, appInfo, testIndex, appIndex)) {
                testIndex++;
                startTest();
            } else {
                checkTestIndex();
            }
        }
    }

    private void stopTest() {
        try {
            if (mWake != null && !mWake.isScreenOn()) {
                Command.execCommand("input keyevent 26", false);
            }
            Util.isTest = onStopTest(isException, errorMsg);
            String testFinishText = testFinish ? "测试完成" : "手动或者意外停止，测试结束";
            Util.i(testFinishText);
            Toast.makeText(this, testFinishText, Toast.LENGTH_LONG).show();
            isStop = true;
        } catch (Exception e) {
            Util.i(e.toString());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mNotifi.cancelAll();
            mWake.getLock().release();
            mWake = null;
            unregisterReceiver(mReceiver);
        } catch (Exception e) {
            Util.i(e.toString());
        }
        if (settings.disCharge) {
            ChargeUtils.setChargeEnable(getApplicationContext(), true);
        }
        if (settings.screenOff) {
            setScreenOff(false);
        }
        if (!isStop) stopTest();
    }

    private void checkTestIndex() {
        Util.i("检查index是否为最后一个？");
        if (isLastApp()) {
            onBeforeStopTest(appInfo, testIndex);
            testFinish = true;
            Util.i("当前为最后一个，结束进程");
            stopTest();
        } else {
            Util.i("不是，测试index加1");
            appIndex++;
            testIndex = 0;
            startTest();
        }
    }

    private boolean isLastApp() {
        Util.i("测试Index:" + appIndex);
        return null == mAppList || !Util.isTest || appIndex == mAppList.size() - 1;
    }

    public SettingsInfo getSettings() {
        return settings;
    }

    public AppHelper getAppHelper() {
        return mAppHelper;
    }

    public long getBatchID() {
        return batchID;
    }

    private void checkTestBatch() {
//        long mb = DBManager.getMaxBatch();
//        mb++;
//        Preference.putLong(Constants.KEY_MAX_BATCH, mb);
        ArrayList<Long> batchs = DBManager.getBatchs();
        int             mb     = batchs.size();
        Util.i("当前为第" + mb + "次测试");
        Toast.makeText(this, "当前为第" + mb + "次测试", Toast.LENGTH_SHORT).show();
    }

    void showTestFinishDialog(boolean isException, String errorMsg) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Intent testFinish = new Intent(Constants.ACTION_TEST_FINISH);
        testFinish.putExtra("isException", isException);
        testFinish.putExtra("errorMsg", errorMsg);
        sendBroadcast(testFinish);
    }

    boolean isLastIndex() {
        return appIndex == mAppList.size() - 1;
    }

    boolean needAppWait() {
        return mParams.appWaitTime != 0;
    }

    boolean needLastWait() {
        return mParams.lastWaitTime != 0;
    }

    void wakeUp() {
        if (!mWake.isScreenOn()) {
            Command.execCommand("input keyevent 26", false);
            getWake().getLock().setReferenceCounted(false);
            getWake().getLock().acquire();
        }
    }

    void lockScreen() {
        if (mWake.isScreenOn()) {
            Command.execCommand("input keyevent 26", false);
            getWake().getLock().release();
        }
    }

    private void regReceiver() {
        mReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_SAVE_FRONT_POWER);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(ACTION_STOP_TEST);
        registerReceiver(mReceiver, intentFilter);
    }

    public void setScreenOff(boolean screenOff) {
        BrightnessUtils.setAutoBrightnessEnable(getApplicationContext(), !screenOff);
        ScreenHelper.setScreenValue(0);
        ScreenHelper.setScreenEnable(!screenOff);
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case Constants.ACTION_SAVE_FRONT_POWER:
                        notifyTestFinish();
                        break;

                    case Intent.ACTION_BATTERY_CHANGED:
                        if (Util.isTest && intent.getIntExtra("level", 0) <= 5) {
                            stopTest();
                        }
                        break;
                    case ACTION_STOP_TEST:
                        stopTest();
                        break;
                    default:
                        break;
                }
            }
        }
    }

}

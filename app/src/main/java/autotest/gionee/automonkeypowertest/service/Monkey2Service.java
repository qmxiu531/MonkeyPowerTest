package autotest.gionee.automonkeypowertest.service;


import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo.MyBatterySipper.AppInfo;
import autotest.gionee.automonkeypowertest.bean.TestParams;
import autotest.gionee.automonkeypowertest.util.Command;
import autotest.gionee.automonkeypowertest.util.Configurator;
import autotest.gionee.automonkeypowertest.util.Constants;
import autotest.gionee.automonkeypowertest.util.Helper.AppHelper;
import autotest.gionee.automonkeypowertest.util.Helper.MonkeyHelper;
import autotest.gionee.automonkeypowertest.util.Helper.PowerMonitor;
import autotest.gionee.automonkeypowertest.util.Preference;
import autotest.gionee.automonkeypowertest.util.ShellUtil;
import autotest.gionee.automonkeypowertest.util.Util;
import autotest.gionee.automonkeypowertest.util.sqlite.DBManager;
import autotest.gionee.automonkeypowertest.util.sqlite.DatabaseUtil;

public class Monkey2Service extends PowerBaseService {

    private long cycle_count   = 1;
    private int  mCurrentCycle = 0;
    private int  appCount      = 0;
    private long wait_time;
    private long time;

    @Override
    protected void onInit(ArrayList<AppInfo> mAppList) {
        super.onInit(mAppList);
        appCount = mAppList.size();
        TestParams params = getParams();
        cycle_count = params.cycleCount;
        Util.i("cycle_count=" + cycle_count);
        wait_time = params.lastWaitTime;
        time = params.testTime;
    }


    @Override
    protected void onTestApp(AppHelper appHelper, AppInfo appInfo, int testIndex, int appIndex) {
        if (appIndex == appCount - 1 && testIndex == 1) {
            Util.home(this);
            if (getWake().isScreenOn()) {
                Command.execCommand("input keyevent 26", false);
            }
            getWake().getLock().release();
            setAlarm(wait_time);
        } else {
            appHelper.launchApp(appInfo.packageName);//测试前拉起应用，防止monkey有时拉起失败
            Util.i("开始测试monkey");
            MonkeyHelper.getInstance().testPkg(appInfo.packageName, time * 60 * 1000, MonkeyHelper.getInstance().getMonkeyParams());
            setAlarm(time);
        }
    }

    @Override
    protected boolean onTimeUp(AppHelper appHelper, AppInfo appInfo, int testIndex, int appIndex) {
        if (appIndex == appCount - 1 && testIndex == 1) {
            if (Preference.getBoolean(Constants.KEY_IS_STOP_MUSIC, Constants.DEFAULT_IS_STOP_MUSIC)) {
                appHelper.killAppProcess("com.android.music");
                ShellUtil.execCommand("pm clear com.android.deskclock", false);
            }
            if (!getWake().isScreenOn()) {
                Command.execCommand("input keyevent 26", false);
                getWake().getLock().acquire();
            }
            ContentValues cv = DatabaseUtil.MyBatterSipperToCV(getPowerMonitor().stop(), 0, (int) time);
            DBManager.insert(cv);
        } else {
            MonkeyHelper.kill();
            ContentValues cv = DatabaseUtil.MyBatterSipperToCV(getPowerMonitor().stop(), 1, (int) wait_time);
            DBManager.insert(cv);
            if (Configurator.needKillAppAfterMonkey()) {
                appHelper.killAppProcess(appInfo.packageName);
            }
        }
        return wait_time > 0 && appIndex == appCount - 1 && testIndex == 0;
    }

    @Override
    protected boolean onStopTest(boolean isException, String errorMsg) {
        if (MonkeyHelper.getInstance().isMonkeyRunning())
            MonkeyHelper.kill();
        mCurrentCycle++;
        Util.i("cycle=" + mCurrentCycle);
        if (!isException && Util.isCycle && mCurrentCycle < cycle_count) {
            startService(new Intent(this, Monkey2Service.class));
            return true;
        } else {
            sendBroadcast(new Intent("autotest.gionee.automonkeypowertest.stoptest"));
            showTestFinishDialog(isException, errorMsg);
            stopSelf();
        }
        return false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}

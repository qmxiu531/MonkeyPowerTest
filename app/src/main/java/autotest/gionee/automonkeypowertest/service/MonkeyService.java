package autotest.gionee.automonkeypowertest.service;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo.MyBatterySipper.AppInfo;
import autotest.gionee.automonkeypowertest.util.Helper.AppHelper;
import autotest.gionee.automonkeypowertest.util.Helper.MonkeyHelper;
import autotest.gionee.automonkeypowertest.util.Util;


public class MonkeyService extends BackPowerTestService {

    @Override
    void onFrontTest(AppHelper appHelper, AppInfo appInfo, int testIndex, int appIndex) {
        MonkeyHelper mh = MonkeyHelper.getInstance();
        mh.startPermitMonitor(getApplicationContext());
        appHelper.launchApp(appInfo.packageName);//测试前拉起应用，防止monkey有时拉起失败
        Util.i("开始测试monkey");
        mh.testPkg(appInfo.packageName, getTestTime() * 60 * 1000, mh.getMonkeyParams());
        mh.stopPermitMonitor(getApplicationContext());
        setAlarm(getTestTime());
    }

    @Override
    protected void onFrontTestTimeUp(AppHelper appHelper, AppInfo appInfo, int testIndex, int appIndex) {
        MonkeyHelper.kill();
    }

    @Override
    protected void onBeforeCycleStop() {
        if (MonkeyHelper.getInstance().isMonkeyRunning()) {
            MonkeyHelper.kill();
        }
    }

    @Override
    protected void onBeforeFinishStop() {
        sendBroadcast(new Intent("autotest.gionee.automonkeypowertest.stoptest"));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

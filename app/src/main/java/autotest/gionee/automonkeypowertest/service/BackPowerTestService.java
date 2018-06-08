package autotest.gionee.automonkeypowertest.service;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo;
import autotest.gionee.automonkeypowertest.bean.TestParams;
import autotest.gionee.automonkeypowertest.util.Configurator;
import autotest.gionee.automonkeypowertest.util.Helper.AppHelper;
import autotest.gionee.automonkeypowertest.util.Helper.MonkeyHelper;
import autotest.gionee.automonkeypowertest.util.ShellUtil;
import autotest.gionee.automonkeypowertest.util.Util;

/**
 * gionee
 * 2018/3/21
 */

public abstract class BackPowerTestService extends PowerBaseService {
    private long currentCycle = 0;
    private long testTime;
    private long waitTime;
    private long cycleCount   = 0;
    private long lastWaitTime = 0;

    @Override
    protected void onInit(ArrayList<HtmlReportInfo.MyBatterySipper.AppInfo> mAppList) {
        super.onInit(mAppList);
        TestParams params = getParams();
        testTime = params.testTime;
        waitTime = params.appWaitTime;
        cycleCount = params.cycleCount;
        lastWaitTime = params.lastWaitTime;
        Util.i("cycle_count=" + cycleCount);
    }

    @Override
    protected void onTestApp(AppHelper appHelper, HtmlReportInfo.MyBatterySipper.AppInfo appInfo, int testIndex, int appIndex) {
        switch (testIndex) {
            case 0:
                onFrontTest(appHelper, appInfo, testIndex, appIndex);
                break;
            case 1:
                if (getSettings().isStopMusic) {
                    appHelper.killAppProcess("com.android.music");
                    ShellUtil.execCommand("pm clear com.android.deskclock", false);
                }
                Util.home(this);
                lockScreen();
                setAlarm(isLastIndex() ? (waitTime + lastWaitTime) : waitTime);
                break;
            default:
                break;
        }
    }

    @Override
    protected boolean onTimeUp(AppHelper appHelper, HtmlReportInfo.MyBatterySipper.AppInfo appInfo, int testIndex, int appIndex) {
        super.onTimeUp(appHelper, appInfo, testIndex, appIndex);
        if (testIndex == 0) {
            onFrontTestTimeUp(appHelper, appInfo, testIndex, appIndex);
            if (Configurator.needKillAppAfterMonkey()) {
                appHelper.killAppProcess(appInfo.packageName);
            }
        } else if (testIndex == 1) {
            wakeUp();
        }
        return testIndex == 0 && (needAppWait() || (isLastIndex() && needLastWait()));
    }


    @Override
    protected boolean onStopTest(boolean isException, String errorMsg) {
        onBeforeCycleStop();
        currentCycle++;
        Util.i("currentCycle=" + currentCycle);
        Util.i("cycleCount=" + cycleCount);
        if (!isException && Util.isCycle && currentCycle < cycleCount) {
            startService(new Intent(this, MonkeyService.class));
            return true;
        } else {
            onBeforeFinishStop();
            showTestFinishDialog(isException, errorMsg);
            stopSelf();
            return false;
        }
    }

    public long getTestTime() {
        return testTime;
    }

    abstract void onFrontTest(AppHelper appHelper, HtmlReportInfo.MyBatterySipper.AppInfo appInfo, int testIndex, int appIndex);

    protected abstract void onFrontTestTimeUp(AppHelper appHelper, HtmlReportInfo.MyBatterySipper.AppInfo appInfo, int testIndex, int appIndex);

    protected void onBeforeCycleStop() {
    }

    protected void onBeforeFinishStop() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

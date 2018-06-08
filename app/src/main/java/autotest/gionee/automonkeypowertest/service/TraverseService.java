package autotest.gionee.automonkeypowertest.service;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo.MyBatterySipper.AppInfo;
import autotest.gionee.automonkeypowertest.util.Constants;
import autotest.gionee.automonkeypowertest.util.Helper.AppHelper;
import autotest.gionee.automonkeypowertest.util.Helper.TraverseUtil;
import autotest.gionee.automonkeypowertest.util.Preference;
import autotest.gionee.automonkeypowertest.util.Util;


public class TraverseService extends PowerBaseService {

    private TraverseUtil traverseUtil;
//    private long         start_time;
    private long         cycle_count;
    private long current_cycle = 0;

    @Override
    protected void onInit(ArrayList<AppInfo> mAppList) {
        super.onInit(mAppList);
        traverseUtil = new TraverseUtil(getApplicationContext());
        cycle_count = getParams().cycleCount;
    }

    @Override
    protected void onTestApp(AppHelper appHelper, AppInfo appInfo, int testIndex, int appIndex) {
//        start_time = System.currentTimeMillis();
        appHelper.launchApp(appInfo.packageName);
        traverseUtil.testApp(appInfo.appName);
        traverseUtil.setTestAppListener(super::notifyTestFinish);
    }

    @Override
    protected boolean onTimeUp(AppHelper appHelper,AppInfo appInfo, int testIndex, int appIndex) {
        super.onTimeUp(appHelper,appInfo, testIndex, appIndex);
//        long tTime = System.currentTimeMillis() - start_time;
//        Preference.putLong(Constants.KEY_TEST_TIME, tTime / 1000 / 60);
        return false;
    }

    @Override
    protected boolean onStopTest(boolean isException, String errorMsg) {
        current_cycle++;
        Util.i("cycle=" + current_cycle);
        if (!isException && Util.isCycle && current_cycle < cycle_count) {
            startService(new Intent(this, TraverseService.class));
            return true;
        } else {
            traverseUtil.stop();
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
}

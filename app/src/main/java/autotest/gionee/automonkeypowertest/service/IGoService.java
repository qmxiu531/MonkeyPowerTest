package autotest.gionee.automonkeypowertest.service;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo;
import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo.MyBatterySipper.AppInfo;
import autotest.gionee.automonkeypowertest.util.Configurator;
import autotest.gionee.automonkeypowertest.util.Helper.AppHelper;
import autotest.gionee.automonkeypowertest.util.Helper.IGoHelper;
import autotest.gionee.automonkeypowertest.util.Helper.PowerMonitor;


public class IGoService extends BackPowerTestService {
    private IGoHelper iGoHelper;

    @Override
    protected void onInit(ArrayList<AppInfo> mAppList) {
        super.onInit(mAppList);
        iGoHelper = new IGoHelper(getApplicationContext());
    }

    @Override
    void onFrontTest(AppHelper appHelper, HtmlReportInfo.MyBatterySipper.AppInfo appInfo, int testIndex, int appIndex) {
        iGoHelper.testApp(getTestTime(), appInfo.packageName);
        setSecondAlarm(getTestTime() * 60 + 40);
    }

    @Override
    protected void onFrontTestTimeUp(AppHelper appHelper, AppInfo appInfo, int testIndex, int appIndex) {

    }

    @Override
    protected PowerMonitor.PowerBean changeResultData(PowerMonitor.PowerBean bean, int testIndex) {
        if (testIndex == 0) {
            double lastCoverage = iGoHelper.getLastCoverage();
            return bean.setCoverage(lastCoverage);
        } else {
            return super.changeResultData(bean, testIndex);
        }
    }

    @Override
    protected void onBeforeFinishStop() {
        iGoHelper.stop();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

package autotest.gionee.automonkeypowertest.service;

import android.content.ContentValues;

import java.util.ArrayList;

import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo.MyBatterySipper.AppInfo;
import autotest.gionee.automonkeypowertest.bean.PowerInfoObj;
import autotest.gionee.automonkeypowertest.bean.TestParams;
import autotest.gionee.automonkeypowertest.util.Helper.AppHelper;
import autotest.gionee.automonkeypowertest.util.Helper.PowerMonitor;
import autotest.gionee.automonkeypowertest.util.Util;
import autotest.gionee.automonkeypowertest.util.sqlite.DBManager;
import autotest.gionee.automonkeypowertest.util.sqlite.DatabaseUtil;

public abstract class PowerBaseService extends BaseTestService {
    private PowerMonitor powerMonitor;
    private static final int BACK  = 0;
    private static final int FRONT = 1;


    @Override
    protected void onInit(ArrayList<AppInfo> mAppList) {
        super.onInit(mAppList);
        powerMonitor = new PowerMonitor(this);
    }

    PowerMonitor getPowerMonitor() {
        return powerMonitor;
    }

    @Override
    protected void onBeforeStart(ArrayList<AppInfo> mAppList) {
        super.onBeforeStart(mAppList);
    }

    @Override
    protected void onBeforeTest(AppInfo appInfo, long testIndex) {
        powerMonitor.start(appInfo, getBatchID());
    }

    @Override
    protected boolean onTimeUp(AppHelper appHelper, AppInfo appInfo, int testIndex, int appIndex) {
        TestParams             params    = getParams();
        PowerMonitor.PowerBean powerBean = powerMonitor.stop();
        ContentValues          cv        = DatabaseUtil.MyBatterSipperToCV(changeResultData(powerBean, testIndex), testIndex == 0 ? FRONT : BACK, (int) (testIndex == 0 ? params.testTime : (isLastIndex() ? params.appWaitTime : (params.appWaitTime + params.lastWaitTime))));
        DBManager.insert(cv);
        return false;
    }

    protected PowerMonitor.PowerBean changeResultData(PowerMonitor.PowerBean bean, int testIndex) {
        return bean;
    }

    @Override
    public void onDestroy() {
        DBManager.insertHighPowerList(getBatchID(), Util.getHighPowerList());
        super.onDestroy();
    }
}

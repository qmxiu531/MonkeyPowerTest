package autotest.gionee.automonkeypowertest.util.Helper;


import android.content.Context;

import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo.MyBatterySipper;
import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo.MyBatterySipper.AppInfo;
import autotest.gionee.automonkeypowertest.bean.VoltageBean;
import autotest.gionee.automonkeypowertest.util.Util;

public class PowerMonitor {
    private Context          mContext;
    private int              batteryPercent_before;
    private MyBatterySipper  sipper_Before;
    private WholePowerHelper mWholePowerHelper;
    private boolean isStart = false;
    private       AppInfo        mAppInfo_Current;
    private final BatteryVoltage mBattery;
    private long batchID;

    public PowerMonitor(Context mContext) {
        this.mContext = mContext;
        mWholePowerHelper = new WholePowerHelper(mContext);
        mBattery = new BatteryVoltage();
    }

    public void start(AppInfo info, long batchID) {
        this.batchID = batchID;
        if (!isStart) {
            this.mAppInfo_Current = info;
            batteryPercent_before = 0;
            sipper_Before = new MyBatterySipper();
            sipper_Before = PowerHelper.getInstance().getPkgSipper(info.packageName);
            batteryPercent_before = Util.getBatterLevel(mContext);
            mWholePowerHelper.startRecordPower();
            mBattery.start();
            Util.i("测试前 应用:" + info.appName + "  功耗:" + sipper_Before.sumPower);
            isStart = true;
        }
    }

    public PowerBean stop() {
        if (!isStart) {
            throw new IllegalStateException("plz start first");
        }
        MyBatterySipper sipper_after   = PowerHelper.getInstance().getPkgSipper(mAppInfo_Current.packageName);
        MyBatterySipper Sipper_reduce  = PowerHelper.reduceSipper(sipper_Before, sipper_after);
        int             batteryPercent = getBatteryPercent();
        double          powerMah       = mWholePowerHelper.getPowerMah();
        VoltageBean     voltageBean    = mBattery.stop();
        Util.i("测试后 应用:" + mAppInfo_Current.appName + " 功耗：" + sipper_after.sumPower + " 差值:" + Sipper_reduce.sumPower);
        isStart = false;
        return new PowerBean().setVoltageBean(voltageBean).setBatchId(batchID).setMyBatterySipper(Sipper_reduce).setBatteryPercent(batteryPercent).setWholePower(powerMah).setAppInfo(mAppInfo_Current);
    }

    private int getBatteryPercent() {
        Util.i("after:" + Util.getBatterLevel(mContext) + "  before:" + batteryPercent_before);
        return batteryPercent_before - Util.getBatterLevel(mContext);
    }

    public static class PowerBean {


        public MyBatterySipper myBatterySipper;
        public int             batteryPercent;
        public double          wholePower;
        public AppInfo         appInfo;
        public VoltageBean     voltageBean;
        public double coverage = 0.0;
        public long batchId=0;

        PowerBean setMyBatterySipper(MyBatterySipper myBatterySipper) {
            this.myBatterySipper = myBatterySipper;
            return this;
        }

        PowerBean setBatteryPercent(int batteryPercent) {
            this.batteryPercent = batteryPercent;
            return this;
        }

        PowerBean setWholePower(double wholePower) {
            this.wholePower = wholePower;
            return this;
        }

        public PowerBean setAppInfo(AppInfo appInfo) {
            this.appInfo = appInfo;
            return this;
        }

        public PowerBean setVoltageBean(VoltageBean voltageBean) {
            this.voltageBean = voltageBean;
            return this;
        }

        public PowerBean setCoverage(double coverage) {
            this.coverage = coverage;
            return this;
        }

        public PowerBean setBatchId(long batchId) {
            this.batchId = batchId;
            return this;
        }
    }
}

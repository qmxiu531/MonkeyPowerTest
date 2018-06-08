package autotest.gionee.automonkeypowertest.bean;

import java.io.Serializable;

import autotest.gionee.automonkeypowertest.R;

public class ReportBean implements Serializable{

    public String time                = "";
    public String softVersion         = "";
    public String appVersion          = "";
    public String appName             = "";
    public String pkgName             = "";
    public Double power_front         = 0.0;
    public Double power_back          = 0.0;
    public int    duration            = 20;
    public int    duration_back       = 20;
    public int    batteryPercent      = 0;
    public int    batteryPercent_back = 0;
    public double wholePower_front    = 0;
    public double wholePower_back     = 0;
    public Double power_front_avg;
    public Double power_back_avg;
    public String      coverage_front = "";
    public String      coverage_back  = "";
    public int         testType       = -1;
    public VoltageBean voltageBean_F  = new VoltageBean();
    public VoltageBean voltageBean_B  = new VoltageBean();

    public static ReportBean get() {
        return new ReportBean();
    }

    public String getTestTypeName() {
        return toTestTypeName(testType);
    }

    public static String toTestTypeName(int t) {
        return t == R.id.monkeyTest ? "monkey" :(t == R.id.traverseAppTest ? "igo" : "");
    }

    public ReportBean setWholePower_front(double wholePower_front) {
        this.wholePower_front = wholePower_front;
        return this;
    }

    public ReportBean setWholePower_back(double wholePower_back) {
        this.wholePower_back = wholePower_back;
        return this;
    }

    public ReportBean setBatteryPercent(int batteryPercent) {
        this.batteryPercent = batteryPercent;
        return this;
    }

    public ReportBean setBatteryPercent_back(int batteryPercent_back) {
        this.batteryPercent_back = batteryPercent_back;
        return this;
    }

    public ReportBean setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public ReportBean setDuration_Back(int duration) {
        this.duration_back = duration;
        return this;
    }

    public String getTime() {
        return time;
    }

    public ReportBean setTime(String time) {
        this.time = time;
        return this;
    }

    public ReportBean setSoftVersion(String softVersion) {
        this.softVersion = softVersion;
        return this;
    }

    public ReportBean setAppVersion(String appVersion) {
        this.appVersion = appVersion;
        return this;
    }

    public ReportBean setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public ReportBean setPkgName(String pkgName) {
        this.pkgName = pkgName;
        return this;
    }

    public ReportBean setPower_front(Double power_front) {
        this.power_front = power_front;
        return this;
    }

    public ReportBean setPower_back(Double power_back) {
        this.power_back = power_back;
        return this;
    }

    public ReportBean setPower_front_avg(Double power_front_avg) {
        this.power_front_avg = power_front_avg;
        return this;
    }

    public ReportBean setPower_back_avg(Double power_back_avg) {
        this.power_back_avg = power_back_avg;
        return this;
    }

    public ReportBean setVoltageBean_B(VoltageBean voltageBean_B) {
        this.voltageBean_B = voltageBean_B;
        return this;
    }

    public ReportBean setVoltageBean_F(VoltageBean voltageBean_F) {
        this.voltageBean_F = voltageBean_F;
        return this;
    }

    public ReportBean setCoverage_front(String coverage_front) {
        this.coverage_front = coverage_front;
        return this;
    }

    public ReportBean setCoverage_back(String coverage_back) {
        this.coverage_back = coverage_back;
        return this;
    }

    public ReportBean setTestType(int testType) {
        this.testType = testType;
        return this;
    }

}

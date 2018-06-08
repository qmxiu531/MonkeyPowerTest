package autotest.gionee.automonkeypowertest.bean;


import java.util.ArrayList;
import java.util.HashMap;

import autotest.gionee.automonkeypowertest.R;

public class HtmlReportInfo {
    public ArrayList<VersionSipper> versionSippers = new ArrayList<>();

    public HtmlReportInfo setVersionSippers(ArrayList<VersionSipper> versionSippers) {
        this.versionSippers = versionSippers;
        return this;
    }

    public static class VersionSipper {
        public HashMap<String, MyBatterySipper> sipper_Map_Front = new HashMap<>();
        public HashMap<String, MyBatterySipper> sipper_Map_Back  = new HashMap<>();
//        public double                           totalPower       = 0.0;
        public String                           softVersion      = "";
        public long                             batch            = 1L;
//        public int                              appSize          = 0;
//        public int                              testType         = R.id.monkeyTest;

        public VersionSipper setSipper_Map_Front(HashMap<String, MyBatterySipper> sipper_Map_Front) {
            this.sipper_Map_Front = sipper_Map_Front;
            return this;
        }

        public VersionSipper setSipper_Map_Back(HashMap<String, MyBatterySipper> sipper_Map_Back) {
            this.sipper_Map_Back = sipper_Map_Back;
            return this;
        }

//        public VersionSipper setTotalPower(double totalPower) {
//            this.totalPower = totalPower;
//            return this;
//        }

        public VersionSipper setSoftVersion(String softVersion) {
            this.softVersion = softVersion;
            return this;
        }

        public VersionSipper setBatch(long batch) {
            this.batch = batch;
            return this;
        }

//        public VersionSipper setAppSize(int appSize) {
//            this.appSize = appSize;
//            return this;
//        }
//
//        public VersionSipper setTestType(int testType) {
//            this.testType = testType;
//            return this;
//        }
    }

    public static class MyBatterySipper {
        public  AppInfo     appInfo             = new AppInfo();
        public  double      sumPower            = 0.0;
        public  double      usagePowerMah       = 0.0;
        public  double      cpuPowerMah         = 0.0;
        public  double      cameraPowerMah      = 0.0;
        public  double      flashlightPowerMah  = 0.0;
        public  double      gpsPowerMah         = 0.0;
        public  double      mobileRadioPowerMah = 0.0;
        public  double      sensorPowerMah      = 0.0;
        public  double      wakeLockPowerMah    = 0.0;
        public  double      wifiPowerMah        = 0.0;
        public  int         duration            = 20;
        public  int         batteryPercent      = 20;
        public  double      wholePower          = 0;
        public  VoltageBean voltageBean         = new VoltageBean();
        public  double      coverage            = 0.0;
//        public  int         batch               = 0;
        public  int         isFront             = 0;
        public  String      time                = "";
        private long        batchID             = 0;

        public MyBatterySipper setDuration(int duration) {
            this.duration = duration;
            return this;
        }

        public MyBatterySipper setBatteryPercent(int batteryPercent) {
            this.batteryPercent = batteryPercent;
            return this;
        }

        public MyBatterySipper setSumPower(double sumPower) {
            this.sumPower = sumPower;
            return this;
        }

        public MyBatterySipper setUsagePowerMah(double usagePowerMah) {
            this.usagePowerMah = usagePowerMah;
            return this;
        }

        public MyBatterySipper setMobileRadioPowerMah(double mobileRadioPowerMah) {
            this.mobileRadioPowerMah = mobileRadioPowerMah;
            return this;
        }

        public MyBatterySipper setCpuPowerMah(double cpuPowerMah) {
            this.cpuPowerMah = cpuPowerMah;
            return this;
        }

        public MyBatterySipper setCameraPowerMah(double cameraPowerMah) {
            this.cameraPowerMah = cameraPowerMah;
            return this;
        }

        public MyBatterySipper setFlashlightPowerMah(double flashlightPowerMah) {
            this.flashlightPowerMah = flashlightPowerMah;
            return this;
        }

        public MyBatterySipper setGpsPowerMah(double gpsPowerMah) {
            this.gpsPowerMah = gpsPowerMah;
            return this;
        }

        public MyBatterySipper setSensorPowerMah(double sensorPowerMah) {
            this.sensorPowerMah = sensorPowerMah;
            return this;
        }

        public MyBatterySipper setWakeLockPowerMah(double wakeLockPowerMah) {
            this.wakeLockPowerMah = wakeLockPowerMah;
            return this;
        }

        public MyBatterySipper setWifiPowerMah(double wifiPowerMah) {
            this.wifiPowerMah = wifiPowerMah;
            return this;
        }

        public MyBatterySipper setWholePower(double wholePower) {
            this.wholePower = wholePower;
            return this;
        }

        public MyBatterySipper setVoltageBean(VoltageBean voltageBean) {
            this.voltageBean = voltageBean;
            return this;
        }

//        public MyBatterySipper setBatch(int batch) {
//            this.batch = batch;
//            return this;
//        }

        public MyBatterySipper setIsFront(int isFront) {
            this.isFront = isFront;
            return this;
        }

        public MyBatterySipper setTime(String time) {
            this.time = time;
            return this;
        }

        public MyBatterySipper setAppInfo(AppInfo appInfo) {
            this.appInfo = appInfo;
            return this;
        }

        public MyBatterySipper setCoverage(double coverage) {
            this.coverage = coverage;
            return this;
        }

        public MyBatterySipper setBatchID(long batchID) {
            this.batchID = batchID;
            return this;
        }

        public static class AppInfo {

            public String appName     = "";
            public String packageName = "";
            public String appVersion  = "";
            public String softVersion = "";

            AppInfo() {
            }

            public AppInfo(String appName, String packageName, String appVersion) {
                this(appName, packageName, appVersion, "");
            }

            public AppInfo(String appName, String packageName, String appVersion, String softVersion) {
                this.appName = appName;
                this.packageName = packageName;
                this.appVersion = appVersion;
                this.softVersion = softVersion;
            }

        }
    }
}

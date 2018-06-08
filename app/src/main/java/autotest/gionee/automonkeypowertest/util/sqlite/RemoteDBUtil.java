package autotest.gionee.automonkeypowertest.util.sqlite;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo;
import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo.MyBatterySipper;
import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo.MyBatterySipper.AppInfo;
import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo.VersionSipper;
import autotest.gionee.automonkeypowertest.util.Helper.PowerHelper;
import autotest.gionee.automonkeypowertest.util.Util;

public class RemoteDBUtil {
    private final static String TDAS_TEST_DATA_REFERER = "tdas_test_data_referer";
    private final static String TDAS_TEST_DATA = "tdas_test_data";
    private final static String USERNAME = "root";
    private final static String PASSWORD = "autotest";
    private final static String DB_HOST = "db.autotest.gionee.com:3306";
    private final static String DB_NAME = "dataanalysis";
    private final static String DRIVER = "com.mysql.jdbc.Driver";
    private final static String DB_URL = "jdbc:mysql://" + DB_HOST + "/" + DB_NAME + "?" + "user=" + USERNAME
            + "&password=" + PASSWORD + "&useUnicode=true&characterEncoding=UTF8";

    private static final String TDAS_TEST_DATA_REFERER_ID = "tdas_test_data_referer_id";
    private static final String App_NAME = "data_column_1";
    private static final String SOFT_VERSION = "data_column_2";
    private static final String PACK_NAME = "data_column_3";
    private static final String APP_VERSION = "data_column_4";
    private static final String SUM_POWER = "data_column_5";
    private static final String WHOLE_POWER = "data_column_6";
    private static final String CPU_POWER_MAH = "data_column_7";
    private static final String CAMERA_POWER_MAH = "data_column_8";
    private static final String FLASHLIGHT_POWER_MAH = "data_column_9";
    private static final String GPS_POWER_MAH = "data_column_10";
    private static final String MOBILE_RADIO_POWER_MAH = "data_column_11";
    private static final String SENSOR_POWER_MAH = "data_column_12";
    private static final String WAKELOCK_POWER_MAH = "data_column_13";
    private static final String WIFI_POWER_MAH = "data_column_14";
    private static final String IS_FRONT = "data_column_15";
    private static final String DURATION = "data_column_16";
    private static final String TIME = "data_column_17";
    private static final String MODEL = "data_column_18";
    private Statement stmt = null;
    private Connection conn = null;

    private Connection getConnection() throws ClassNotFoundException, SQLException {
        if (conn == null) {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(DB_URL);
        }
        return conn;
    }

    public boolean isConnect() {
        if (conn != null) {
            return true;
        }
        try {
            getConnection();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void close() {
        if (stmt != null) {
            try {
                stmt.close();
                stmt = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private Statement getStatement() {
        try {
            if (conn == null) {
                conn = getConnection();
            }
            if (stmt == null) {
                stmt = conn.createStatement();
            }
        } catch (SQLException | ClassNotFoundException e) {
            Util.i("连接异常" + e.toString());
        }
        return stmt;
    }

    public boolean uploadVersionSipper(VersionSipper sipper, int test_data_referer_id) {
        try {
            ResultSet resultSet = getStatement().executeQuery("select * from " + TDAS_TEST_DATA + " Where tdas_test_data_referer_id=" + test_data_referer_id);
            if (resultSet.next()) {
                del(test_data_referer_id);
            }
            insert(sipper, test_data_referer_id);
        } catch (SQLException e) {
            Util.i(e.toString());
            return false;
        }
        return true;
    }

    private boolean insert(VersionSipper sipper, int test_data_referer_id) {
        HashMap<String, MyBatterySipper> sipper_map_front = sipper.sipper_Map_Front;
        HashMap<String, MyBatterySipper> sipper_Map_Back = sipper.sipper_Map_Back;
        for (String s : sipper_map_front.keySet()) {
            insertBatterySipper(sipper_map_front.get(s), sipper.softVersion, test_data_referer_id);
            MyBatterySipper myBatterySipper = sipper_Map_Back.get(s);
            myBatterySipper = myBatterySipper == null ? new MyBatterySipper() : myBatterySipper;
            insertBatterySipper(myBatterySipper, sipper.softVersion, test_data_referer_id);
        }
        return true;
    }

    private boolean insertBatterySipper(MyBatterySipper sipper, String softVersion, int test_data_referer_id) {
        AppInfo info = sipper.appInfo;
        String sql = "INSERT INTO " + TDAS_TEST_DATA + "(" + TDAS_TEST_DATA_REFERER_ID + ", " + SOFT_VERSION + "," + App_NAME + ","
                + PACK_NAME + "," + APP_VERSION + "," + SUM_POWER + "," + WHOLE_POWER + "," + CPU_POWER_MAH + ","
                + CAMERA_POWER_MAH + "," + FLASHLIGHT_POWER_MAH + "," + GPS_POWER_MAH + "," + MOBILE_RADIO_POWER_MAH
                + "," + SENSOR_POWER_MAH + "," + WAKELOCK_POWER_MAH + "," + WIFI_POWER_MAH + "," + IS_FRONT + ","
                + DURATION + "," + TIME + "," + MODEL
                + ") VALUE ("
                + test_data_referer_id + ",'" + softVersion + "','" + info.appName + "','" + info.packageName + "','"
                + info.appVersion + "','"
                + PowerHelper.setDoubleScale(sipper.sumPower, 2) + "','"
                + sipper.wholePower + "','"
                + PowerHelper.setDoubleScale(sipper.cpuPowerMah, 2) + "','"
                + PowerHelper.setDoubleScale(sipper.cameraPowerMah, 2) + "','"
                + PowerHelper.setDoubleScale(sipper.flashlightPowerMah, 2) + "','"
                + PowerHelper.setDoubleScale(sipper.gpsPowerMah, 2) + "','"
                + PowerHelper.setDoubleScale(sipper.mobileRadioPowerMah, 2) + "','"
                + PowerHelper.setDoubleScale(sipper.sensorPowerMah, 2) + "','"
                + PowerHelper.setDoubleScale(sipper.wakeLockPowerMah, 2) + "','"
                + PowerHelper.setDoubleScale(sipper.wifiPowerMah, 2) + "',"
                + sipper.isFront + "," + sipper.duration + ",'" + Util.getCurrentTime() + "','"
                + Util.getModel() + "')";
        try {
            getStatement().execute(sql);
        } catch (SQLException e) {
            Util.i(e.toString());
            return false;
        }
        return true;
    }

    private void del(int test_data_referer_id) {
        String sql = "DELETE FROM " + TDAS_TEST_DATA + " WHERE tdas_test_data_referer_id=" + test_data_referer_id;
        try {
            getStatement().execute(sql);
        } catch (SQLException e) {
            Util.i("删除失败" + e.toString());
        }
    }

    public int createProjectData() {
        try {
            ResultSet resultSet = getStatement().executeQuery("select * from " + TDAS_TEST_DATA_REFERER + " where projectVersion='" + Util.getSoftVersion() + "'" + " and tmpl_id =102");
            if (resultSet.next()) {
                return resultSet.getInt("test_data_referer_id");
            } else {
                getStatement().execute("insert into " + TDAS_TEST_DATA_REFERER + " (tmpl_id,projectVersion,report_tester_name,isValid)values(102,'" + Util.getSoftVersion() + "','宋研聪',1)");
                return createProjectData();
            }
        } catch (SQLException e) {
            Util.i("新建失败" + e.toString());
        }
        return 0;
    }


    public HtmlReportInfo getHtmlVersionSipper(ArrayList<Integer> ids) {
        ArrayList<VersionSipper> arrayList = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            int id = ids.get(i);
            String version = id2Version(id);
            VersionSipper versionSipper = getVersionSipper(version, id);
            HashMap<String, MyBatterySipper> sipper_Map_Front = versionSipper.sipper_Map_Front;
            if (sipper_Map_Front != null && sipper_Map_Front.size() > 0) {
                arrayList.add(versionSipper);
            }
        }
        HtmlReportInfo htmlReportInfo = new HtmlReportInfo();
        htmlReportInfo.setVersionSippers(arrayList);
        return htmlReportInfo;
    }

    private ArrayList<Integer> getRemoteIDs() {
        ArrayList<Integer> ids = new ArrayList<>();
        try {
            ResultSet resultSet = getStatement().executeQuery(
                    "select test_data_referer_id from "
                            + TDAS_TEST_DATA_REFERER + " where tmpl_id=102");
            while (resultSet.next()) {
                int id = resultSet.getInt("test_data_referer_id");
                if (!ids.contains(id)) {
                    ids.add(id);
                }
            }
        } catch (SQLException e) {
            Util.i(e.toString());
        }
        return ids;
    }

    public ArrayList<RemoteVersionName> getRemoteVersionNameList(String model) {
        ArrayList<RemoteVersionName> RemoteVersionName_list = new ArrayList<>();
        String ids_powerTest = "";
        ArrayList<Integer> remoteIDs = getRemoteIDs();
        for (int i = 0; i < remoteIDs.size(); i++) {
            ids_powerTest = ids_powerTest + (i == 0 ? "" : ",") + remoteIDs.get(i);
        }
        try {
            ResultSet cursor = getStatement().executeQuery(
                    "select * from " + TDAS_TEST_DATA
                            + " where " + MODEL + "='" + model + "' and tdas_test_data_referer_id in (" + ids_powerTest + ")");
            while (cursor.next()) {
                int id = cursor.getInt(cursor.findColumn("tdas_test_data_referer_id"));
                String version = cursor.getString(cursor.findColumn(SOFT_VERSION));
                RemoteVersionName remoteVersionName = new RemoteVersionName(id, version);
                if (!RemoteVersionName_list.contains(remoteVersionName)) {
                    RemoteVersionName_list.add(remoteVersionName);
                }
            }
        } catch (SQLException e) {
            Util.i(e.toString());
        }
        return RemoteVersionName_list;
    }

    private String id2Version(int id) {
        try {
            ResultSet resultSet = getStatement().executeQuery(
                    "select projectVersion from " + TDAS_TEST_DATA_REFERER
                            + " where tmpl_id=102 and test_data_referer_id='"
                            + id + "'");
            if (resultSet.next()) {
                return resultSet.getString(resultSet
                        .findColumn("projectVersion"));
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return "";
    }

    private VersionSipper getVersionSipper(String softVersion, int id) {
        ArrayList<String> pkgNames = new ArrayList<>();
        HashMap<String, MyBatterySipper> sipper_Map_F = getFrontSipperMap(id,
                pkgNames);
        HashMap<String, MyBatterySipper> sipper_Map_B = getBackSipperMap(id,
                pkgNames);
        return new VersionSipper().setSipper_Map_Front(sipper_Map_F)
                .setSipper_Map_Back(sipper_Map_B).setSoftVersion(softVersion);
//                .setAppSize(sipper_Map_F.size());
    }

    private HashMap<String, MyBatterySipper> getFrontSipperMap(int id,
                                                               ArrayList<String> pkgNames) {
        HashMap<String, MyBatterySipper> sipper_Map_F = new HashMap<>();
        try {
            ResultSet cursor = getStatement().executeQuery(
                    "select * from " + TDAS_TEST_DATA
                            + " where tdas_test_data_referer_id=" + id
                            + " And " + IS_FRONT + "=1");
            if (cursor == null || cursor.wasNull()) {
                return sipper_Map_F;
            }
            while (cursor.next()) {
                MyBatterySipper sipper = getMyBatterySipper(cursor);
                if (!pkgNames.contains(sipper.appInfo.packageName)) {
                    pkgNames.add(sipper.appInfo.packageName);
                }
                sipper_Map_F.put(sipper.appInfo.packageName, sipper);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return sipper_Map_F;
    }

    private HashMap<String, MyBatterySipper> getBackSipperMap(int id,
                                                              ArrayList<String> pkgNames) {
        HashMap<String, MyBatterySipper> sipper_Map_B = new HashMap<>();
        try {
            ResultSet back = getStatement().executeQuery(
                    "select * from " + TDAS_TEST_DATA
                            + " where tdas_test_data_referer_id=" + id
                            + " And " + IS_FRONT + "=0");
            if (back != null) {
                while (back.next()) {
                    String pkgName = back.getString(back.findColumn(PACK_NAME));
                    MyBatterySipper myBatterySipper = getMyBatterySipper(back);
                    sipper_Map_B.put(pkgName, myBatterySipper);
                }
            } else {
                for (String pkgName : pkgNames) {
                    sipper_Map_B.put(pkgName, new MyBatterySipper());
                }
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return sipper_Map_B;
    }

    private MyBatterySipper getMyBatterySipper(ResultSet cursor)
            throws SQLException {
        String appName = cursor.getString(cursor.findColumn(App_NAME));
        String appVersion = cursor.getString(cursor.findColumn(APP_VERSION));
        String sumPower = cursor.getString(cursor.findColumn(SUM_POWER));
        String pkgName = cursor.getString(cursor.findColumn(PACK_NAME));
        String softVersion = cursor.getString(cursor.findColumn(SOFT_VERSION));
        AppInfo appInfo = new AppInfo(appName, pkgName, appVersion, softVersion);
        String cpuPowerMah = cursor.getString(cursor.findColumn(CPU_POWER_MAH));
        String cameraPowerMah = cursor.getString(cursor
                .findColumn(CAMERA_POWER_MAH));
        String gpsPowerMah = cursor.getString(cursor.findColumn(GPS_POWER_MAH));
        String flashlightPowerMah = cursor.getString(cursor
                .findColumn(FLASHLIGHT_POWER_MAH));
        String wakeLockPowerMah = cursor.getString(cursor
                .findColumn(WAKELOCK_POWER_MAH));
        String mobileRadioPowerMah = cursor.getString(cursor
                .findColumn(MOBILE_RADIO_POWER_MAH));
        String wifiPowerMah = cursor.getString(cursor
                .findColumn(WIFI_POWER_MAH));
        String sensorPowerMah = cursor.getString(cursor
                .findColumn(SENSOR_POWER_MAH));
        String wholePower = cursor.getString(cursor.findColumn(WHOLE_POWER));
        wholePower = wholePower == null ? "0" : wholePower;
        String duration = cursor.getString(cursor.findColumn(DURATION));
        String isFront = cursor.getString(cursor.findColumn(IS_FRONT));
        String time = cursor.getString(cursor.findColumn(TIME));
        return new MyBatterySipper()
                .setCpuPowerMah(Double.parseDouble(cpuPowerMah))
                .setCameraPowerMah(Double.parseDouble(cameraPowerMah))
                .setSensorPowerMah(Double.parseDouble(sensorPowerMah))
                .setGpsPowerMah(Double.parseDouble(gpsPowerMah))
                .setMobileRadioPowerMah(Double.parseDouble(mobileRadioPowerMah))
                .setFlashlightPowerMah(Double.parseDouble(flashlightPowerMah))
                .setWakeLockPowerMah(Double.parseDouble(wakeLockPowerMah))
                .setWifiPowerMah(Double.parseDouble(wifiPowerMah))
                .setSumPower(Double.parseDouble(sumPower))
                .setWholePower(Double.parseDouble(wholePower))
                .setAppInfo(appInfo).setDuration(Integer.parseInt(duration))
                .setIsFront(Integer.parseInt(isFront)).setTime(time);
    }


    public static class RemoteVersionName {
        public int id;
        public String version;

        RemoteVersionName(int id, String version) {
            this.id = id;
            this.version = version;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            RemoteVersionName rvn = (RemoteVersionName) obj;
            return rvn.id == id && rvn.version.equals(version);
        }
    }
}

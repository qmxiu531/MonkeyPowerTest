package autotest.gionee.automonkeypowertest.util;

import android.app.Activity;
import android.app.filecrypt.zyt.utils.FileUtil;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo;
import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo.MyBatterySipper.AppInfo;
import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo.VersionSipper;
import autotest.gionee.automonkeypowertest.bean.PowerInfoObj;
import autotest.gionee.automonkeypowertest.bean.TestParams;
import autotest.gionee.automonkeypowertest.util.Helper.AppHelper;
import autotest.gionee.automonkeypowertest.util.sqlite.DBManager;

import static autotest.gionee.automonkeypowertest.util.sqlite.DatabaseUtil.APPVERSION;
import static autotest.gionee.automonkeypowertest.util.sqlite.DatabaseUtil.APP_NAME;
import static autotest.gionee.automonkeypowertest.util.sqlite.DatabaseUtil.BATCH_ID;
import static autotest.gionee.automonkeypowertest.util.sqlite.DatabaseUtil.BATTERY_PERCENT;
import static autotest.gionee.automonkeypowertest.util.sqlite.DatabaseUtil.CAMERA_POWER_MAH;
import static autotest.gionee.automonkeypowertest.util.sqlite.DatabaseUtil.CPU_POWER_MAH;
import static autotest.gionee.automonkeypowertest.util.sqlite.DatabaseUtil.DURATION;
import static autotest.gionee.automonkeypowertest.util.sqlite.DatabaseUtil.FLASHLIGHT_POWER_MAH;
import static autotest.gionee.automonkeypowertest.util.sqlite.DatabaseUtil.GPS_POWER_MAH;
import static autotest.gionee.automonkeypowertest.util.sqlite.DatabaseUtil.IS_FRONT;
import static autotest.gionee.automonkeypowertest.util.sqlite.DatabaseUtil.MOBILE_RADIO_POWER_MAH;
import static autotest.gionee.automonkeypowertest.util.sqlite.DatabaseUtil.PKGNAME;
import static autotest.gionee.automonkeypowertest.util.sqlite.DatabaseUtil.SENSOR_POWER_MAH;
import static autotest.gionee.automonkeypowertest.util.sqlite.DatabaseUtil.SOFTVERSION;
import static autotest.gionee.automonkeypowertest.util.sqlite.DatabaseUtil.SUM_POWER;
import static autotest.gionee.automonkeypowertest.util.sqlite.DatabaseUtil.TIME;
import static autotest.gionee.automonkeypowertest.util.sqlite.DatabaseUtil.USAGE_POWER_MAH;
import static autotest.gionee.automonkeypowertest.util.sqlite.DatabaseUtil.VOLTAGE_AVG;
import static autotest.gionee.automonkeypowertest.util.sqlite.DatabaseUtil.VOLTAGE_END;
import static autotest.gionee.automonkeypowertest.util.sqlite.DatabaseUtil.VOLTAGE_START;
import static autotest.gionee.automonkeypowertest.util.sqlite.DatabaseUtil.WAKELOCK_POWER_MAH;
import static autotest.gionee.automonkeypowertest.util.sqlite.DatabaseUtil.WHOLE_POWER;
import static autotest.gionee.automonkeypowertest.util.sqlite.DatabaseUtil.WIFI_POWER_MAH;

public class Util {
    public static boolean isTest                = false;
    public static boolean isCycle               = false;
    public static boolean isFiveMinuteCountDown = true;

    public static void i(String text) {
        Log.i(Constants.TAG, "monkeyPower==" + text);
    }

    public static void i(int text) {
        i(text + "");
    }

    public static void writeLog(String text) throws IOException {
        File file = new File(Constants.FILE_PATH + "/monkeyPowerTestLog.txt");
        if (!file.exists()) {
            boolean isCreated = file.createNewFile();
            Util.i("创建文件powerLog.tLxt：" + isCreated);
            if (!isCreated) return;
        }
        FileWriter fw = new FileWriter(file, true);
        fw.write(text + "\n");
        fw.close();
    }

    public static String getCurrentTime() {
        Date       date   = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public static String getSoftVersion() {
        String v2 = MySystemProperties.get("ro.mediatek.softVersion.release");
        String v1 = MySystemProperties.get("ro.gn.gnznvernumber");
        return v1 == null || "".equals(v1) ? v2 : v1;
    }

    public static String simplifySoftVersion(String softVersion) {
        if (softVersion != null && !"".equals(softVersion)) {
            String[] split = softVersion.split("_");
            if (split.length > 0) {
                return split[split.length - 1];
            }
        }
        return "";
    }

    public static String getProductId() {
        String id = MySystemProperties.get("ro.gn.gnproductid");
        if (id == null || "".equals(id)) {
            String version = getSoftVersion();
            if (version != null && !"".equals(version)) {
                String[] ts = getSoftVersion().split("T");
                if (ts.length > 0) {
                    return ts[0].substring(0, ts[0].lastIndexOf("_"));
                }
            }
        }
        return id;
    }

    public static String getModel() {
        return Build.MODEL;
    }

    public static HtmlReportInfo listToHtmlReportInfo(ArrayList<String> list) {
        Gson                     gson                = new Gson();
        ArrayList<VersionSipper> versionSippers_list = new ArrayList<>();
        for (String content : list) {
            VersionSipper versionSipper = gson.fromJson(content, VersionSipper.class);
            if (versionSipper != null && versionSipper.sipper_Map_Front != null && versionSipper.sipper_Map_Back != null) {
                if (!versionSipper.sipper_Map_Front.isEmpty() && !versionSipper.sipper_Map_Back.isEmpty()) {
                    versionSippers_list.add(versionSipper);
                }
            }
        }
        return new HtmlReportInfo().setVersionSippers(versionSippers_list);
    }

    public static void insertTestData(Context context) {
        AppHelper          app      = new AppHelper(context);
        ArrayList<AppInfo> mAppList = app.getAppInfo();
        Random             random   = new Random();
//        long               batch    = Preference.getLong(Constants.KEY_MAX_BATCH, Constants.DEFAULT_MAX_BATCH);
        long id = DBManager.addBatch(new TestParams().setAppSize(55));
        for (AppInfo appInfo : mAppList) {
            ContentValues map = new ContentValues();
            map.put(BATCH_ID, id);
            map.put(APP_NAME, appInfo.appName);
            map.put(PKGNAME, appInfo.packageName);
            map.put(SOFTVERSION, Util.getSoftVersion());
            map.put(APPVERSION, appInfo.appVersion);
            map.put(SUM_POWER, random.nextDouble());
            map.put(USAGE_POWER_MAH, random.nextDouble());
            map.put(CPU_POWER_MAH, random.nextDouble());
            map.put(CAMERA_POWER_MAH, random.nextDouble());
            map.put(GPS_POWER_MAH, random.nextDouble());
            map.put(FLASHLIGHT_POWER_MAH, random.nextDouble());
            map.put(MOBILE_RADIO_POWER_MAH, random.nextDouble());
            map.put(SENSOR_POWER_MAH, random.nextDouble());
            map.put(WAKELOCK_POWER_MAH, random.nextDouble());
            map.put(WIFI_POWER_MAH, random.nextDouble());
            map.put(BATTERY_PERCENT, random.nextInt(10));
            map.put(WHOLE_POWER, random.nextDouble());
            map.put(VOLTAGE_START, 4300);
            map.put(VOLTAGE_END, 4300);
            map.put(VOLTAGE_AVG, 4300);
            map.put(DURATION, 20);
//            map.put(BATCH, batch + 1);
            map.put(IS_FRONT, 1);
            map.put(TIME, Util.getCurrentTime());
            DBManager.insert(map);
            ContentValues map2 = new ContentValues();
            map.put(BATCH_ID, id);
            map2.put(APP_NAME, appInfo.appName);
            map2.put(PKGNAME, appInfo.packageName);
            map2.put(SOFTVERSION, Util.getSoftVersion());
            map2.put(APPVERSION, appInfo.appVersion);
            map2.put(SUM_POWER, random.nextDouble());
            map2.put(USAGE_POWER_MAH, random.nextDouble());
            map2.put(CPU_POWER_MAH, random.nextDouble());
            map2.put(CAMERA_POWER_MAH, random.nextDouble());
            map2.put(GPS_POWER_MAH, random.nextDouble());
            map2.put(FLASHLIGHT_POWER_MAH, random.nextDouble());
            map2.put(MOBILE_RADIO_POWER_MAH, random.nextDouble());
            map2.put(SENSOR_POWER_MAH, random.nextDouble());
            map2.put(WAKELOCK_POWER_MAH, random.nextDouble());
            map2.put(WIFI_POWER_MAH, random.nextDouble());
            map2.put(BATTERY_PERCENT, random.nextInt(10));
            map2.put(DURATION, 20);
            map2.put(WHOLE_POWER, random.nextDouble());
            map2.put(VOLTAGE_START, 4300);
            map2.put(VOLTAGE_END, 4300);
            map2.put(VOLTAGE_AVG, 4300);
//            map2.put(BATCH, batch + 1);
            map2.put(IS_FRONT, 0);
            map2.put(TIME, Util.getCurrentTime());
            DBManager.insert(map2);
        }
//        Preference.putLong(Constants.KEY_MAX_BATCH, batch + 1);
    }

    public static ArrayList<View> getAllChild(View view) {
        return getAllChild(view, null);
    }

    public static ArrayList<View> getAllChild(View view, Class<?> T) {
        ViewGroup       vp         = (ViewGroup) view;
        int             childCount = vp.getChildCount();
        ArrayList<View> views      = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            View childAt = vp.getChildAt(i);
            if (childAt instanceof ViewGroup) {
                views.addAll(getAllChild(childAt, T));
            } else {
                if (T == null || childAt.getClass().equals(T)) {
                    views.add(childAt);
                }
            }
        }
        return views;
    }

    public static String long2String(long time) {
        if (time > 0L) {
            SimpleDateFormat sf   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date             date = new Date(time);
            return sf.format(date);
        }
        return "0";
    }

    public static long string2long(String string) {
        try {
            return string2long("yyyy-MM-dd HH:mm:ss", string);
        } catch (ParseException e) {
            try {
                return string2long("yyyyMMddHHmmss", string);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        }
        return 0;
    }

    public static long string2long(String formating, String string)
            throws ParseException {
        DateFormat format = new SimpleDateFormat(formating);
        Date       date   = format.parse(string);
        return date.getTime();
    }

    public static void hideMM(Activity activity, EditText et) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    public static int getBatterLevel(Context context) {
        BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        return batteryManager.getIntProperty(BatteryManager.BATTERY_STATUS_NOT_CHARGING);
    }

    public static void home(Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static String regexMatch(String text, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    public static void writeFile(String filePath, String content) {
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(File file) {
        StringBuilder sb      = new StringBuilder();
        String        content = "";
        if (!file.exists()) {
            return content;
        }
        try {
            FileReader     fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String         s;
            while ((s = br.readLine()) != null) {
                if (!"".equals(s)) {
                    sb.append(s);
                }
            }
            fr.close();
            br.close();
        } catch (Exception e) {
            Util.i(e.toString());
        }
        return sb.toString();
    }

    public static ArrayList<PowerInfoObj> getHighPowerList() {
        return getHighPowerList(null);
    }

    public static ArrayList<PowerInfoObj> getHighPowerList(HighPowerFilter filter) {
        ArrayList<PowerInfoObj> list = new ArrayList<>();
        try {
            String content = FileUtil.read(Constants.HIGH_POWER_NAME);
            Type listType = new TypeToken<ArrayList<PowerInfoObj>>() {
            }.getType();
            list = new Gson().fromJson(content, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (filter != null) {
            ArrayList<PowerInfoObj> list2 = new ArrayList<>();
            for (PowerInfoObj info : list) {
                if (filter.accept(info)) {
                    list2.add(info);
                }
            }
            return list2;
        }
        return list;
    }

    public interface HighPowerFilter {

        boolean accept(PowerInfoObj info);
    }

}

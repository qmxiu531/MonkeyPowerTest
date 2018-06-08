package autotest.gionee.automonkeypowertest.util.Helper;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.google.gson.Gson;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo.MyBatterySipper.AppInfo;
import autotest.gionee.automonkeypowertest.bean.SelectApp;
import autotest.gionee.automonkeypowertest.util.Constants;
import autotest.gionee.automonkeypowertest.util.Preference;
import autotest.gionee.automonkeypowertest.util.ShellUtil;
import autotest.gionee.automonkeypowertest.util.Util;

public class AppHelper {
    private       HashMap<String, AppInfo> appInfoMap;
    private       Context                  mContext;
    private final ArrayList<String>        appNames;
    private final ArrayList<AppInfo>       appInfoList;
    private       HashMap<String, AppInfo> pkgNameAppInfoMap;

    public AppHelper(Context context) {
        this.mContext = context;
        appInfoMap = new HashMap<>();
        appNames = new ArrayList<>();
        appInfoList = new ArrayList<>();
        pkgNameAppInfoMap = new HashMap<>();
    }

    private void init() {
        PackageManager pm     = mContext.getPackageManager();
        Intent         intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        if (resolveInfos == null || resolveInfos.isEmpty()) {
            return;
        }
        for (ResolveInfo info : resolveInfos) {
            String name        = info.loadLabel(pm).toString();
            String packageName = info.activityInfo.packageName;
            if (isIgnoreApp(packageName)) {
                continue;
            }
            String versionName = "";
            try {
                versionName = pm.getPackageInfo(packageName, 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                Util.i("PackageManager.NameNotFoundException:" + e.toString());
            }
            AppInfo appInfo = new AppInfo(name, packageName, versionName);
            appInfoList.add(appInfo);
            appNames.add(name);
            appInfoMap.put(name, appInfo);
            pkgNameAppInfoMap.put(packageName, appInfo);
        }
    }

    public ArrayList<AppInfo> getSelectedAppInfo() {
        SelectApp                selectApp  = getSelectApp();
        ArrayList<AppInfo>       infos      = new ArrayList<>();
        HashMap<String, AppInfo> appInfoMap = getAppInfoMap();
        for (String s : selectApp.selectApps) {
            AppInfo appInfo = appInfoMap.get(s);
            if (appInfo != null) {
                infos.add(appInfo);
            }
        }
        return infos;
    }

    public HashMap<String, AppInfo> getPkgNameAppInfoMap() {
        init();
        return pkgNameAppInfoMap;
    }

    public ArrayList<AppInfo> getAppInfo() {
        init();
        return appInfoList;
    }

    public ArrayList<String> getAppNames() {
        init();
        return appNames;
    }

    public HashMap<String, AppInfo> getAppInfoMap() {
        init();
        return appInfoMap;
    }


    public void killAppProcess(String pkgName) {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            try {
                Class<?> aClass           = Class.forName("android.app.ActivityManager");
                Method   forceStopPackage = aClass.getDeclaredMethod("forceStopPackage", String.class);
                forceStopPackage.invoke(activityManager, pkgName);
                Util.i("forceStopPackage " + pkgName);
            } catch (Exception e) {
                e.printStackTrace();
                ShellUtil.execCommand("am force-stop " + pkgName, false);
            }
        }
    }

    public void launchApp(String pkgName) {
        mContext.startActivity(mContext.getPackageManager().getLaunchIntentForPackage(pkgName));
    }

    private boolean isIgnoreApp(String packageName) {
        if (packageName == null) {
            return true;
        }
        String regex = mContext.getPackageName() + "|com.android.settings|autotest.gionee.com.casehelper|com.gionee.demo|com.gionee.kidshome|com.gionee.amisystem";
        return packageName.matches(regex);
    }

    public static int getTestAppCount() {
        SelectApp selectApp = getSelectApp();
        return selectApp.selectApps.size();
    }

    public static SelectApp getSelectApp() {
        String selectAppsJson = "";
        try {
            selectAppsJson = Preference.getString(Constants.KEY_SELECT_APP, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (selectAppsJson.equals("")) {
            return SelectApp.getDefault();
        }
        SelectApp selectApp;
        try {
            selectApp = new Gson().fromJson(selectAppsJson, SelectApp.class);
        } catch (Exception e) {
            selectApp = SelectApp.getDefault();
        }
        return selectApp;
    }

    public static int pkg2Uid(Context context, String pkg) {
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo ai = pm.getApplicationInfo(pkg, PackageManager.GET_META_DATA);
            return ai.uid;
        } catch (PackageManager.NameNotFoundException e) {
            Util.i("PackageManager.NameNotFoundException" + e.toString());
        }
        return 0;
    }

}

package autotest.gionee.automonkeypowertest.util.sqlite;

import android.content.ContentValues;
import android.content.Context;

import java.util.ArrayList;

import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo;
import autotest.gionee.automonkeypowertest.bean.PowerInfoObj;
import autotest.gionee.automonkeypowertest.bean.ReportResultBean;
import autotest.gionee.automonkeypowertest.bean.TestParams;

/**
 * gionee
 * 2018/2/2
 */

public class DBManager {

    private static DatabaseUtil db;

    public static DatabaseUtil getDb() {
        return db;
    }

    public static void init(Context context) {
        db = new DatabaseUtil(context);
    }

    public static void del(String tableName, String whereClause, String[] whereArgs) {
        db.del(tableName, whereClause, whereArgs);
    }

    public static boolean isEmpty(String table) {
        return db.isEmpty(table);
    }

    public static long insert(ContentValues values) {
        return db.insert(values);
    }

    public static ArrayList<String> getColumnSample(Column column) {
        return db.getColumnSample(column);
    }

    public static void clear() {
        db.clear();
    }

    public static ArrayList<String> getAppNameList() {
        return db.getAppNameList();
    }

//    public static void saveVersionParams(ContentValues map, long batch) {
//        db.saveVersionParams(map, batch);
//    }

    public static HtmlReportInfo.VersionSipper getVersionSipper(long batch) {
        return db.getVersionSipper(batch);
    }


    public static ReportResultBean getReportBean(long batch) {
        return db.getReportBean(batch);
    }

//    public static int getMaxBatch() {
//        return db.getMaxBatch();
//    }

    public static long addBatch(TestParams params) {
        return db.addBatch(params);
    }

    public static ArrayList<Long> getBatchs() {
        return db.getBatchs();
    }

    public static TestParams getLastTestParams() {
        return db.getLastTestParams();
    }

    public static void insertHighPowerList(long batchId, ArrayList<PowerInfoObj> list) {
        db.insertHighPowerList(batchId, list);
    }
}

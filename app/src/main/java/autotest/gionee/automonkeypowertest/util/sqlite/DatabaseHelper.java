package autotest.gionee.automonkeypowertest.util.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import autotest.gionee.automonkeypowertest.util.Util;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int    DATABASE_VERSION              = 5;
    public static final  String DATABASE_NAME                 = "MonkeyPowerTest";
    public static final  String TABLE_NAME_MONKEY_POWER       = "MonkeyPower";
    //    public static final  String TABLE_NAME_MONKEY_POWER_PARAMS = "MonkeyPower_params";
    public static final  String TABLE_NAME_MONKEY_POWER_BATCH = "MonkeyPower_batch";
    private static final String DATABASE_CREATE               =
            "create table MonkeyPower(_id INTEGER PRIMARY KEY AUTOINCREMENT,batchId long,appName text,packagename text" +
                    ",softVersion text,appVersion text,sumPower Double,usagePowerMah Double,cpuPowerMah Double" +
                    ",cameraPowerMah Double,flashlightPowerMah Double,gpsPowerMah Double,mobileRadioPowerMah Double" +
                    ",sensorPowerMah Double,wakeLockPowerMah Double,wifiPowerMah Double,duration Integer," +
                    "batteryPercent Integer,wholePower Double,voltageStart Integer,voltageEnd Integer,voltageAvg Integer,coverage Double,isFront Integer,time TIMESTAMP(14))";

    public DatabaseHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        super(new DatabaseContext(context), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Util.i("onCreate sql");
        db.execSQL(DATABASE_CREATE);
//        db.execSQL("create table " + TABLE_NAME_MONKEY_POWER_PARAMS + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,totalPower Double,batch Integer,appSize Integer,testType Integer,softVersion text)");
        db.execSQL("create table " + TABLE_NAME_MONKEY_POWER_BATCH + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,testTime long,appWaitTime long,lastWaitTime long, cycleCount long,appSize Integer,testType Integer,softVersion text)");
        db.execSQL(HighPowerTable.getCommandStr());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists MonkeyPower");
//        db.execSQL("drop table if exists MonkeyPower_params");
        db.execSQL("drop table if exists " + TABLE_NAME_MONKEY_POWER_BATCH);
        db.execSQL("drop table if exists " + HighPowerTable.NAME);
        onCreate(db);
    }

    public static class HighPowerTable {
        public static final String NAME        = "HighPowerTable";
        public static final String START_TIME  = "startTime";
        public static final String START_POWER = "startPower";
        public static final String END_TIME    = "endTime";
        public static final String END_POWER   = "endPower";
        public static final String PAK_NAME    = "pakName";
        public static final String BATCH_ID    = "batchId";

        public static String getCommandStr() {
            return "create table " + NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," + BATCH_ID + " long," + START_TIME + " long,"
                    + START_POWER + " double," + END_TIME + " long," + END_POWER + " double," + PAK_NAME + " text)";
        }
    }
}

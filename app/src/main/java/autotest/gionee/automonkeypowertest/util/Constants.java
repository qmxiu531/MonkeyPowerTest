package autotest.gionee.automonkeypowertest.util;

import android.os.Environment;

import java.io.File;

import autotest.gionee.automonkeypowertest.R;

public class Constants {
    public static final String TAG                     = "gionee.os.autotest";
    public static final String ACTION_NEXT_TEST        = "gionee.autotest.automonkeypowertest.test";
    public static final String ACTION_SAVE_FRONT_POWER = "gionee.autotest.automonkeypowertest.save";
    public static final String ACTION_TEST_FINISH      = "testFinish";
    public static final String FILE_PATH               = Environment.getExternalStorageDirectory() + File.separator + "AutoMonkeyPowerTest" + File.separator;
    public static final String WHOLE_POWER_PATH        = "sys/class/power_supply/battery/coulomb_count";
    public static final String EXCEL_NAME              = FILE_PATH + "相同版本对比数据.xls";
    public static final long   DEFAULT_MAX_BATCH       = 0L;
    public static final int    DEFAULT_DIGIT_KEEP      = 3;
    public static final int    DEFAULT_THROTTLE_MONKEY = 500;
    public static final double NA                      = -3.1415926535897932384626;
    public static final String ACTION_ACCESS_ENABLED   = "automonkeypowertest.action_access_enabled";

    public static final boolean DEFAULT_BUG_REPORT            = true;
    public static final boolean DEFAULT_NAV                   = false;
    public static final boolean DEFAULT_TRACKBALL             = false;
    public static final boolean DEFAULT_APP_SWITCH            = false;
    public static final boolean DEFAULT_IS_STOP_MUSIC         = false;
    public static final long    DEFAULT_TEST_TIME             = 20L;
    public static final long    DEFAULT_WAIT_TIME             = 1L;
    public static final long    DEFAULT_LAST_WAIT_TIME        = 0L;
    public static final String  DEFAULT_SELECT_APP            = "记事本";
    public static final int     DEFAULT_TEST_TYPE_ID          = -1;
    public static final long    DEFAULT_CYCLE_COUNT           = 1L;
    public static final String  DEFAULT_ADDRESS_JSON          = "";
    public static final String  DEFAULT_RECEIVER_EMAILS       = "songyc@gionee.com";
    public static final boolean DEFAULT_DISCHARGE             = true;
    public static final boolean DEFAULT_KILL_APP_AFTER_MONKEY = true;
    public static final int     DEFAULT_IGO_TIME              = 60 * 48;
    public static final boolean DEFAULT_SCREEN_OFF            = false;

    public static final String KEY_CURRENT_TEST_PKG_INDEX = "currentTestPkgIndex";
    public static final String KEY_TEST_TIME              = "testTime";
    public static final String KEY_MAX_BATCH              = "maxBatch";
    public static final String KEY_APP_SIZE               = "appSize";
    public static final String KEY_LAST_SELECT_BATCH      = "lastSelectBatch";
    public static final String KEY_WAIT_TIME              = "waitTime";
    public static final String KEY_LAST_WAIT_TIME         = "lastWaitTime";
    public static final String KEY_IS_SEND_DEFAULT_EMAIL  = "isSendDefaultEmail";
    public static final String KEY_SELECT_APP             = "selectApp";
    public static final String KEY_BUG_REPORT             = "bugReport";
    public static final String KEY_NAV                    = "nav";
    public static final String KEY_TRACKBALL              = "trackball";
    public static final String KEY_APP_SWITCH             = "appSwitch";
    public static final String KEY_DISCHARGE              = "disCharge";
    public static final String KEY_IS_STOP_MUSIC          = "isStopMusic";
    public static final String KEY_CYCLE_COUNT            = "cycle_count";
    public static final String KEY_TEST_TYPE_ID           = "testType_id";
    public static final String KEY_THROTTLE_MONKEY        = "throttle_monkey";
    public static final String KEY_DIGIT_KEEP             = "digit_keep";
    public static final String KEY_ADDRESS_JSON           = "defaultAddress_json";
    public static final String KEY_RECEIVER_EMAILS        = "receiverEmails";
    public static final String KEY_KILL_APP_AFTER_MONKEY  = "needKillAppAfterMonkey";
    public static final String KEY_SCREEN_OFF             = "screenOff";

    public static final String ELITOR_CLOCK = "autotest.gionee.automonkeypowertest.elitor.clock";//广播意图
    public static final String START_POWER = "start_power";//开始功耗数据
    public static final String HIGH_POWER_NAME = FILE_PATH+"high_power.txt";//高功耗文件名字
    public static final long HIGH_POWER_TIME = 10*60*1000;//高功耗监控间隔



}

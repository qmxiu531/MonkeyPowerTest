package autotest.gionee.automonkeypowertest.util;


import autotest.gionee.automonkeypowertest.R;
import autotest.gionee.automonkeypowertest.bean.SettingsInfo;
import autotest.gionee.automonkeypowertest.bean.TestParams;

public class Configurator {
    public static boolean isMonitorPermit = false;
    private static TestParams params;

    public static void setIsMonitorPermit(boolean isMonitorPermit) {
        Configurator.isMonitorPermit = isMonitorPermit;
    }

    public static boolean needKillAppAfterMonkey() {
        return Preference.getBoolean(Constants.KEY_KILL_APP_AFTER_MONKEY, Constants.DEFAULT_KILL_APP_AFTER_MONKEY);
    }

    public static void setTestParams(TestParams params) {
        Configurator.params = params;
    }

    public static TestParams getParams() {
        return params;
    }

    //
//    public static void setTestTime(long testTime) {
//        Preference.putLong(Constants.KEY_TEST_TIME, testTime);
//    }
//
//    public static long getTestTime() {
//        return Preference.getLong(Constants.KEY_TEST_TIME, Constants.DEFAULT_TEST_TIME);
//    }
//
//    public static long getWaitTime() {
//        return Preference.getLong(Constants.KEY_WAIT_TIME, Constants.DEFAULT_WAIT_TIME);
//    }
//
//    public static long getCycleCount() {
//        return Preference.getLong(Constants.KEY_CYCLE_COUNT, Constants.DEFAULT_CYCLE_COUNT);
//    }
//
//    public static long getLastWaitTime() {
//        return Preference.getLong(Constants.KEY_LAST_WAIT_TIME, Constants.DEFAULT_LAST_WAIT_TIME);
//    }
//
//    public static void setTestType(int testType) {
//        Preference.putInt(Constants.KEY_TEST_TYPE_ID, testType);
//    }
//
//    public static int getTestType() {
//        return Preference.getInt(Constants.KEY_TEST_TYPE_ID, Constants.DEFAULT_TEST_TYPE_ID);
//    }

    public static SettingsInfo getSettings() {
        int     keeps              = Preference.getInt(Constants.KEY_DIGIT_KEEP, Constants.DEFAULT_DIGIT_KEEP);
        boolean bugReport          = Preference.getBoolean(Constants.KEY_BUG_REPORT, Constants.DEFAULT_BUG_REPORT);
        boolean nav                = Preference.getBoolean(Constants.KEY_NAV, Constants.DEFAULT_NAV);
        boolean trackball          = Preference.getBoolean(Constants.KEY_TRACKBALL, Constants.DEFAULT_TRACKBALL);
        boolean appSwitch          = Preference.getBoolean(Constants.KEY_APP_SWITCH, Constants.DEFAULT_APP_SWITCH);
        boolean isStopMusic        = Preference.getBoolean(Constants.KEY_IS_STOP_MUSIC, Constants.DEFAULT_IS_STOP_MUSIC);
        boolean disCharge          = Preference.getBoolean(Constants.KEY_DISCHARGE, Constants.DEFAULT_DISCHARGE);
        boolean killAppAfterMonkey = Preference.getBoolean(Constants.KEY_KILL_APP_AFTER_MONKEY, Constants.DEFAULT_KILL_APP_AFTER_MONKEY);
        boolean screenOff          = Preference.getBoolean(Constants.KEY_SCREEN_OFF, Constants.DEFAULT_SCREEN_OFF);
        int     monkey_throttle    = Preference.getInt(Constants.KEY_THROTTLE_MONKEY, Constants.DEFAULT_THROTTLE_MONKEY);
        return new SettingsInfo().setBugReport(bugReport).setNav(nav).setTrackball(trackball).setAppSwitch(appSwitch)
                .setIsStopMusic(isStopMusic).setDisCharge(disCharge).setKillAppAfterMonkey(killAppAfterMonkey)
                .setMonkeyThrottle(monkey_throttle).setDigitKeep(keeps).setScreenOff(screenOff);
    }

    public static void setSettingsInfo(SettingsInfo info) {
        Preference.putInt(Constants.KEY_DIGIT_KEEP, info.digitKeep);
        Preference.putBoolean(Constants.KEY_BUG_REPORT, info.bugReport);
        Preference.putBoolean(Constants.KEY_NAV, info.nav);
        Preference.putBoolean(Constants.KEY_TRACKBALL, info.trackball);
        Preference.putBoolean(Constants.KEY_APP_SWITCH, info.appSwitch);
        Preference.putBoolean(Constants.KEY_IS_STOP_MUSIC, info.isStopMusic);
        Preference.putBoolean(Constants.KEY_DISCHARGE, info.disCharge);
        Preference.putBoolean(Constants.KEY_KILL_APP_AFTER_MONKEY, info.killAppAfterMonkey);
        Preference.putBoolean(Constants.KEY_SCREEN_OFF, info.screenOff);
        Preference.putInt(Constants.KEY_THROTTLE_MONKEY, info.monkeyThrottle);
    }

    public static void setTestType(int testType) {
        Preference.putInt(Constants.KEY_TEST_TYPE_ID, testType);
    }

    public static int getTestType() {
        int anInt = Preference.getInt(Constants.KEY_TEST_TYPE_ID, R.id.monkeyTest);
        Util.i("当前选择的测试类型是" + (anInt == R.id.monkeyTest ? "monkey" : anInt == R.id.traverseAppTest ? "igo" : anInt));
        return (anInt == R.id.monkeyTest || anInt == R.id.traverseAppTest) ? anInt : R.id.monkeyTest;
    }
}

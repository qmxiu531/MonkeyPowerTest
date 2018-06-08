package autotest.gionee.automonkeypowertest.util.Helper;


import android.content.Context;

import java.util.ArrayList;

import autotest.gionee.automonkeypowertest.util.Command;
import autotest.gionee.automonkeypowertest.util.Configurator;
import autotest.gionee.automonkeypowertest.util.Constants;
import autotest.gionee.automonkeypowertest.util.Preference;
import autotest.gionee.automonkeypowertest.util.Util;
import gionee.autotest.AccessUtil;

public class MonkeyHelper {
    private static MonkeyHelper instance = new MonkeyHelper();
    private Thread monkeyThread;

    private MonkeyHelper() {
    }

    public static MonkeyHelper getInstance() {
        if (instance == null) {
            instance = new MonkeyHelper();
        }
        return instance;
    }

    public void testPkg(String pkgName, long event, MonkeyParams monkeyParams) {
        monkeyThread = new Thread() {
            @Override
            public void run() {
                String paramsString = (monkeyParams.appSwitch ? " --pct-appswitch 0 " : " ") + (monkeyParams.bugReport ? " --bugreport " : " ") + (monkeyParams.nav ? " --pct-nav 0 " : "") + (monkeyParams.trackball ? " --pct-trackball 0 " : "");
                String cmd          = "monkey -p " + pkgName + " -v -v -v -s 1 --throttle " + monkeyParams.monkeyThrottle + "  --ignore-crashes --ignore-security-exceptions --ignore-timeouts " + paramsString + event + " 1>/mnt/sdcard/AutoMonkeyPowerTest/monkeyLog.txt 2>/mnt/sdcard/AutoMonkeyPowerTest/monkeyErrorLog.txt";
                Util.i(cmd);
                Command.execCommand(cmd, false);
            }
        };
        monkeyThread.start();
    }

    public boolean isMonkeyRunning() {
        return monkeyThread != null && monkeyThread.isAlive();
    }

    public static void kill() {
        Util.i("--------------------kill-------------");
        int pid = getMonkeyPid(System.currentTimeMillis());
        if (pid != 0) {
            Command.CommandResult cr = Command.execCommand("kill -9 " + pid, false);
            Util.i("--------------杀掉monkey" + (cr.errorMsg.isEmpty() ? "成功" : "失败") + "------------");
        } else {
            Util.i("-------------pid为0,可能无monkey进程---------------------------");
        }
    }

    private static int getMonkeyPid(long currentTimes) {
        Command.CommandResult commandResult = Command.execCommand("ps|grep com.android.commands.monkey", false);
        String[]              split         = commandResult.successMsg.split(" ");
        ArrayList<String>     list          = new ArrayList<>();
        for (String s : split) {
            if (" ".equals(s) || "".equals(s)) {
                continue;
            }
            list.add(s);
        }
        int pid = list.size() > 2 ? Integer.parseInt(list.get(1)) : 0;
        if (pid == 0 && (System.currentTimeMillis() - currentTimes < 3000)) {
            return getMonkeyPid(currentTimes);
        }
        return pid;
    }

    public void startPermitMonitor(Context context) {
        AccessUtil accessUtil = new AccessUtil(context);
        accessUtil.setServiceEnable(true);
        Configurator.setIsMonitorPermit(true);
    }

    public void stopPermitMonitor(Context context) {
        AccessUtil accessUtil = new AccessUtil(context);
        accessUtil.setServiceEnable(false);
        Configurator.setIsMonitorPermit(false);
    }

    public MonkeyParams getMonkeyParams() {
        boolean bugReport          = Preference.getBoolean(Constants.KEY_BUG_REPORT, Constants.DEFAULT_BUG_REPORT);
        boolean nav                = Preference.getBoolean(Constants.KEY_NAV, Constants.DEFAULT_NAV);
        boolean trackball          = Preference.getBoolean(Constants.KEY_TRACKBALL, Constants.DEFAULT_TRACKBALL);
        boolean appSwitch          = Preference.getBoolean(Constants.KEY_APP_SWITCH, Constants.DEFAULT_APP_SWITCH);
        boolean isStopMusic        = Preference.getBoolean(Constants.KEY_IS_STOP_MUSIC, Constants.DEFAULT_IS_STOP_MUSIC);
        boolean disCharge          = Preference.getBoolean(Constants.KEY_DISCHARGE, Constants.DEFAULT_DISCHARGE);
        boolean killAppAfterMonkey = Preference.getBoolean(Constants.KEY_KILL_APP_AFTER_MONKEY, Constants.DEFAULT_KILL_APP_AFTER_MONKEY);
        int     monkey_throttle    = Preference.getInt(Constants.KEY_THROTTLE_MONKEY, Constants.DEFAULT_THROTTLE_MONKEY);
        MonkeyParams monkeyParams  = new MonkeyParams(bugReport, nav, trackball, appSwitch, isStopMusic, disCharge, killAppAfterMonkey).setMonkeyThrottle(monkey_throttle);
        Util.i(monkeyParams.toString());
        return monkeyParams;
    }

    public static class MonkeyParams {
        public boolean bugReport          = true;
        public boolean nav                = true;
        public boolean trackball          = true;
        public boolean appSwitch          = true;
        public boolean isStopMusic        = true;
        public boolean disCharge          = true;
        public boolean killAppAfterMonkey = true;
        public int monkeyThrottle;

        public MonkeyParams(boolean bugReport, boolean nav, boolean trackball, boolean appSwitch, boolean isStopMusic, boolean disCharge, boolean killAppAfterMonkey) {
            this.bugReport = bugReport;
            this.nav = nav;
            this.trackball = trackball;
            this.appSwitch = appSwitch;
            this.isStopMusic = isStopMusic;
            this.disCharge = disCharge;
            this.killAppAfterMonkey = killAppAfterMonkey;
        }

        public MonkeyParams setMonkeyThrottle(int monkeyThrottle) {
            this.monkeyThrottle = monkeyThrottle;
            return this;
        }

        @Override
        public String toString() {
            return "MonkeyParams::bugReport="+bugReport+" nav="+nav+" trackball="+trackball+" appSwitch="+appSwitch+" isStopMusic="+isStopMusic+" disCharge="+disCharge+" needKillAppAfterMonkey="+killAppAfterMonkey;
        }
    }
}

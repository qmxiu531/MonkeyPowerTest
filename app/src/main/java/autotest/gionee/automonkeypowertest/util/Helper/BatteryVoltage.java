package autotest.gionee.automonkeypowertest.util.Helper;


import java.io.File;

import autotest.gionee.automonkeypowertest.bean.VoltageBean;
import autotest.gionee.automonkeypowertest.util.EmailHtml.FileUtil;
import autotest.gionee.automonkeypowertest.util.MySystemProperties;
import autotest.gionee.automonkeypowertest.util.Util;

public class BatteryVoltage {

    private int voltageStart = 0;

    private static boolean isMTK() {
        return MySystemProperties.get("ro.gn.platform.support").contains("MTK");
    }

    public int getVoltage() {
        File   file    = new File("/sys/class/power_supply/battery/", isMTK() ? "batt_vol" : "voltage_now");
        String text    = FileUtil.readText(file);
        int    voltage;
        try {
            voltage = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
        return voltage;
    }

    public void start() {
        voltageStart = getVoltage();
        Util.i("volStr" + voltageStart);
    }

    public VoltageBean stop() {
        int voltageEnd = getVoltage();
        Util.i("start=" + voltageStart + " end=" + voltageEnd);
        return new VoltageBean(voltageStart, voltageEnd, (voltageEnd + voltageStart) / 2);
    }
}

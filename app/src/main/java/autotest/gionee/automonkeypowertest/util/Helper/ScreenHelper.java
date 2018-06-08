package autotest.gionee.automonkeypowertest.util.Helper;

import java.io.File;
import java.io.IOException;

import autotest.gionee.automonkeypowertest.util.ShellUtil;
import autotest.gionee.automonkeypowertest.util.Util;

/**
 * gionee
 * 2018/1/18
 */

public class ScreenHelper {
    private static final String BRIGHTNESS        = "brightness";
    private static final String BRIGHTNESS_ENABLE = "brightness_enable";
    private static final String BRIGHTNESS_PATH   = "/sys/class/leds/lcd-backlight/";

    public static void setScreenValue(int value) {
        File   file   = new File(BRIGHTNESS_PATH + BRIGHTNESS);
        String before = Util.readFile(file);
        Util.writeFile(BRIGHTNESS_PATH + BRIGHTNESS, String.valueOf(value));
        String after = Util.readFile(file);
        Util.i("valueBefore=" + before + " valueAfter=" + after);
    }


    public static void setScreenEnable(boolean isEnabled) {
        String                  before        = Util.readFile(new File(BRIGHTNESS_PATH + BRIGHTNESS_ENABLE));
        String                  cmd           = String.format("echo \"%1$s\">%2$s", String.valueOf(isEnabled ? 1 : 0), BRIGHTNESS_PATH + BRIGHTNESS_ENABLE);
        ShellUtil.CommandResult commandResult = ShellUtil.execCommand(cmd, true);
        String                  content       = Util.readFile(new File(BRIGHTNESS_PATH + BRIGHTNESS_ENABLE));
        Util.i("cmd=" + cmd + "result=" + commandResult.result + "  success=" + commandResult.successMsg + " error= " + commandResult.errorMsg + "before=" + before + " currentValue=" + content);
    }

}

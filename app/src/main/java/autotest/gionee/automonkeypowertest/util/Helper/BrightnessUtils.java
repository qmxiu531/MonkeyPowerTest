package autotest.gionee.automonkeypowertest.util.Helper;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.view.WindowManager;


/*
 * Android调节屏幕亮度工具类
 * by itas109
 * http://blog.csdn.net/itas109
 *
 * 注意：需要添加setting权限
 * <uses-permission android:name="android.permission.WRITE_SETTINGS" />
 */
public class BrightnessUtils {
    public static boolean isAutoBrightness(Context context) {
        boolean IsAutoBrightness = false;
        try {
            IsAutoBrightness = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return IsAutoBrightness;
    }

    public static int getScreenBrightness(Context context) {
        int             nowBrightnessValue = 0;
        ContentResolver resolver           = context.getContentResolver();
        try {
            nowBrightnessValue = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

    // 程序退出之后亮度失效
    public static void setCurWindowBrightness(Context context, int brightness) {
        if (isAutoBrightness(context)) {
            setAutoBrightnessEnable(context, false);
        }
        Activity                   activity = (Activity) context;
        WindowManager.LayoutParams lp       = activity.getWindow().getAttributes();
        if (brightness < 1) {
            brightness = 1;
        }
        if (brightness > 255) {
            brightness = 255;
        }
        lp.screenBrightness = (float) brightness * (1f / 255f);
        activity.getWindow().setAttributes(lp);
    }

    // 程序退出之后亮度依旧有效
    public static void setSystemBrightness(Context context, int brightness) {
        if (brightness < 1) {
            brightness = 1;
        }
        if (brightness > 255) {
            brightness = 255;
        }
        saveBrightness(context, brightness);
    }

    public static void setAutoBrightnessEnable(Context context, boolean isEnabled) {
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                isEnabled ? Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC : Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    public static void saveBrightness(Context context, int brightness) {
        Uri uri = Settings.System.getUriFor("screen_brightness");
        Settings.System.putInt(context.getContentResolver(), "screen_brightness", brightness);
        context.getContentResolver().notifyChange(uri, null);
    }

}
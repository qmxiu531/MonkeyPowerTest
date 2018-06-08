package autotest.gionee.automonkeypowertest.util.sqlite;

/**
 * @项目名： AutoChargeMap
 * @包名： com.gionee.autochargemap.utils
 * @文件名: ChargeUtils
 * @创建者: gionee
 * @创建时间: 2017/3/25 15:09
 * @描述： 获取充电的电流和电压
 */


import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ChargeUtils {
    public static final  int    ChargeUtils_TYPE_CURRENT = 0x00000000;
    public static final  int    ChargeUtils_TYPE_VOLTAGE = 0x00000001;
    public static final  int    ChargeUtils_TYPE_TEMP    = 0x00000002;
    //高通
    private static final String QCOM_CURRENT_NOW         = "/sys/class/power_supply/battery/current_now";//电流
    private static final String QCOM_VOLTAGE_NOW         = "/sys/class/power_supply/battery/voltage_now";//电压
    private static final String QCOM_TEMP                = "/sys/class/power_supply/battery/temp";//温度

    //MTK
    private static final String MTK_CURRENT_NOW = "/sys/class/power_supply/battery/BatteryAverageCurrent";//电流
    private static final String MTK_VOLTAGE_NOW = "/sys/class/power_supply/battery/batt_vol";//电压
    private static final String MTK_TEMP        = "/sys/class/power_supply/battery/batt_temp";//温度


    //获得充电电压 充电电压 温度
    public static String getCharge(int type) {
        String mFileName = null;
//        Log.i("cup:" + getCpu());
        if (getCpu().contains("qcom")) {
            switch (type) {
                case ChargeUtils_TYPE_CURRENT:
                    mFileName = QCOM_CURRENT_NOW;
                    break;
                case ChargeUtils_TYPE_VOLTAGE:
                    mFileName = QCOM_VOLTAGE_NOW;
                    break;
                case ChargeUtils_TYPE_TEMP:
                    mFileName = QCOM_TEMP;
                    break;
            }
        } else {
            switch (type) {
                case ChargeUtils_TYPE_CURRENT:
                    mFileName = MTK_CURRENT_NOW;
                    break;
                case ChargeUtils_TYPE_VOLTAGE:
                    mFileName = MTK_VOLTAGE_NOW;
                    break;
                case ChargeUtils_TYPE_TEMP:
                    mFileName = MTK_TEMP;
                    break;
            }
        }
        String            changeVoltage     = null;
        FileInputStream   fileInputStream   = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader    br                = null;
        try {
            File voltageFilePath = new File(mFileName);
            if (voltageFilePath.exists()) {
                fileInputStream = new FileInputStream(voltageFilePath);
                inputStreamReader = new InputStreamReader(fileInputStream);
                br = new BufferedReader(inputStreamReader);
                String data;
                while ((data = br.readLine()) != null) {
                    changeVoltage = data;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (getCpu().contains("qcom")) {
            switch (type) {
                case ChargeUtils_TYPE_CURRENT:
                    changeVoltage = Math.abs(Integer.parseInt(changeVoltage) / 1000) + "";
                    break;
                case ChargeUtils_TYPE_VOLTAGE:
                    changeVoltage = Math.abs(Integer.parseInt(changeVoltage) / 1000) + "";
                    break;
                case ChargeUtils_TYPE_TEMP:
                    changeVoltage = Math.abs(Integer.parseInt(changeVoltage) / 10) + "";
                    break;
            }
        } else {
            switch (type) {
                case ChargeUtils_TYPE_TEMP:
                    changeVoltage = Math.abs(Integer.parseInt(changeVoltage) / 10) + "";
                    break;
            }
        }
        return changeVoltage;
    }

    //获得充电电流
    public static String getChargeCurrent() {
        String mFileName;
        if (getCpu().contains("qcom")) {
            mFileName = "/sys/class/power_supply/battery/current_now";
        } else {
            mFileName = "/sys/class/power_supply/battery/BatteryAverageCurrent";
        }
        String            chargeCurrent     = null;
        FileInputStream   fileInputStream   = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader    br                = null;
        try {
            File currentFilePath = new File(mFileName);
            if (currentFilePath.exists()) {
                fileInputStream = new FileInputStream(currentFilePath);
                inputStreamReader = new InputStreamReader(fileInputStream);
                br = new BufferedReader(inputStreamReader);
                String data;
                while ((data = br.readLine()) != null) {
                    chargeCurrent = data;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return chargeCurrent;
    }

    //获得库伦值
    public static String getCoulombCount() {
        String mFileName;
        if (getCpu().contains("qcom")) {
            mFileName = "/sys/class/power_supply/battery/coulomb_count";
        } else {
            mFileName = "/sys/class/power_supply/battery/coulomb_count";
        }
        String            chargeCurrent     = null;
        FileInputStream   fileInputStream   = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader    br                = null;
        try {
            File currentFilePath = new File(mFileName);
            if (currentFilePath.exists()) {
                fileInputStream = new FileInputStream(currentFilePath);
                inputStreamReader = new InputStreamReader(fileInputStream);
                br = new BufferedReader(inputStreamReader);
                String data;
                while ((data = br.readLine()) != null) {
                    chargeCurrent = data;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return chargeCurrent;
    }

    /**
     * @param
     * @param
     * @return
     * @Desc 读取文件内容
     */
    public static String readDataFromFile(String fileNameWithPath) {
        String result = null;
        try {
            File file = new File(fileNameWithPath);
            if (!file.exists()) {
//                Log.e(TAG, fileNameWithPath + " is not exist!");
                return null;
            }
            FileInputStream inputStream = new FileInputStream(file);
            byte[]          b           = new byte[inputStream.available()];
            inputStream.read(b);
            result = new String(b);
            inputStream.close();
        } catch (Exception e) {
//            Log.e(TAG, "[readDataFromFile] " + e.toString());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param
     * @param
     * @return
     * @Desc 覆盖方式将字符串写入到文本文件中
     */
    public static void writeTextToFileInCover(String filePath, String fileName, String text) {
        //生成文件夹之后，再生成文件，不然会出错
//        makeFilePath(filePath, fileName);
//
        String strFilePath = filePath + fileName;

        try {
            File             file = new File(strFilePath);
            FileOutputStream fos  = new FileOutputStream(file);
            fos.write(text.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public static void writeTextToFileInCover(String text) {
//        //生成文件夹之后，再生成文件，不然会出错
////        makeFilePath(filePath, fileName);
////        String strFilePath = filePath + fileName;
//        String mFileName;
//        if (getCpu().contains("qcom")) {
//            mFileName = "/sys/class/power_supply/battery/coulomb_count";
//        } else {
//            mFileName = "/sys/class/power_supply/battery/charging_enabled";
//        }
//        FileOutputStream fos = null;
//        try {
//            File file = new File(mFileName);
//            fos = new FileOutputStream(file);
//            fos.write(text.getBytes());
//            fos.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (fos != null) {
//                    fos.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }

    //获得文件中的内容
    public static String getTextcentent(String path) {
//        String mFileName;
//        if (getCpu().contains("qcom")) {
//            mFileName = "/sys/class/power_supply/battery/coulomb_count";
//        } else {
//            mFileName = "/sys/class/power_supply/battery/coulomb_count";
//        }
        String            chargeCurrent     = null;
        FileInputStream   fileInputStream   = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader    br                = null;
        try {
            File currentFilePath = new File(path);
            if (currentFilePath.exists()) {
                fileInputStream = new FileInputStream(currentFilePath);
                inputStreamReader = new InputStreamReader(fileInputStream);
                br = new BufferedReader(inputStreamReader);
                String data;
                while ((data = br.readLine()) != null) {
                    chargeCurrent = data;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return chargeCurrent;
    }

    /**
     * @return cup型号
     */
    private static String getCpu() {
        return getProperty("ro.hardware", "unknown");
    }


    /**
     * 反射机制获取或设置系统属性（SystemProperties）,从而获取手机版本
     *
     * @param key          对应的键
     * @param defaultValue 没有返回默认的值
     * @return
     */
    public static String getProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c   = Class.forName("android.os.SystemProperties");
            Method   get = c.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(c, key, "unknown"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return value;
        }
    }

    /**
     * public static final int  NODE_TYPE_LCM_ACL_BRIGHTNESS = 0;   //"/sys/class/leds/amoled_lcm_acl/brightness"
     * public static final int  NODE_TYPE_LCM_PARTICAL_BRIGHTNESS =1 ;  //"/sys/class/leds/amoled_lcm_partical/brightness"
     * public static final int  NODE_TYPE_LCM_HBM_BRIGHTNESS =2;    //"/sys/class/leds/amoled_lcm_hbm/brightness",
     * public static final int  NODE_TYPE_LED_RED_BRIGHTNESS=3; //"/sys/class/leds/red/brightness"
     * public static final int  NODE_TYPE_LED_BLUE_BRIGHTNESS=4;    //"/sys/class/leds/blue/brightness"
     * public static final int  NODE_TYPE_LED_GREEN_BRIGHTNESS=5;   ///sys/class/leds/green/brightness
     * public static final int  NODE_TYPE_LED_BUTTONBACKLIGHT_BRIGHTNESS=6; ///sys/class/leds/button-backlight/brightness
     * public static final int  NODE_TYPE_FLASHLIGHT_CAMERA_TORCH=7; ////sys/class/flashlightdrv/kd_camera_flashlight/torch
     * public static final int  NODE_TYPE_TPWAKESWITCH_DOUBLE_WAKE=8;   //"/sys/bus/platform/devices/tp_wake_switch/double_wake
     * public static final int  NODE_TYPE_TPWAKESWITCH_GLOVE_ENABLE=9;  ///sys/bus/platform/devices/tp_wake_switch/glove_enable
     * public static final int  NODE_TYPE_TPWAKESWITCH_GESTURE_WAKE=10; ///sys/bus/platform/devices/tp_wake_switch/gesture_wake
     * public static final int  NODE_TYPE_TPWAKESWITCH_GESTURE_CONFIG=11; ///sys/bus/platform/devices/tp_wake_switch/gesture_config
     * public static final int  NODE_TYPE_TPWAKESWITCH_FACTORY_CHECK=12;    ///sys/bus/platform/devices/tp_wake_switch/factory_check
     * public static final int  NODE_TYPE_GSENSRO_CALI=13;  ///sys/bus/platform/drivers/gsensor/cali
     * public static final int  NODE_TYPE_ALSPS_HIGH_THRESHOLD=14; ///sys/bus/platform/drivers/als_ps/high_threshold
     * public static final int  NODE_TYPE_ALSPS_LOW_THRESHOLD=15;   ///sys/bus/platform/drivers/als_ps/high_threshold
     * public static final int  NODE_TYPE_ALSPS_PDATA=16;   ///sys/bus/platform/drivers/als_ps/low_threshold
     * public static final int  NODE_TYPE_ALSPS_INCALL_CALI=17; ///sys/class/misc/m_alsps_misc/handanswer_status
     * public static final int  NODE_TYPE_INPUTX_BATCH=18;  ///sys/class/input/inputx/batch
     * public static final int  NODE_TYPE_INPUTX_DELAYMS=19;    ///sys/class/input/inputx/delay_ms
     * public static final int  NODE_TYPE_INPUTX_ENABLE=20; ///sys/class/input/inputx/enable
     * public static final int  NODE_TYPE_INPUTX_FLUSH=21;  ///sys/class/input/inputx/flush
     * public static final int  NODE_TYPE_BATTERY_AVERAGE_CURRENT=22;   ///sys/devices/platform/battery/power_supply/battery/BatteryAverageCurrent
     * public static final int  NODE_TYPE_BATTERY_CHARGE_VOLTAGE=23;    ///sys/devices/platform/battery/power_supply/battery/ChargerVoltage
     * public static final int  NODE_TYPE_BATTERY_BATT_TEMP=24; ///sys/devices/platform/battery/power_supply/battery/batt_temp
     * public static final int  NODE_TYPE_BATTERY_BATT_VOL=25;  ///sys/devices/platform/battery/power_supply/battery/batt_vol
     * public static final int  NODE_TYPE_BATTERY_CAPACITY=26;  ///sys/devices/platform/battery/power_supply/battery/capacity
     * public static final int  NODE_TYPE_BATTERY_HEALTH=27;    ///sys/devices/platform/battery/power_supply/battery/health
     * public static final int  NODE_TYPE_BATTERY_TECHNOLOGY=28;    ///sys/devices/platform/battery/power_supply/battery/technology
     * public static final int  NODE_TYPE_BATTERY_NOTIFY=29;    ///sys/devices/platform/mt-battery/BatteryNotify
     * public static final int  NODE_TYPE_VIBRATOR_ENABLE=30;   ///sys/class/timed_output/vibrator/enable
     * public static final int  NODE_TYPE_VIBRATOR_VIBR_ON=31;  ///sys/class/timed_output/vibrator/vibr_on
     * public static final int  NODE_TYPE_VIBRATOR_PATTERN=32;  ///sys/class/timed_output/vibrator/pattern
     * public static final int  NODE_TYPE_HALL_SWITCH_STATE=33; ///sys/devices/virtual/switch/gn_hall_key/state
     * public static final int  NODE_TYPE_ACCDET_HEADSET_MODE=34;   ///sys/bus/platform/drivers/Accdet_Driver/set_headset_mode
     * public static final int  NODE_TYPE_GN_DEVICE_CHECK=35;   ///sys/devices/platform/gn_device_check
     * <p>
     * public static final int  NODE_TYPE_TPWAKESWITCH_GESTURE_CONTENT = 36;///sys/bus/platform/devices/tp_wake_switch/gesture_coordition
     * public static final int  NODE_TYPE_GN_DEVICE_GET = 37;  ///sys/devices/platform/gn_device_check/name
     * public static final int  NODE_TYPE_MISC_STATUS_GET = 38; ///sys/devices/virtual/misc/x_misc/x_misc/status
     * public static final int  NODE_TYPE_FLASHLIGHT_CAMERA_TORCH_0 = 39; ///sys/class/flashlightdrv/kd_camera_flashlight/torch0
     * public static final int  NODE_TYPE_FLASHLIGHT_CAMERA_TORCH_1 = 40; ///sys/class/flashlightdrv/kd_camera_flashlight/torch1
     * //Gionee <Amigo_Skylight> wangym <20150409> add for  CR01462639 begin
     * public static final int  NODE_TYPE_HALL_SWITCH_ON = 41;///sys/devices/platform/gn_hall_key/state
     * //Gionee <Amigo_Skylight> wangym <20150409> add for  CR01462639 end
     * //Gionee <bug> <wangym> <20150530> add for  CR01492068 begin
     * public static final int  NODE_TYPE_CALL_CHARGING_SWITCH_ON = 42;///sys/devices/platform/battery/Charging_CallState
     * //Gionee <bug> <wangym> add for  CR01492068 end
     * //Gionee <fingerprint> <qudw> <20150528> modify for CR01490911
     * public static final int NODE_TYPE_FINGERPRINT_INPUTMODE_SWITCH = 43; ///sys/bus/spi/devices/spi0.0/diag/nav_en
     * //Gionee <qudw> <fingerprint> <20150608> add for CR01498174 begin
     * public static final int NODE_TYPE_FINGERPRINT_INTERRUPT_SETUP_DST_PID = 44; ///sys/bus/platform/devices/gn_fpc1020/setup/dst_pid
     * public static final int NODE_TYPE_FINGERPRINT_INTERRUPT_SETUP_DST_SIGNO = 45; ///sys/bus/platform/devices/gn_fpc1020/setup/dst_signo
     * public static final int NODE_TYPE_FINGERPRINT_INTERRUPT_SETUP_ENABLED= 46; ///sys/bus/platform/devices/gn_fpc1020/setup/enabled
     * //Gionee <qudw> <fingerprint> <20150608> add for CR01498174 end
     * <p>
     * //Gionee <qudw> <fingerprint> <20150709> add for CR01516884 begin
     * public static final int NODE_TYPE_FINGERPRINT_INTERRUPT_PM_HW_RESET = 47; ///sys/bus/platform/devices/gn_fpc1020/pm/hw_reset
     * public static final int NODE_TYPE_FINGERPRINT_INTERRUPT_PM_NOTIFY_ACK = 48; ///sys/bus/platform/devices/gn_fpc1020/pm/notify_ack
     * public static final int NODE_TYPE_FINGERPRINT_INTERRUPT_PM_NOTIFY_ENABLED = 49; ///sys/bus/platform/devices/gn_fpc1020/pm/notify_enabled
     * public static final int NODE_TYPE_FINGERPRINT_INTERRUPT_PM_STATE = 50; ///sys/bus/platform/devices/gn_fpc1020/pm/state
     * public static final int NODE_TYPE_FINGERPRINT_INTERRUPT_PM_SUPPLY_ON = 51; ///sys/bus/platform/devices/gn_fpc1020/pm/supply_on
     * public static final int NODE_TYPE_FINGERPRINT_INTERRUPT_PM_WAKEUP_REQ = 52; ///sys/bus/platform/devices/gn_fpc1020/pm/wakeup_req
     * //Gionee <qudw> <fingerprint> <20150709> add for CR01516884 end
     * <p>
     * //Gionee <wangh> <fingerprint> <20150725> add for CR01467156 begin
     * public static final int NODE_TYPE_FINGERPRINT_INTERRUPT_SPICLK_ENABLE = 53; ///sys/bus/platform/devices/gn_fpc1020/spiclk/enable
     * public static final int NODE_TYPE_FINGERPRINT_INTERRUPT_FINGERID_HWID = 54; ///sys/bus/platform/devices/gn_fpc1020/fingerid/hwid
     * //Gionee <wangh> <fingerprint> <20150725> add for CR01467156 end
     * //Gionee <Amigo_ForceTouch> <wangym>  <20151229> add for force_touch begin
     * public static final int NODE_TYPE_FORCE_TOUCH_SUPPORT = 55;                  ///sys/bus/platform/devices/tp_wake_switch/force_touch_support
     * public static final int NODE_TYPE_FORCE_TOUCH_ENABLE = 56;                 // /sys/bus/platform/devices/tp_wake_switch/force_touch_enable
     * public static final int NODE_TYPE_FORCE_TOUCH_LOW_THRE = 57;                ///sys/bus/platform/devices/tp_wake_switch/force_touch_low_threshold
     * public static final int NODE_TYPE_FORCE_TOUCH_HIGH_THRE = 58;               ///sys/bus/platform/devices/tp_wake_switch/force_touch_high_threshold
     * public static final int NODE_TYPE_FORCE_TOUCH_THRESHOLD = 59;               ///sys/bus/platform/devices/tp_wake_switch/touch_threshold
     * public static final int NODE_TYPE_LEFT_HAND_MODE = 60;                              ///sys/bus/platform/devices/tp_wake_switch/left_hand_mode
     * //Gionee <Amigo_ForceTouch> <wangym> <20151229> add for force_touch end
     * //Gionee <Amigo_UI> <guozj> <20160412> add for CR01674119 begin
     * public static final int NODE_TYPE_TP_NAV_MODE = 61;							///sys/bus/platform/devices/tp_wake_switch/nav_mode
     * //Gionee <Amigo_UI> <guozj> <20160412> add for CR01674119 end
     * //Gionee <Amigo_UI> <guozj> <20161104> add for 19067 begin
     * public static final int NODE_TYPE_TP_NAV_MODE_ENABLE = 62;							///sys/devices/platform/tp_wake_switch/nav_enabe
     * //Gionee <Amigo_UI> <guozj> <20161104> add for 19067 end
     * //Gionee <Amigo_UI> <wangym> <20161122> add for 31213 begin
     * public static final int NODE_TYPE_OTG_CHARGE_SWITCH = 63;							///sys/devices/platform/battery/Otg_Charge_Switch
     * //Gionee <Amigo_UI> <wangym> <20161122> add for 31213 end
     * //Gionee <GN_BSP_BATTERY> <zhangke> <20161227> add for ID55882 begin
     * public static final int NODE_TYPE_BATTERY_CHARGING_ENABLED = 64; ///sys/class/power_supply/battery/charging_enabled
     * //Gionee <GN_BSP_BATTERY> <zhangke> <20161227> add for ID55882 end
     * //Gionee <GN_Oversea_FAST_CHARGE> <taofp> <20170107> add for 59089 begin
     * public static final int NODE_TYPE_FAST_CHARGE = 65;                         ///sys/devices/platform/battery/Fast_Charge_Switch
     * //Gionee <GN_Oversea_FAST_CHARGE> <taofp> <20170107> add for 59089 end
     * //Gionee <Amigo_FingerPrint> <guozj> <20170311> add for 82430 begin
     * public static final int NODE_TYPE_SOLID_FINGERPRINT = 66;					///sys/devices/platform/gn_fpr_node/solid_fingerprint
     * //Gionee <Amigo_FingerPrint> <guozj> <20170311> add for 82430 end
     * //Gionee <APP_Sipper> <liumeixia> <20170320> add for 73249 begin
     * public static final int NODE_TYPE_BATTERY_SPECIFIC_SDP_ONLINE = 67; ///sys/class/power_supply/battery/specific_sdp_online
     * //Gionee <APP_Sipper> <liumeixia> <20170320> add for 73249 end
     * <p>
     * // Gionee <Amigo_UI> <fengxb> <20170510> add for 103178 begin
     * public static final int NODE_TYPE_SENSITIVITY_SWITCH_SUPPORT = 68;   // /sys/devices/platform/tp_wake_switch/sensitivity_switch_support
     * public static final int NODE_TYPE_TP_SENSITIVITY_SWITCH = 69;    // /sys/devices/platform/tp_wake_switch/tp_sensitivity_switch
     * // Gionee <Amigo_UI> <fengxb> <20170510> add for 103178 end
     * //Gionee <APP_Sipper> <liumeixia> <20170519> add for 73249 begin
     * public static final int NODE_TYPE_BATTERY_EN_SAFETY_TIMER = 70; ///sys/class/power_supply/battery/en_safety_timer
     * //Gionee <APP_Sipper> <liumeixia> <20170519> add for 73249 end
     * //Gionee <GN_BSP_BATTERY> <zhangke> <20170524> add for ID148305 begin
     * public static final int NODE_TYPE_BATTERY_MMI_STATUS = 71; ///sys/class/power_supply/battery/mmi_status
     * //Gionee <GN_BSP_BATTERY> <zhangke> <20170524> add for ID148305 end
     */

    public static boolean writeNodeState(Context context, String nodeType, int value) {
        Object pm = (Object) (context.getSystemService("amigoserver"));
        try {
            Class<?> cls    = Class.forName("android.os.amigoserver.AmigoServerManager");
            Method   method = cls.getMethod("SetNodeState", int.class, int.class);
            Field    f      = cls.getField(nodeType);
            method.invoke(pm, f.get(null), value);
//            Util.log(UTIL_TAG, "writeGestureNodeValue " + nodeType + " " + f.get(null) + ":" + value);
            return true;
        } catch (Exception e) {
//            Util.log(UTIL_TAG, "Exception :" + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static int getNodeState(Context context, String nodeType) {
        Object pm = (Object) (context.getSystemService("amigoserver"));
        try {
            Class<?> cls    = Class.forName("android.os.amigoserver.AmigoServerManager");
            Method   method = cls.getMethod("GetNodeState", int.class);
            Field    f      = cls.getField(nodeType);
            int      value  = (Integer) method.invoke(pm, f.get(null));
//            Util.log(UTIL_TAG, "getNodeValue " + nodeType + " " + f.get(null) + ":" + value);
            return value;
        } catch (Exception e) {
//            Util.log(UTIL_TAG, "Exception :" + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public static void setChargeEnable(Context context, boolean isEnabled) {
        writeNodeState(context, "NODE_TYPE_BATTERY_CHARGING_ENABLED", isEnabled ? 1 : 0);
    }

}

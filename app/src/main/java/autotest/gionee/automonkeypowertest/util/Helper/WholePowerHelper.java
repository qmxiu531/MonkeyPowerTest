package autotest.gionee.automonkeypowertest.util.Helper;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import autotest.gionee.automonkeypowertest.util.Util;

public class WholePowerHelper {
    private double power_start = 0;
    private double power_end   = 0;
    private ArrayList<Double> powerList;
    private boolean isRecording = false;
    private Context     mContext;
    private USBReceiver mReceiver;
    private        boolean isConnectUsb = false;
    private static boolean isNewCoulomb = !android.os.Build.MODEL.contains("S10|S10C");

    public WholePowerHelper(Context context) {
        this.mContext = context;
    }

    public void startRecordPower() {
        power_start = getCoulomb_count();
        Util.i("power_start:" + power_start);
//        isRecording = true;
//        new GetCoulombTask().start();
//        regReceiver();
    }

    private void stopRecordPower() {
        power_end = getCoulomb_count();
        Util.i("power_end:" + power_end);
//        Util.i("power_end=" + power_end);
//        isRecording = false;
//        unRegReceiver();
    }

    public double getPowerMah() {
        stopRecordPower();
        double gap = 0;
        if (power_end != 0) {
            gap = power_end - power_start;
        }
        if (powerList != null && !powerList.isEmpty()) {
            for (double integer : powerList) {
                gap += integer;
                Util.i("item=" + integer);
            }
            powerList.clear();
            powerList = null;
        }
        return Math.abs(gap);
    }

    public static double getCoulomb_count() {
        File   file          = new File("sys/class/power_supply/battery/coulomb_count");
        double coulomb_count = 0;
        if (!file.exists()) {
            return coulomb_count;
        }
        try {
            FileReader     fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String         s;
            while ((s = br.readLine()) != null) {
                if (!"".equals(s))
                    coulomb_count = Math.abs(Double.parseDouble(s));
            }
            fr.close();
            br.close();
        } catch (Exception e) {
            Util.i(e.toString());
        }
        return isNewCoulomb ? coulomb_count / 10 : coulomb_count;
    }

    private class GetCoulombTask extends Thread {
        private double mTemp = 0;

        @Override
        public void run() {
            while (isRecording) {
                double coulomb_count = getCoulomb_count();
                if (coulomb_count == 0.0 || isConnectUsb) {
//                    Util.i("usb插入");
                    if (mTemp != 0) {
                        double value = mTemp - power_start;
                        if (powerList == null) {
                            powerList = new ArrayList<>();
                        }
//                        Util.i("上一次为" + value);
                        powerList.add(value);
                        power_start = coulomb_count;
                    }
                }
                mTemp = coulomb_count;
                SystemClock.sleep(100);
            }
        }
    }

    private void regReceiver() {
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(USBReceiver.ACTION_USB_STATE);
            mReceiver = new USBReceiver();
            mContext.registerReceiver(mReceiver, intentFilter);
        } catch (Exception e) {
            Util.i(e.toString());
        }
    }

    private void unRegReceiver() {
        if (mReceiver == null) {
            return;
        }
        mContext.unregisterReceiver(mReceiver);
    }

    class USBReceiver extends BroadcastReceiver {
        public final static String ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE";

        @Override
        public void onReceive(Context mContext, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;
            switch (action) {
                case ACTION_USB_STATE:
                    Bundle extras = intent.getExtras();
                    if (extras != null) {
                        isConnectUsb = extras.getBoolean("connected");
                    }
                    break;
                default:
                    break;
            }
        }
    }
}

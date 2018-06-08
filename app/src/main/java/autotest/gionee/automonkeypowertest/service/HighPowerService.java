package autotest.gionee.automonkeypowertest.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.gson.Gson;

import java.util.ArrayList;

import autotest.gionee.automonkeypowertest.bean.PowerInfoObj;
import autotest.gionee.automonkeypowertest.util.Constants;
import autotest.gionee.automonkeypowertest.util.Preference;
import autotest.gionee.automonkeypowertest.util.YUtils;

/**
 * Created by xhk on 2018/1/29.
 */

public class HighPowerService extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        ArrayList<PowerInfoObj> startAllPower = YUtils.getAllPower(getApplicationContext(), 1);
        String                  json             = new Gson().toJson(startAllPower);
        Preference.putString(Constants.START_POWER,json);
        YUtils.setAlarm(getApplicationContext(),Constants.ELITOR_CLOCK,Constants.HIGH_POWER_TIME,1);
    }

    @Override
    public void onDestroy() {
        YUtils.cancelAlarm(getApplicationContext(),Constants.ELITOR_CLOCK);
    }
}

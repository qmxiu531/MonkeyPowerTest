package autotest.gionee.automonkeypowertest.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

import java.util.ArrayList;

import autotest.gionee.automonkeypowertest.bean.PowerInfoObj;
import autotest.gionee.automonkeypowertest.util.Constants;
import autotest.gionee.automonkeypowertest.util.Preference;
import autotest.gionee.automonkeypowertest.util.YUtils;

/**
 * Created by xhk on 2018/1/29.
 */

public class HighPowerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Constants.ELITOR_CLOCK)){
            ArrayList<PowerInfoObj> endAllPower = YUtils.getAllPower(context, 2);
            YUtils.setPowerData(endAllPower);
            ArrayList<PowerInfoObj> startAllPower = YUtils.getAllPower(context, 1);
            String                  json             = new Gson().toJson(startAllPower);
            Preference.putString(Constants.START_POWER,json);
            YUtils.setAlarm(context,Constants.ELITOR_CLOCK,Constants.HIGH_POWER_TIME,1);

        }

    }
}

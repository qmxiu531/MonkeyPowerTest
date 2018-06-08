package autotest.gionee.automonkeypowertest.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import autotest.gionee.automonkeypowertest.service.Monkey2Service;
import autotest.gionee.automonkeypowertest.util.Constants;
import autotest.gionee.automonkeypowertest.util.Preference;
import autotest.gionee.automonkeypowertest.util.Util;

public class TestReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case "autotest.gionee.automonkeypowertest.starttest":
                int times = intent.getIntExtra("times", 1);
                Preference.putLong(Constants.KEY_CYCLE_COUNT, times);
                Util.i("开始测试");
                Util.isTest = true;
                context.startService(new Intent(context, Monkey2Service.class));
                break;

            default:
                break;
        }
    }
}

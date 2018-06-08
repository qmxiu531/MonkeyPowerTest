package autotest.gionee.automonkeypowertest.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import autotest.gionee.automonkeypowertest.util.Constants;
import gionee.autotest.AccessUtil;

public class AccessReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Constants.ACTION_ACCESS_ENABLED:
                int start = intent.getIntExtra("start", 0);
                AccessUtil accessUtil = new AccessUtil(context);
                accessUtil.setServiceEnable(start == 1);
                break;

            default:
                break;
        }
    }
}

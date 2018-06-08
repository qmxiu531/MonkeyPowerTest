package autotest.gionee.automonkeypowertest.dialog;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

import autotest.gionee.automonkeypowertest.bean.SelectApp;
import autotest.gionee.automonkeypowertest.bean.SelectAppParams;
import autotest.gionee.automonkeypowertest.util.Helper.AppHelper;

public class SelectAppObtainer extends AsyncTask<Void, Void, SelectAppParams> {
    private Context context;

    protected SelectAppObtainer(Context context) {
        this.context = context;
    }

    @Override
    protected SelectAppParams doInBackground(Void... voids) {
        ArrayList<String> appNames   = new AppHelper(context).getAppNames();
        SelectApp         selectApp  = AppHelper.getSelectApp();
        boolean[]         selectItem = new boolean[appNames.size()];
        ArrayList<String> selectApps = selectApp.selectApps;
        for (int i = 0; i < selectItem.length; i++) {
            selectItem[i] = selectApps.contains(appNames.get(i));
        }
        return new SelectAppParams(appNames, selectItem);
    }
}
